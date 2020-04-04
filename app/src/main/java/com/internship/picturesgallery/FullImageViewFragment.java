package com.internship.picturesgallery;

import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;

public class FullImageViewFragment extends AppCompatDialogFragment {
    private static final String IMAGE_PATH_PARAMETER = "path";

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

        File picturePathFile;
        if (getArguments() != null && getArguments().getString(IMAGE_PATH_PARAMETER) != null) {
            picturePathFile = new File(getArguments().getString(IMAGE_PATH_PARAMETER));
        } else {
            picturePathFile = null;
            Toast.makeText(getContext(), getString(R.string.image_loading_error), Toast.LENGTH_SHORT).show();
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int newLayoutWidth, newLayoutHeight;
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            newLayoutWidth = 0;
            newLayoutHeight = displayMetrics.heightPixels * 4 / 5;
        } else {
            newLayoutWidth = displayMetrics.widthPixels * 7 / 8;
            newLayoutHeight = 0;
        }
        ImageView imageView = view.findViewById(R.id.fragment_picture_element);
        picassoImageLoader(picturePathFile, imageView, newLayoutWidth, newLayoutHeight);
        imageView.setOnClickListener(v -> dismiss());
        return builder.setView(view).create();
    }

    private void picassoImageLoader(File sourceFile, ImageView imageView, int layoutWidth, int layoutHeight) {
        Picasso.get()
                .load(sourceFile)
                .placeholder(R.drawable.ic_placeholder)
                .resize(layoutWidth, layoutHeight)
                .into(imageView);
    }
}
