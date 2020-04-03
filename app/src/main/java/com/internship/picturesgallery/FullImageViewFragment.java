package com.internship.picturesgallery;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

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

        ImageView pictureView = view.findViewById(R.id.fragment_picture_element);
        picassoImageLoader(picturePathFile, pictureView);
        pictureView.setOnClickListener(v -> dismiss());

        return builder.setView(view).create();
    }

    private static void picassoImageLoader(File sourceFile, ImageView imageView) {
        Picasso.get()
                .load(sourceFile)
                .placeholder(R.drawable.ic_placeholder)
                .into(imageView);
    }
}
