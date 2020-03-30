package com.internship.picturesgallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

class PicturesRecyclerAdapter extends RecyclerView.Adapter<PicturesRecyclerAdapter.RecyclerViewHolder> {

    int size = 10;
    public PicturesRecyclerAdapter() {

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
        ArrayList<Integer> list = new ArrayList<>();
        list.add(R.color.colorPrimaryDark);
        list.add(R.color.colorAccent);
        list.add(R.color.colorPrimary);
        holder.image.setBackgroundColor(ContextCompat.getColor(holder.image.getContext(), list.get(position%3)));
    }

    @Override
    public int getItemCount() {
        return size;
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
