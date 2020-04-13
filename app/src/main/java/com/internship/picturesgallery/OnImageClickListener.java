package com.internship.picturesgallery;

import android.net.Uri;

import androidx.annotation.NonNull;

interface OnImageClickListener {
    void onClick(@NonNull Uri sourceFile);
}
