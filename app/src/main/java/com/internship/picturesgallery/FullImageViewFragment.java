package com.internship.picturesgallery;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

public class FullImageViewFragment extends AppCompatDialogFragment {
    private final static String IMAGE_PATH_PARAMETER = "path";
    private final static ColorDrawable TRANSPARENT_BACKGROUND = new ColorDrawable(Color.TRANSPARENT);

    public static FullImageViewFragment newInstance(@NonNull Uri picturePathParameter) {
        FullImageViewFragment fragment = new FullImageViewFragment();
        Bundle args = new Bundle();
        args.putParcelable(IMAGE_PATH_PARAMETER, picturePathParameter);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        if (getActivity() == null || getArguments() == null || getArguments().getParcelable(IMAGE_PATH_PARAMETER) == null) {
            showToastAndDismiss(getString(R.string.dialog_loading_error));
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_full_image_view, null);

        ImageView imageView = view.findViewById(R.id.fragment_picture_element);
        imageView.setOnClickListener(v -> dismiss());
        Uri picturePathFile = getArguments().getParcelable(IMAGE_PATH_PARAMETER);
        picassoImageLoader(picturePathFile, imageView);
        return builder.setView(view).create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getDialog() == null || getDialog().getWindow() == null) {
            showToastAndDismiss(getString(R.string.dialog_loading_error));
        }
        getDialog().getWindow().setBackgroundDrawable(TRANSPARENT_BACKGROUND);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void picassoImageLoader(@NonNull Uri sourceFile, @NonNull ImageView imageView) {
        Picasso.get()
                .load(sourceFile)
                .placeholder(R.drawable.ic_placeholder)
                .fit()
                .centerInside()
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(imageView);
    }

    private void showToastAndDismiss(String toastText) {
        Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
