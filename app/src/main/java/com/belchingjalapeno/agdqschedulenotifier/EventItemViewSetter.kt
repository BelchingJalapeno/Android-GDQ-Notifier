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
            if(animate)
            animateViewExpand(parentView.measuredHeight, parentView.measuredHeight + runnersView.measuredHeight + castersView.measuredHeight, parentView, animate)
        } else {
            if (runnersView.visibility == View.INVISIBLE) {
                return
            }
            setViewVisibility(runnersView, View.INVISIBLE, animate)
            setViewVisibility(castersView, View.INVISIBLE, animate)
            setViewVisibility(runnersTextView, View.INVISIBLE, animate)
            setViewVisibility(castersTextView, View.INVISIBLE, animate)
            if(animate)
            animateViewExpand(parentView.measuredHeight, parentView.measuredHeight - (runnersView.measuredHeight + castersView.measuredHeight), parentView, animate)
            else{
                val layoutParams = parentView.layoutParams
                layoutParams.height = parentView.measuredHeight - (runnersView.measuredHeight + castersView.measuredHeight)
                parentView.layoutParams = layoutParams
                parentView.requestLayout()
                parentView.invalidate()
            }
        }
    }

    private fun animateViewExpand(startingHeight: Int, endingHeight: Int, parentView: View, animate: Boolean) {
        if (!animate) {
            val layoutParams = parentView.layoutParams
            layoutParams.height = endingHeight
            parentView.layoutParams = layoutParams
            parentView.requestLayout()
            return
        }
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