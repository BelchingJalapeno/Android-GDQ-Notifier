package com.belchingjalapeno.agdqschedulenotifier

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri

class TwitchIntentBuilder {
    companion object {

        fun getTwitchPendingIntent(context: Context): PendingIntent {
            val intent = getTwitchIntent()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            return PendingIntent.getActivity(context, 0, intent, 0)
        }

        fun getTwitchIntent(): Intent {
            return Intent(Intent.ACTION_VIEW, Uri.parse("https://www.twitch.tv/gamesdonequick"))
        }
    }
}