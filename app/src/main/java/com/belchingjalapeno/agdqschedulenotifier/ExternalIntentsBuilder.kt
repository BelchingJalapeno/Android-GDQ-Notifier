package com.belchingjalapeno.agdqschedulenotifier

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri

class ExternalIntentsBuilder {
    companion object {

        fun getTwitchPendingIntent(context: Context, sharedPreferences: SharedPreferences): PendingIntent {
            val intent = getTwitchIntent(sharedPreferences)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            return PendingIntent.getActivity(context, 0, intent, 0)
        }

        fun getTwitchIntent(sharedPreferences: SharedPreferences): Intent {
            return Intent(Intent.ACTION_VIEW, Uri.parse(sharedPreferences.getString(TWITCH_PREFERENCE_KEY, "https://www.twitch.tv/gamesdonequick")))
        }

        fun getDonateIntent(sharedPreferences: SharedPreferences): Intent {
            return Intent(Intent.ACTION_VIEW, Uri.parse(sharedPreferences.getString(DONATE_PREFERENCE_KEY, "https://gamesdonequick.com/tracker/donate/sgdq2018")))
        }

        fun getFilePickerJsonIntent(): Intent {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "application/*"

            return intent
        }
    }
}