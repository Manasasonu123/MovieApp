package com.example.movieapp.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.movieapp.R

@BindingAdapter("load")
fun ImageView.loadImage(url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(this.context)
            .load(url)
            .placeholder(R.drawable.baseline_image_search_24) // Add a placeholder
            .error(R.drawable.baseline_error_outline_24)      // Add an error fallback image
            .into(this)
    } else {
        setImageResource(R.drawable.baseline_image_search_24) // Show placeholder if URL is null
    }
}