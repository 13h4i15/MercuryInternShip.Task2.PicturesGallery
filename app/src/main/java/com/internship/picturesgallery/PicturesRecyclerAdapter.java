package com.internship.picturesgallery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

final class PicturesRecyclerAdapter extends RecyclerView.Adapter<PicturesRecyclerAdapter.RecyclerViewHolder> {
    private final List<File> pathList = new ArrayList<>();
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;
    private int lastClickedImagePosition;

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_item, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        view.setOnClickListener(v -> {
            lastClickedImagePosition = recyclerViewHolder.getAdapterPosition();
            onClickListener.onClick(v);
        });
        view.setOnLongClickListener(v -> {
            lastClickedImagePosition = recyclerViewHolder.getAdapterPosition();
            return onLongClickListener.onLongClick(v);
        });
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        picassoImageLoader(pathList.get(position), holder.image);
    }

    @Override
    public int getItemCount() {
        return pathList.size();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public void setPathList(List<File> pathList) {
        this.pathList.clear();
        this.pathList.addAll(pathList);
        notifyDataSetChanged();
    }

    public File getLastClickedImagePath() {
        try {
            return pathList.get(lastClickedImagePosition);
        } catch (IndexOutOfBoundsException ignore) {
            return null;
        }
    }

    private static void picassoImageLoader(File sourceFile, ImageView imageView) {
        Picasso.get()
                .load(sourceFile)
                .placeholder(R.drawable.ic_placeholder)
                .fit()
                .centerCrop()
                .into(imageView);
    }

    public final static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.picture_element);
        }
    }
}
