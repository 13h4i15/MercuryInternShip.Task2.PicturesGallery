package com.internship.picturesgallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
    private final static int FOLDER_REQUEST_CODE = 2;
    private final static String FULL_IMAGE_VIEW_INTENT_TYPE = "image/*";
    private final static String RECYCLER_STATE_EXTRA = "recyclerState";
    private final static String FOLDER_PATH_EXTRA = "folder";
    private final static String TAG_DIALOG = "OpenImageDialogFragment";
    private final static String LOADING_ERROR_TAG = "loadingDataError";
    private final static String PATH_RECEIVING_ERROR_TAG = "pathReceivingError";

    private Disposable pathLoadingDisposable;
    private PicturesRecyclerAdapter picturesRecyclerAdapter;
    private RecyclerView recyclerView;
    private String folderPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.pictures_recycler);

        picturesRecyclerAdapter = new PicturesRecyclerAdapter();

        int spanCount = getResources().getInteger(R.integer.span_count);
        RecyclerView.LayoutManager layoutManager
                = new GridLayoutManager(this, spanCount, GridLayoutManager.HORIZONTAL, false);

        if (savedInstanceState == null && checkForPermissions()) receiveFolderPath();

        picturesRecyclerAdapter.setOnClickListener(receiveOnClickImageListener());
        picturesRecyclerAdapter.setOnLongClickListener(receiveOnLongClickImageListener());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new PictureItemDecorator(spanCount));
        recyclerView.setAdapter(picturesRecyclerAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                receiveFolderPath();
            } else {
                Toast.makeText(this, getString(R.string.permissions_lack_toast), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case FOLDER_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri docUrt = DocumentsContract.buildDocumentUriUsingTree(
                                data.getData(), DocumentsContract.getTreeDocumentId(data.getData()));
                        folderPath = getFullPathFromDocumentUri(docUrt);
                        loadImagesPathWithRxInAdapter(folderPath, null);
                    } catch (NullPointerException nullPointerException) {
                        Log.e(PATH_RECEIVING_ERROR_TAG, nullPointerException.toString());
                        Toast.makeText(this, getString(R.string.images_loading_error_toast), Toast.LENGTH_SHORT).show();
                        receiveFolderPath();
                    }
                } else {
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RECYCLER_STATE_EXTRA, recyclerView.getLayoutManager().onSaveInstanceState());
        outState.putString(FOLDER_PATH_EXTRA, folderPath);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        folderPath = savedInstanceState.getString(FOLDER_PATH_EXTRA);
        Parcelable recyclerState = savedInstanceState.getParcelable(RECYCLER_STATE_EXTRA);
        loadImagesPathWithRxInAdapter(folderPath, recyclerState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        if (pathLoadingDisposable != null && !pathLoadingDisposable.isDisposed())
            pathLoadingDisposable.dispose();
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

    private String getFullPathFromDocumentUri(Uri uri) {
        String documentId = DocumentsContract.getDocumentId(uri);
        if (isExternalStorageDocument(uri)) {
            String[] splitDocumentId = documentId.split(File.pathSeparator);
            String documentType = splitDocumentId[0];
            if ("primary".equalsIgnoreCase(documentType)) {
                // You can pick only from device memory
                // Other cases are downloads folder or media documents
                try {
                    return Environment.getExternalStorageDirectory() + File.separator + splitDocumentId[1];
                } catch (IndexOutOfBoundsException ignore) {
                    return Environment.getExternalStorageDirectory() + File.separator;
                }
            }
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private List<File> obtainAllShownImagesPath(String filePath) throws NullPointerException {
        File[] fileArray = new File(filePath).listFiles();
        List<File> imagesPathList = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\.jpg|\\.png|\\.gif");
        for (File i : fileArray) {
            Matcher matcher = pattern.matcher(i.toString());
            if (matcher.find()) imagesPathList.add(0, i);
        }
        return imagesPathList;
    }

    private void loadImagesPathWithRxInAdapter(String path, Parcelable recyclerState) throws NullPointerException {
        if (pathLoadingDisposable != null && !pathLoadingDisposable.isDisposed())
            pathLoadingDisposable.dispose();
        pathLoadingDisposable = Single.fromCallable(() -> obtainAllShownImagesPath(path))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loadedPathList -> {
                    try {
                        picturesRecyclerAdapter.setPathList(loadedPathList);
                        if (recyclerState != null)
                            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerState);
                    } catch (Exception exception) {
                        Log.e(LOADING_ERROR_TAG, exception.toString());
                    }
                }, exception -> Log.e(LOADING_ERROR_TAG, exception.toString()));
    }

    private void receiveFolderPath() {
        Intent receiveFolderIntent = new Intent();
        receiveFolderIntent.setAction(Intent.ACTION_OPEN_DOCUMENT_TREE);
        receiveFolderIntent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivityForResult(Intent.createChooser(receiveFolderIntent, getString(R.string.choose_folder_title)), FOLDER_REQUEST_CODE);
    }

    private OnImageClickListener receiveOnClickImageListener() {
        return (view, sourceFile) -> {
            Intent imageViewIntent = new Intent();
            imageViewIntent.setAction(Intent.ACTION_VIEW);
            imageViewIntent.setDataAndType(Uri.parse(sourceFile.getPath()), FULL_IMAGE_VIEW_INTENT_TYPE);
            imageViewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(imageViewIntent);
        };
    }

    private OnImageClickListener receiveOnLongClickImageListener() {
        return (view, sourceFile) -> {
            FullImageViewFragment fullImageViewFragment = FullImageViewFragment.newInstance(sourceFile.getPath());
            fullImageViewFragment.show(getSupportFragmentManager(), TAG_DIALOG);
        };
    }
}
