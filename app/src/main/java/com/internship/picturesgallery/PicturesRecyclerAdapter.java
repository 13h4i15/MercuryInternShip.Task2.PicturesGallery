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
    private OnImageClickListener onClickListener, onLongClickListener;

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_item, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        view.setOnClickListener(v -> onClickListener.onClick(pathList.get(recyclerViewHolder.getLayoutPosition())));
        view.setOnLongClickListener(v -> {
            onLongClickListener.onClick(pathList.get(recyclerViewHolder.getLayoutPosition()));
            return true;
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

    public void setOnClickListener(@NonNull OnImageClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(@NonNull OnImageClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public void setPathList(@NonNull List<File> pathList) {
        this.pathList.clear();
        this.pathList.addAll(pathList);
        notifyDataSetChanged();
    }

    private static void picassoImageLoader(@NonNull File sourceFile, @NonNull ImageView imageView) {
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
