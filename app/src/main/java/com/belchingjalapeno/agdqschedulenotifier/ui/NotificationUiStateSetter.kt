package com.belchingjalapeno.agdqschedulenotifier.ui

import android.graphics.Color
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.belchingjalapeno.agdqschedulenotifier.R

class NotificationUiStateSetter {
    fun setViewState(notificationEnabled: Boolean, notificationToggleView: ImageView, animationTime: Long = 200) {
        setNotificationIconState(notificationToggleView, notificationEnabled, animationTime)
    }

    private fun setNotificationIconState(notificationIconView: ImageView, enabled: Boolean, animationTime: Long) {
        if (enabled) {
            val endingColor = ContextCompat.getColor(notificationIconView.context, R.color.colorAccent)
            notificationIconView.setImageResource(R.drawable.ic_notifications_active_black_24dp)
            if (animationTime == 0L) {
                notificationIconView.setColorFilter(endingColor)
            } else {
                val startingColor = Color.argb((0.54f * 255).toInt(), 255, 255, 255)
                addColorAnimation(notificationIconView, startingColor, endingColor)
                notificationIconView.animate().setDuration(animationTime).start()
            }
        } else {
            val endingColor = Color.argb((0.54f * 255).toInt(), 255, 255, 255)
            notificationIconView.setImageResource(R.drawable.ic_notifications_off_black_24dp)
            if (animationTime == 0L) {
                notificationIconView.setColorFilter(endingColor)
            } else {
                val startingColor = ContextCompat.getColor(notificationIconView.context, R.color.colorAccent)
                addColorAnimation(notificationIconView, startingColor, endingColor)
                notificationIconView.animate().setDuration(animationTime).start()
            }
        }
    }
}