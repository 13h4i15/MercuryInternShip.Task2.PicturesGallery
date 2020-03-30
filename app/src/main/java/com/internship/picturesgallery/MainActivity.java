package com.internship.picturesgallery;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final static Uri image_media_uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);

        RecyclerView recyclerView = findViewById(R.id.pictures_recycler);
        final Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        int spanCount = 4;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 2;
        }
        List<String> list = getAllShownImagesPath();
        PicturesRecyclerAdapter picturesRecyclerAdapter = new PicturesRecyclerAdapter(list, point.y);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, spanCount, GridLayoutManager.HORIZONTAL, false);
        picturesRecyclerAdapter.setOnClickListener(getOnClickListenet(picturesRecyclerAdapter.getList(), layoutManager));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new PictureItemDecorator());
        recyclerView.setAdapter(picturesRecyclerAdapter);
    }


    private View.OnClickListener getOnClickListenet(final List<String> list, RecyclerView.LayoutManager layoutManager) {
        final Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        return v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(list.get(layoutManager.getPosition(v)) ), "image/*");
            startActivity(intent);
        };
    }


    private List<String> getAllShownImagesPath() {
        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.DISPLAY_NAME};

        try (Cursor cursor = getContentResolver().query(image_media_uri, projection, null,
                null, null)) {

            final int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            final List<String> imagesList = new ArrayList<>();
            while (cursor.moveToNext()) {
                String absolutePathOfImage = cursor.getString(column_index_data);
                imagesList.add(absolutePathOfImage);
            }
            return imagesList;
        } catch (Exception e) {
            return null;
        }
    }
}
