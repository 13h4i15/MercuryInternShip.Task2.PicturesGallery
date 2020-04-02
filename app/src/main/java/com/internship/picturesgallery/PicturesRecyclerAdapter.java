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
    private final List<String> pathList = new ArrayList<>();
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_item, parent, false);
        final RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        view.setOnClickListener(onClickListener);
        view.setOnLongClickListener(onLongClickListener);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        MainActivity.picassoImageLoader(new File(pathList.get(position)), holder.image);
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

    public List<String> getPathList() {
        return pathList;
    }

    public void setPathList(List<String> pathList) {
        this.pathList.clear();
        this.pathList.addAll(pathList);
        notifyDataSetChanged();
    }

    public final static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.picture_element);
        }
    }
}
