package library.core.theme

import android.content.Context
import com.naul.customtheme.R

import library.core.theme.Theme

class DarkTheme(context: Context) : Theme(context) {
    init {
        add(R.drawable.img_bg, R.drawable.img_bg_dark)
        add(R.color.textColor, R.color.textColorDark)
        add(R.color.backgroundColor, R.color.backgroundColorDark)
        add(R.color.buttonBackground, R.color.buttonBackgroundDark)
        add(R.color.buttonTextColor, R.color.buttonTextColorDark)
    }
}