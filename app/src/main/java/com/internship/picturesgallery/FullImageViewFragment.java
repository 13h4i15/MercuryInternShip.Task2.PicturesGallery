package com.internship.picturesgallery;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class FullImageViewFragment extends AppCompatDialogFragment {
    private final static String IMAGE_PATH_PARAMETER = "path";

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

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_full_image_view, null);

        ImageView imageView = view.findViewById(R.id.fragment_picture_element);
        imageView.setOnClickListener(v -> dismiss());
        Uri picturePathFile = requireArguments().getParcelable(IMAGE_PATH_PARAMETER);
        picassoImageLoader(picturePathFile, imageView);
        Dialog dialog = builder.setView(view).create();
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.transparent_background));
        return dialog;
    }

    private void picassoImageLoader(Uri sourceFile, @NonNull ImageView imageView) {
        Picasso.get()
                .load(sourceFile)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .fit()
                .centerInside()
                .into(imageView);
    }
}
