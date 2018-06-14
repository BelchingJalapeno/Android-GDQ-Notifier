package com.belchingjalapeno.agdqschedulenotifier

import android.app.Application
import com.squareup.leakcanary.LeakCanary

class CustomAppllication: Application() {
    override fun onCreate() {
        super.onCreate()
        if(LeakCanary.isInAnalyzerProcess(this)){
            return
        }
        LeakCanary.install(this)
    }
}