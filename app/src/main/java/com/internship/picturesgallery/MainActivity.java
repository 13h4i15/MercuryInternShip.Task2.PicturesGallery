package com.internship.picturesgallery;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private final static int PERMISSION_REQUEST_CODE = 1;
    private final static String RECYCLER_STATE_EXTRA = "recyclerState";
    private final static String TAG_DIALOG = "OpenImageDialogFragment";

    private Disposable pathLoadingDisposable;
    private PicturesRecyclerAdapter picturesRecyclerAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.pictures_recycler);

        picturesRecyclerAdapter = new PicturesRecyclerAdapter();

        int spanCount = getResources().getInteger(R.integer.span_count);
        RecyclerView.LayoutManager layoutManager
                = new GridLayoutManager(this, spanCount, GridLayoutManager.HORIZONTAL, false);

        if (savedInstanceState == null && checkForPermissions()) {
            loadImagesPathWithRxInAdapter(null);
        }

        picturesRecyclerAdapter.setOnClickListener(getOnClickImageListener());
        picturesRecyclerAdapter.setOnLongClickListener(getOnLongClickImageListener());
        picturesRecyclerAdapter.setHasStableIds(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new PictureItemDecorator(spanCount));
        recyclerView.setAdapter(picturesRecyclerAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadImagesPathWithRxInAdapter(null);
            } else {
                Toast.makeText(this, R.string.permissions_lack_toast, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (recyclerView.getLayoutManager() != null) {
            outState.putParcelable(RECYCLER_STATE_EXTRA, recyclerView.getLayoutManager().onSaveInstanceState());
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Parcelable recyclerState = savedInstanceState.getParcelable(RECYCLER_STATE_EXTRA);
        loadImagesPathWithRxInAdapter(recyclerState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        disposePathLoadingDisposable();
        super.onDestroy();
    }

    private boolean checkForPermissions() {
        if (!isPermissionGranted()) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    private boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private List<Uri> obtainAllShownImagesPath() {
        String[] projection = {MediaStore.Images.Media._ID};
        List<Uri> imagesPathList = new ArrayList<>();

        String folder = "%DCIM/Camera%";
        String[] whereArgs = new String[]{folder};
        String mediaColumn = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ? MediaStore.Images.Media.RELATIVE_PATH
                : MediaStore.Images.Media.DATA;
        String wherePath = mediaColumn + " LIKE ?";
        String whereType = MediaStore.Images.Media.MIME_TYPE + " LIKE 'image/%'";
        String where = wherePath + " AND " + whereType;

        try (Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                where, whereArgs, MediaStore.Images.Media.DATE_ADDED)) {
            int columnIndexId = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            while (cursor.moveToNext()) {
                long id = cursor.getLong(columnIndexId);
                Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                imagesPathList.add(0, uri);
            }
        }
        return imagesPathList;
    }

    private void loadImagesPathWithRxInAdapter(@Nullable Parcelable recyclerState) {
        disposePathLoadingDisposable();
        pathLoadingDisposable = Single.fromCallable(this::obtainAllShownImagesPath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loadedPathList -> {
                    picturesRecyclerAdapter.setPathList(loadedPathList);
                    if (recyclerView.getLayoutManager() != null && recyclerState != null) {
                        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerState);
                    }
                }, exception -> {
                    Log.e(Constants.LOADING_ERROR_TAG, exception.toString());
                    Toast.makeText(this, R.string.images_loading_error_toast, Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void disposePathLoadingDisposable() {
        if (pathLoadingDisposable != null && !pathLoadingDisposable.isDisposed()) {
            pathLoadingDisposable.dispose();
        }
    }

    private OnImageClickListener getOnClickImageListener() {
        return (sourceFile) -> {
            Intent imageViewIntent = new Intent();
            imageViewIntent.setAction(Intent.ACTION_VIEW);
            imageViewIntent.setDataAndType(sourceFile, "image/*");
            startActivity(imageViewIntent);
        };
    }

    private OnImageClickListener getOnLongClickImageListener() {
        return (sourceFile) -> {
            FullImageViewFragment fullImageViewFragment = FullImageViewFragment.newInstance(sourceFile);
            fullImageViewFragment.show(getSupportFragmentManager(), TAG_DIALOG);
        };
    }
}