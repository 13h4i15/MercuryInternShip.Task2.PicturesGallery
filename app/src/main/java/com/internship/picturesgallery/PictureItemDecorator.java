package com.internship.picturesgallery;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

final class PictureItemDecorator extends RecyclerView.ItemDecoration {
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int dimen = (int) view.getResources().getDimension(R.dimen.picture_item_margin);
        outRect.set(dimen, dimen, dimen, dimen);
    }
}
