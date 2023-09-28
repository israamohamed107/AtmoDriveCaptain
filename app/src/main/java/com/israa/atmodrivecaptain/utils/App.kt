package com.israa.atmodrivecaptain.utils

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application(){
    override fun onCreate() {
        super.onCreate()

        MySharedPreference.init(this)
    }
}