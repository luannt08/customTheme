package com.naul.customtheme

import android.app.Application
import library.core.theme.LightTheme
import library.wrapper.ThemeWrapper

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        //default them is light
        ThemeWrapper.setTheme(LightTheme(applicationContext))
    }
}
