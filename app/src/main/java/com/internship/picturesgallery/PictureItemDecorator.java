package com.internship.picturesgallery;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

final class PictureItemDecorator extends RecyclerView.ItemDecoration {


    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int spanCount = parent.getResources().getInteger(R.integer.span_count);
        int position = parent.getChildAdapterPosition(view);
        int dimen = (int) view.getResources().getDimension(R.dimen.picture_item_margin);
        outRect.set(dimen, dimen, dimen, dimen);
        if (position < spanCount) {
            outRect.left = 0;
        } else if (position > parent.getChildCount() - spanCount) {
            outRect.right = 0;
        }

        if (position % spanCount == 0)
            outRect.top = 0;
        else if (position % spanCount == 3) outRect.bottom = 0;
    }
}

