package com.internship.picturesgallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    public final static int SPAN_PORTRAIT_QUANTITY = 4;
    private final static int SPAN_LANDSCAPE_QUANTITY = 2;
    private final static int PLACEHOLDER_ID = R.drawable.ic_placeholder;
    private final static int PERMISSION_REQUEST_CODE = 1;
    private final static Uri IMAGE_MEDIA_URI = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private final static String FULL_IMAGE_VIEW_INTENT_TYPE = "image/*";
    private final static String TAG_DIALOG = "OpenImageDialogFragment";

    private Disposable pathLoadingDisposable;
    private PicturesRecyclerAdapter picturesRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkForPermissions()) {
            Toast.makeText(this, getString(R.string.permissions_lack_toast), Toast.LENGTH_SHORT).show();
            finish();
        }

        final RecyclerView recyclerView = findViewById(R.id.pictures_recycler);

        picturesRecyclerAdapter
                = new PicturesRecyclerAdapter(loadImagesPathWithRx(), getScreenHeight(), getSpanCount());
        final RecyclerView.LayoutManager layoutManager
                = new GridLayoutManager(this, getSpanCount(), GridLayoutManager.HORIZONTAL, false);

        picturesRecyclerAdapter.setOnClickListener(getOnClickImageListener(picturesRecyclerAdapter.getPathList(), layoutManager));
        picturesRecyclerAdapter.setOnLongClickListener(getOnLongClickImageListener(picturesRecyclerAdapter.getPathList(), layoutManager));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new PictureItemDecorator());
        recyclerView.setAdapter(picturesRecyclerAdapter);
    }

    @Override
    protected void onDestroy() {
        if (pathLoadingDisposable != null && !pathLoadingDisposable.isDisposed())
            pathLoadingDisposable.dispose();
        super.onDestroy();
    }

    public static void picassoImageLoader(File sourceFile, ImageView imageView) {
        Picasso.get()
                .load(sourceFile)
                .placeholder(PLACEHOLDER_ID)
                .noFade()
                .into(imageView);
    }

    public static void picassoImageLoader(File sourceFile, ImageView imageView, int size) {
        Picasso.get()
                .load(sourceFile)
                .placeholder(PLACEHOLDER_ID)
                .resize(size, size)
                .centerCrop()
                .noFade()
                .into(imageView);
    }

    private boolean checkForPermissions() {
        if (isPermissionGranted()) return true;
        else ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);

        return isPermissionGranted();
    }

    private boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private int getScreenHeight() {
        final Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        return point.y;
    }

    private int getSpanCount() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ?
                SPAN_LANDSCAPE_QUANTITY : SPAN_PORTRAIT_QUANTITY;
    }

    private List<String> getAllShownImagesPath() {
        final String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.DISPLAY_NAME};
        final List<String> imagesPathList = new ArrayList<>();
        try (Cursor cursor = getContentResolver().query(IMAGE_MEDIA_URI, projection, null,
                null, null)) {
            final int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            while (cursor.moveToNext()) {
                String absolutePathOfImage = cursor.getString(columnIndexData);
                imagesPathList.add(0, absolutePathOfImage);
            }
        }
        return imagesPathList;
    }

    private List<String> loadImagesPathWithRx() {
        if (pathLoadingDisposable != null && !pathLoadingDisposable.isDisposed())
            pathLoadingDisposable.dispose();
        final List<String> imagesPathList = new ArrayList<>();
        pathLoadingDisposable = Single.fromCallable(this::getAllShownImagesPath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loadedPathList -> {
                    imagesPathList.clear();
                    imagesPathList.addAll(loadedPathList);
                    picturesRecyclerAdapter.notifyDataSetChanged();
                });
        return imagesPathList;
    }

    private View.OnClickListener getOnClickImageListener(final List<String> imagesList, final RecyclerView.LayoutManager layoutManager) {
        return view -> {
            final Intent imageViewIntent = new Intent();
            imageViewIntent.setAction(Intent.ACTION_VIEW);
            imageViewIntent.setDataAndType(Uri.parse(getPathFromListByView(imagesList, layoutManager, view)), FULL_IMAGE_VIEW_INTENT_TYPE);
            startActivity(imageViewIntent);
        };
    }

    private View.OnLongClickListener getOnLongClickImageListener(final List<String> imagesList, final RecyclerView.LayoutManager layoutManager) {
        return view -> {
            final FullImageViewFragment fullImageViewFragment = FullImageViewFragment.newInstance(getPathFromListByView(imagesList, layoutManager, view));
            final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fullImageViewFragment.show(fragmentTransaction, TAG_DIALOG);
            return true;
        };
    }

    private String getPathFromListByView(final List<String> imagesList, final RecyclerView.LayoutManager layoutManager, View view) {
        return imagesList.get(layoutManager.getPosition(view));
    }
}
