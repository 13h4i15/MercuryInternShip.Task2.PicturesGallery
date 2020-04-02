package com.internship.picturesgallery;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class FullImageViewFragment extends AppCompatDialogFragment {
    private static final String IMAGE_PATH_PARAMETER = "url";

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

        final File picturePathFile;
        if (getArguments() != null && getArguments().getString(IMAGE_PATH_PARAMETER) != null) {
            picturePathFile = new File(getArguments().getString(IMAGE_PATH_PARAMETER));
        } else {
            picturePathFile = null;
            Toast.makeText(getContext(), getString(R.string.image_loading_error), Toast.LENGTH_SHORT).show();
        }

        ImageView pictureView = view.findViewById(R.id.fragment_picture_element);
        MainActivity.picassoImageLoader(picturePathFile, pictureView);
        pictureView.setOnClickListener(v -> dismiss());

        return builder.setView(view).create();
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        if(window == null) return;
        WindowManager.LayoutParams lp = window.getAttributes();
    }
}
