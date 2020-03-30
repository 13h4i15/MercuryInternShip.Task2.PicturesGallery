package com.internship.picturesgallery;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
    private List<String> list;
    private int screenHeiht;
    View.OnClickListener onClickListener;

    public PicturesRecyclerAdapter(List<String> list, int screenHeiht) {
        this.list = list;
        this.screenHeiht = screenHeiht;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_item, parent, false);
        final RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        view.setOnClickListener(v -> onClickListener.onClick(v));
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        int size = screenHeiht/4;
        Drawable drawable =new ColorDrawable(ContextCompat.getColor(holder.image.getContext(), R.color.colorPrimaryDark));
        if (holder.image.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) size = screenHeiht/2;
        Picasso.get()
                .load(new File(list.get(position)))
                .placeholder(R.drawable.ic_placeholder)
                .resize(size, size)
                .noFade()
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

    public void setOnClickListener(View.OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    public List<String> getList() {
        return list;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private final ImageView image;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.picture_element);
        }
    }
}
