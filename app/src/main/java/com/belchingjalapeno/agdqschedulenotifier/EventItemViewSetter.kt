package com.belchingjalapeno.agdqschedulenotifier

import android.animation.ValueAnimator
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageView

class EventItemViewSetter {
    fun setViewState(workQueueManager: WorkQueueManager, parentView: View, speedRunEvent: SpeedRunEvent, animate: Boolean = true) {
        val notificationToggleView = parentView.findViewById<ImageView>(R.id.notification_toggle_button)!!

        val notificationEnabled = workQueueManager.isQueued(speedRunEvent)

        setNotificationIconState(notificationToggleView, notificationEnabled)

//        if (notificationEnabled) {
//            if (runnersView.visibility == View.VISIBLE) {
//                return
//            }
//            setViewVisibility(runnersView, View.VISIBLE, animate)
//            setViewVisibility(castersView, View.VISIBLE, animate)
//            setViewVisibility(runnersTextView, View.VISIBLE, animate)
//            setViewVisibility(castersTextView, View.VISIBLE, animate)
//            if (animate) {
//                animateViewExpand(parentView.measuredHeight, parentView.measuredHeight + runnersView.measuredHeight + castersView.measuredHeight, parentView, animate)
//            }
//        } else {
//            if (runnersView.visibility == View.INVISIBLE) {
//                return
//            }
//            setViewVisibility(runnersView, View.INVISIBLE, animate)
//            setViewVisibility(castersView, View.INVISIBLE, animate)
//            setViewVisibility(runnersTextView, View.INVISIBLE, animate)
//            setViewVisibility(castersTextView, View.INVISIBLE, animate)
//            if (animate) {
//                animateViewExpand(parentView.measuredHeight, parentView.measuredHeight - (runnersView.measuredHeight + castersView.measuredHeight), parentView, animate)
//            } else {
//                val layoutParams = parentView.layoutParams
//                layoutParams.height = parentView.measuredHeight - (runnersView.measuredHeight + castersView.measuredHeight)
//                parentView.layoutParams = layoutParams
////                parentView.requestLayout()
////                parentView.invalidate()
//            }
//        }
    }

    private fun animateViewExpand(startingHeight: Int, endingHeight: Int, parentView: View, animate: Boolean) {
        if (!animate) {
            val layoutParams = parentView.layoutParams
            layoutParams.height = endingHeight
            parentView.layoutParams = layoutParams
//            parentView.requestLayout()
//            parentView.invalidate()
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
//            notificationIconView.setImageResource(R.drawable.ic_notifications_active_white_24dp)
            val startingColor = Color.argb((0.54f * 255).toInt(), 255, 255, 255)
            val endingColor = ContextCompat.getColor(notificationIconView.context, R.color.colorAccent)
            notificationIconView.setImageResource(R.drawable.ic_notifications_off_black_24dp)
            addColorAnimation(notificationIconView, startingColor, endingColor)
            notificationIconView.animate().setDuration(200).start()
        } else {
            val startingColor = ContextCompat.getColor(notificationIconView.context, R.color.colorAccent)
            val endingColor = Color.argb((0.54f * 255).toInt(), 255, 255, 255)
            notificationIconView.setImageResource(R.drawable.ic_notifications_off_black_24dp)
            addColorAnimation(notificationIconView, startingColor, endingColor)
            notificationIconView.animate().setDuration(200).start()
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