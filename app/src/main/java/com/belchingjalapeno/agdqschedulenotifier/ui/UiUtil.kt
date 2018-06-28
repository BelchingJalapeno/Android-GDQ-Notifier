package com.belchingjalapeno.agdqschedulenotifier.ui

import android.support.v4.graphics.ColorUtils
import android.view.View
import android.widget.ImageView

fun addColorAnimation(imageView: ImageView, startColor: Int, endColor: Int) {
    imageView.animate().setUpdateListener { p0 ->
        if (p0 != null) {
            val percent = p0.currentPlayTime / p0.duration.toFloat()
            imageView.setColorFilter(ColorUtils.blendARGB(startColor, endColor, clamp(percent)))
        }
    }
}

fun addAlphaAnimation(imageView: View, endAlpha: Float) {
    imageView.animate().alpha(endAlpha)
}

private fun clamp(value: Float, min: Float = 0.0f, max: Float = 1.0f): Float {
    return Math.max(Math.min(value, max), min)
}