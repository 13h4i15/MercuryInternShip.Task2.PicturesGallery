package com.internship.picturesgallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private final static int PERMISSION_REQUEST_CODE = 1;
    private final static String FULL_IMAGE_VIEW_INTENT_TYPE = "image/*";
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
                Toast.makeText(this, getString(R.string.permissions_lack_toast), Toast.LENGTH_SHORT).show();
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

    @Nullable
    private List<File> obtainAllShownImagesPath() {
        String filePath = "/storage/emulated/0/DCIM/Camera";
        Pattern pattern = Pattern.compile("\\.jpg|\\.png|\\.gif");
        File[] fileArray = new File(filePath).listFiles();
        if (fileArray == null) return null;
        List<File> imagesPathList = new ArrayList<>();
        for (File i : fileArray) {
            if (i == null) continue;
            Matcher matcher = pattern.matcher(i.toString());
            if (matcher.find()) imagesPathList.add(0, i);
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
            imageViewIntent.setDataAndType(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ?
                    FileProvider.getUriForFile(this, getPackageName() + ".provider", new File(sourceFile.getPath())) :
                    Uri.parse(sourceFile.getPath()), FULL_IMAGE_VIEW_INTENT_TYPE);
            imageViewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(imageViewIntent);
        };
    }

    private OnImageClickListener getOnLongClickImageListener() {
        return (sourceFile) -> {
            FullImageViewFragment fullImageViewFragment = FullImageViewFragment.newInstance(sourceFile.getPath());
            fullImageViewFragment.show(getSupportFragmentManager(), TAG_DIALOG);
        };
    }
}
