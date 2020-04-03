package com.internship.picturesgallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

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
    private final static int FOLDER_REQUEST_CODE = 2;
    private final static String FULL_IMAGE_VIEW_INTENT_TYPE = "image/*";
    private final static String SCROLL_TO_EXTRA = "position";
    private final static String FOLDER_PATH_EXTRA = "folder";
    private final static String TAG_DIALOG = "OpenImageDialogFragment";
    private final static String LOADING_ERROR_TAG = "OpenImageDialogFragment";

    private Disposable pathLoadingDisposable;
    private PicturesRecyclerAdapter picturesRecyclerAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private String folderPath;
    private int firstVisiblePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView recyclerView = findViewById(R.id.pictures_recycler);

        picturesRecyclerAdapter
                = new PicturesRecyclerAdapter();

        layoutManager
                = new GridLayoutManager(this, getResources().getInteger(R.integer.span_count), GridLayoutManager.HORIZONTAL, false);

        firstVisiblePosition = 0;
        if (savedInstanceState != null) {
            folderPath = savedInstanceState.getString(FOLDER_PATH_EXTRA);
            firstVisiblePosition = savedInstanceState.getInt(SCROLL_TO_EXTRA);
            loadImagesPathWithRxInAdapter(folderPath, firstVisiblePosition);
        } else if (checkForPermissions()) getFolderPath();

        picturesRecyclerAdapter.setOnClickListener(getOnClickImageListener());
        picturesRecyclerAdapter.setOnLongClickListener(getOnLongClickImageListener());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new PictureItemDecorator());
        recyclerView.setAdapter(picturesRecyclerAdapter);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getFolderPath();
        } else {
            Toast.makeText(this, getString(R.string.permissions_lack_toast), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case FOLDER_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    String path = data.getData().getPath();
                    String[] fileSplit = path.split(":");
                    path = Environment.getExternalStorageDirectory().getPath() + File.separator + fileSplit[fileSplit.length - 1];
                    folderPath = path;
                    loadImagesPathWithRxInAdapter(folderPath, firstVisiblePosition);
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(FOLDER_PATH_EXTRA, folderPath);
        outState.putInt(SCROLL_TO_EXTRA, picturesRecyclerAdapter.getLastVisiblePosition() - getResources().getInteger(R.integer.scroll_to_start_number));
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
                .placeholder(R.drawable.ic_placeholder)
                .fit()
                .centerCrop()
                .into(imageView);
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

    private List<File> getAllShownImagesPath(String filePath) {
        File[] fileArray = new File(filePath).listFiles();
        final List<File> imagesPathList = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\.jpg|\\.png|\\.gif");
        for (File i : fileArray) {
            Matcher matcher = pattern.matcher(i.toString());
            if (matcher.find()) {
                imagesPathList.add(0, i);
            }
        }
        return imagesPathList;
    }

    private void loadImagesPathWithRxInAdapter(String path, int firstVisiblePosition) {
        if (pathLoadingDisposable != null && !pathLoadingDisposable.isDisposed())
            pathLoadingDisposable.dispose();
        pathLoadingDisposable = Single.fromCallable(() -> getAllShownImagesPath(path))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loadedPathList -> {
                    try {
                        picturesRecyclerAdapter.setPathList(loadedPathList);
                        layoutManager.scrollToPosition(firstVisiblePosition);
                    } catch (Exception exception) {
                        Log.e(LOADING_ERROR_TAG, exception.toString());
                    }
                }, exception -> Log.e(LOADING_ERROR_TAG, exception.toString()));
    }

    private void getFolderPath() {
        final Intent getFolderIntent = new Intent();
        getFolderIntent.setAction(Intent.ACTION_OPEN_DOCUMENT_TREE);
        getFolderIntent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivityForResult(Intent.createChooser(getFolderIntent, getString(R.string.choose_folder_title)), FOLDER_REQUEST_CODE);
    }

    private View.OnClickListener getOnClickImageListener() {
        return view -> {
            final Intent imageViewIntent = new Intent();
            imageViewIntent.setAction(Intent.ACTION_VIEW);
            imageViewIntent.setDataAndType(Uri.parse(picturesRecyclerAdapter.getLastClickedImagePath().getPath()), FULL_IMAGE_VIEW_INTENT_TYPE);
            startActivity(imageViewIntent);
        };
    }

    private View.OnLongClickListener getOnLongClickImageListener() {
        return view -> {
            final FullImageViewFragment fullImageViewFragment = FullImageViewFragment.newInstance(picturesRecyclerAdapter.getLastClickedImagePath().getPath());
            fullImageViewFragment.show(getSupportFragmentManager(), TAG_DIALOG);
            return true;
        };
    }
}
