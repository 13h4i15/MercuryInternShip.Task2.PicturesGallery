package com.internship.picturesgallery;

import android.view.View;

import java.io.File;

interface OnImageClickListener {
    void onClick(View view, File sourceFile);
}
