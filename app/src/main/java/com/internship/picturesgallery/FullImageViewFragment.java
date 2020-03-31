package com.internship.picturesgallery;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;

import java.io.File;

public class FullImageViewFragment extends AppCompatDialogFragment {
    private static final String IMAGE_PATH_PARAMETER = "url";

    private String picturePath;


    public static FullImageViewFragment newInstance(String pictureUrlParameter) {
        FullImageViewFragment fragment = new FullImageViewFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE_PATH_PARAMETER, pictureUrlParameter);
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

        if (getArguments() != null) {
            picturePath = getArguments().getString(IMAGE_PATH_PARAMETER);
        }

        MainActivity.picassoImageLoader(new File(picturePath), view.findViewById(R.id.fragment_picture_element));

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");

        builder.setView(view);
        return builder.create();
    }
}
