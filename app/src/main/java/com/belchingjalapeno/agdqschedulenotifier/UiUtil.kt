package com.belchingjalapeno.agdqschedulenotifier

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

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
