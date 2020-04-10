package com.internship.picturesgallery;

import androidx.annotation.NonNull;

import java.io.File;

interface OnImageClickListener {
    void onClick(@NonNull File sourceFile);
}
