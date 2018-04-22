package com.naul.customtheme

import android.os.Bundle
import android.view.View
import android.widget.Button
import library.core.theme.DarkTheme
import library.core.theme.LightTheme
import library.wrapper.ThemeActivity
import library.wrapper.ThemeWrapper

class MainActivity : ThemeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_light).setOnClickListener(View.OnClickListener {
            ThemeWrapper.applyTheme(LightTheme(applicationContext))
        })


        findViewById<Button>(R.id.btn_dark).setOnClickListener(View.OnClickListener {
            ThemeWrapper.applyTheme(DarkTheme(applicationContext))
        })
    }
}
