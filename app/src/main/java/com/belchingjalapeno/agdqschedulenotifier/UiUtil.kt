package com.belchingjalapeno.agdqschedulenotifier

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.support.v4.graphics.ColorUtils
import android.view.View
import android.widget.ImageView

//https://stackoverflow.com/questions/4043398/animate-visibility-modes-gone-and-visible
fun animateViewVisibility(view: View, visibility: Int) {
    // cancel runnning animations and remove and listeners
    view.animate().cancel()
    view.animate().setListener(null)

    // animate making view visible
    if (visibility == View.VISIBLE) {
        view.animate().alpha(1f).start()
        view.visibility = View.VISIBLE
    } else {
        view.animate().setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                view.visibility = visibility
            }
        }).alpha(0f).start()
    }// animate making view hidden (HIDDEN or INVISIBLE)
}

fun addColorAnimation(imageView: ImageView, startColor: Int, endColor: Int) {
    imageView.animate().setUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
        override fun onAnimationUpdate(p0: ValueAnimator?) {
            val percent = p0!!.currentPlayTime / p0!!.duration.toFloat()
            imageView.setColorFilter(ColorUtils.blendARGB(startColor, endColor, clamp(percent)))
        }
    })
}

fun addAlphaAnimation(imageView: View, endAlpha: Float) {
    imageView.animate().alpha(endAlpha)
}

private fun clamp(value: Float, min: Float = 0.0f, max: Float = 1.0f): Float {
    return Math.max(Math.min(value, max), min)
}