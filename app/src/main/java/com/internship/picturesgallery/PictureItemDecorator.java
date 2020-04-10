package com.internship.picturesgallery;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

final class PictureItemDecorator extends RecyclerView.ItemDecoration {
    private final int spanCount;

    public PictureItemDecorator(int spanCount) {
        this.spanCount = spanCount;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int dimen = view.getResources().getDimensionPixelSize(R.dimen.picture_item_margin);

        outRect.set(dimen, 0, 0, dimen);
        if (position < spanCount) outRect.left = 0;
        if (position % spanCount == spanCount - 1) outRect.bottom = 0;
    }
}

