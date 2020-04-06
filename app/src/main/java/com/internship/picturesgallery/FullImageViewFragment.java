package com.internship.picturesgallery;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;

public class FullImageViewFragment extends AppCompatDialogFragment {
    private static final String IMAGE_PATH_PARAMETER = "path";
    private File picturePathFile;
    private ImageView imageView;

    public static FullImageViewFragment newInstance(String picturePathParameter) {
        FullImageViewFragment fragment = new FullImageViewFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE_PATH_PARAMETER, picturePathParameter);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_full_image_view, null);

        if (getArguments() != null && getArguments().getString(IMAGE_PATH_PARAMETER) != null) {
            picturePathFile = new File(getArguments().getString(IMAGE_PATH_PARAMETER));
        } else {
            Toast.makeText(getContext(), getString(R.string.image_loading_error), Toast.LENGTH_SHORT).show();
            dismiss();
        }

        imageView = view.findViewById(R.id.fragment_picture_element);
        imageView.setOnClickListener(v -> dismiss());
        return builder.setView(view).create();
    }

    @Override
    public void onStart() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int newLayoutHeight = displayMetrics.heightPixels * getResources().getInteger(R.integer.fragment_scale_height) / 100;
        int newLayoutWidth = displayMetrics.widthPixels * getResources().getInteger(R.integer.fragment_scale_width) / 100;
        picassoImageLoader(picturePathFile, imageView, newLayoutWidth, newLayoutHeight);
        super.onStart();
    }

    private void picassoImageLoader(File sourceFile, ImageView imageView, int layoutWidth, int layoutHeight) {
        Picasso.get()
                .load(sourceFile)
                .placeholder(R.drawable.ic_placeholder)
                .resize(layoutWidth, layoutHeight)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        try {
                            getDialog().getWindow().setLayout(bitmap.getWidth(), bitmap.getHeight());
                        }finally {
                            imageView.setLayoutParams(new LinearLayout.LayoutParams(bitmap.getWidth(), bitmap.getHeight()));
                            imageView.requestLayout();
                            imageView.setImageBitmap(bitmap);
                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        imageView.setImageDrawable(errorDrawable);
                        Log.e(Constants.LOADING_ERROR_TAG, e.toString());
                        dismiss();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        imageView.setImageDrawable(placeHolderDrawable);
                    }
                });
    }
}
