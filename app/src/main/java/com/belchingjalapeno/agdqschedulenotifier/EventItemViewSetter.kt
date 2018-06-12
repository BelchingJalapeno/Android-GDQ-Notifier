package com.belchingjalapeno.agdqschedulenotifier

import android.animation.ValueAnimator
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class EventItemViewSetter(private val workQueueManager: WorkQueueManager) {
    fun setViewState(parentView: View, speedRunEvent: SpeedRunEvent, animate: Boolean = true) {
        val notificationToggleView = parentView.findViewById<ImageView>(R.id.notification_toggle_button)!!
        val runnersView = parentView.findViewById<TextView>(R.id.runnersView)
        val castersView = parentView.findViewById<TextView>(R.id.castersView)
        val runnersTextView = parentView.findViewById<TextView>(R.id.runnersTextView)
        val castersTextView = parentView.findViewById<TextView>(R.id.castersTextView)

        val notificationEnabled = workQueueManager.isQueued(speedRunEvent)

        setNotificationIconState(notificationToggleView, notificationEnabled)

        if (notificationEnabled) {
            if (runnersView.visibility == View.VISIBLE) {
                return
            }
            setViewVisibility(runnersView, View.VISIBLE, animate)
            setViewVisibility(castersView, View.VISIBLE, animate)
            setViewVisibility(runnersTextView, View.VISIBLE, animate)
            setViewVisibility(castersTextView, View.VISIBLE, animate)

            if (animate) {
                animateViewExpand(parentView.measuredHeightAndState, parentView.measuredHeightAndState + runnersView.measuredHeightAndState + castersView.measuredHeightAndState, parentView)
            }
        } else {
            if (runnersView.visibility == View.GONE) {
                return
            }
            setViewVisibility(runnersView, View.GONE, animate)
            setViewVisibility(castersView, View.GONE, animate)
            setViewVisibility(runnersTextView, View.GONE, animate)
            setViewVisibility(castersTextView, View.GONE, animate)
            if (animate) {
                animateViewExpand(parentView.measuredHeightAndState, parentView.measuredHeightAndState - (runnersView.measuredHeightAndState + castersView.measuredHeightAndState), parentView)
            }
        }
    }

    private fun animateViewExpand(startingHeight: Int, endingHeight: Int, parentView: View) {
        val anim = ValueAnimator.ofInt(startingHeight, endingHeight)
        anim.addUpdateListener { valueAnimator ->
            val `val` = valueAnimator.animatedValue as Int
            val layoutParams = parentView.layoutParams
            layoutParams.height = `val`
            parentView.layoutParams = layoutParams
        }
        anim.duration = 200
        anim.start()
    }

    private fun setNotificationIconState(notificationIconView: ImageView, enabled: Boolean) {
        if (enabled) {
            notificationIconView.setImageResource(R.drawable.ic_notifications_active_white_24dp)
            notificationIconView.setColorFilter(ContextCompat.getColor(notificationIconView.context, R.color.colorAccent))
            notificationIconView.imageAlpha = 255
        } else {
            notificationIconView.setImageResource(R.drawable.ic_notifications_off_black_24dp)
            notificationIconView.setColorFilter(0xFFFFFF)
            notificationIconView.imageAlpha = (0.54f * 255).toInt()
        }
    }

    private fun setViewVisibility(view: View, visibility: Int, animate: Boolean) {
        if (animate) {
            animateViewVisibility(view, visibility)
        } else {
            view.visibility = visibility
        }
    }
}