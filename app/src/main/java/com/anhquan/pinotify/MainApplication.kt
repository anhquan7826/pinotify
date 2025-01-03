package com.anhquan.pinotify

import android.app.Application
import com.google.android.material.color.DynamicColors
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}