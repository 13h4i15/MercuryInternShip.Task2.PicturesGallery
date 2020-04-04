package com.internship.picturesgallery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        view.setOnClickListener(v -> onClickListener.onClick(v, pathList.get(recyclerViewHolder.getLayoutPosition())));
        view.setOnLongClickListener(v -> {
            onLongClickListener.onClick(v, pathList.get(recyclerViewHolder.getLayoutPosition()));
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

    public void setOnClickListener(OnImageClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(OnImageClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public void setPathList(List<File> pathList) {
        this.pathList.clear();
        this.pathList.addAll(pathList);
        notifyDataSetChanged();
    }

    private static void picassoImageLoader(File sourceFile, SquareImageView squareImageView) {
        Picasso.get()
                .load(sourceFile)
                .placeholder(R.drawable.ic_placeholder)
                .fit()
                .centerCrop()
                .into(squareImageView);
    }

    public final static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private final SquareImageView image;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.picture_element);
        }
    }
}
