package com.internship.picturesgallery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

final class PicturesRecyclerAdapter extends RecyclerView.Adapter<PicturesRecyclerAdapter.RecyclerViewHolder> {
    private final List<String> list;
    private final int screenHeight, spanCount;
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;

    public PicturesRecyclerAdapter(List<String> list, int screenHeight, int spanCount) {
        this.list = list;
        this.screenHeight = screenHeight;
        this.spanCount = spanCount;
    }

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
        final int size = screenHeight / spanCount - ((8 + (spanCount != MainActivity.SPAN_PORTRAIT_QUANTITY ? 6 : 0)) *
                (int) holder.image.getResources().getDimension(R.dimen.picture_item_margin));
        MainActivity.picassoImageLoader(new File(list.get(position)), holder.image, size);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public List<String> getList() {
        return list;
    }

    public final static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.picture_element);
        }
    }
}
