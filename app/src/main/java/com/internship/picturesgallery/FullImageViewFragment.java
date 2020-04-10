package com.internship.picturesgallery;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;

public class FullImageViewFragment extends AppCompatDialogFragment {
    private static final String IMAGE_PATH_PARAMETER = "path";
    private static final ColorDrawable TRANSPARENT_BACKGROUND = new ColorDrawable(Color.TRANSPARENT);

    private File picturePathFile;
    private ImageView imageView;

    public static FullImageViewFragment newInstance(@NonNull String picturePathParameter) {
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
        if (getActivity() == null || getArguments() == null || getArguments().getString(IMAGE_PATH_PARAMETER) == null) {
            Toast.makeText(getContext(), R.string.dialog_loading_error, Toast.LENGTH_SHORT).show();
            dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_full_image_view, null);

        picturePathFile = new File(getArguments().getString(IMAGE_PATH_PARAMETER));

        imageView = view.findViewById(R.id.fragment_picture_element);
        imageView.setOnClickListener(v -> dismiss());
        return builder.setView(view).create();
    }

    @Override
    public void onStart() {
        if (getDialog() == null || getDialog().getWindow() == null) {
            Toast.makeText(getContext(), R.string.dialog_loading_error, Toast.LENGTH_SHORT).show();
            dismiss();
        }
        picassoImageLoader(picturePathFile, imageView);
        super.onStart();
    }

    private void picassoImageLoader(@NonNull File sourceFile, @NonNull ImageView imageView) {
        Picasso.get()
                .load(sourceFile)
                .placeholder(R.drawable.ic_placeholder)
                .fit()
                .centerInside()
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        getDialog().getWindow().setBackgroundDrawable(TRANSPARENT_BACKGROUND);
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getContext(), R.string.image_loading_error, Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                });
    }
}
