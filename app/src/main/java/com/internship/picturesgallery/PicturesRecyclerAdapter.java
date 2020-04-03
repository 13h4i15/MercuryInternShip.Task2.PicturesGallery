package com.internship.picturesgallery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

final class PicturesRecyclerAdapter extends RecyclerView.Adapter<PicturesRecyclerAdapter.RecyclerViewHolder> {
    private final List<File> pathList = new ArrayList<>();
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;
    private int lastClickedImagePosition, lastVisiblePosition;

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_item, parent, false);
        final RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
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
        lastVisiblePosition = position > lastVisiblePosition ? position
                : position + holder.image.getContext().getResources().getInteger(R.integer.scroll_to_start_number) +
                holder.image.getContext().getResources().getInteger(R.integer.span_count);
        MainActivity.picassoImageLoader(pathList.get(position), holder.image);
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

    public int getLastVisiblePosition() {
        return lastVisiblePosition;
    }

    public final static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.picture_element);
        }
    }
}
