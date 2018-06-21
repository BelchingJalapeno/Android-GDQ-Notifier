package com.belchingjalapeno.agdqschedulenotifier

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri

class ExternalIntentsBuilder {
    companion object {

        fun getTwitchPendingIntent(context: Context): PendingIntent {
            val intent = getTwitchIntent()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            return PendingIntent.getActivity(context, 0, intent, 0)
        }

        fun getTwitchIntent(): Intent {
            return Intent(Intent.ACTION_VIEW, Uri.parse("https://www.twitch.tv/gamesdonequick"))
        }

        fun getDonateIntent(): Intent {
            return Intent(Intent.ACTION_VIEW, Uri.parse("https://gamesdonequick.com/tracker/donate/sgdq2018"))
        }

        fun getFilePickerJsonIntent(): Intent {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "application/*"

            return intent
        }
    }
}