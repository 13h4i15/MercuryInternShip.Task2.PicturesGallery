package com.internship.picturesgallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class PicturesRecyclerAdapter extends RecyclerView.Adapter<PicturesRecyclerAdapter.RecyclerViewHolder> {
    List<String> list;
    int screenHeiht;

    public PicturesRecyclerAdapter(List<String> list, int screenHeiht) {
        this.list = list;
        this.screenHeiht = screenHeiht;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_item, parent, false);
        final RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        int size = screenHeiht/4;
        Picasso.get()
                .load(new File(list.get(position)))
                .resize(size, size)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private final ImageView image;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.picture_element);
        }
    }
}
