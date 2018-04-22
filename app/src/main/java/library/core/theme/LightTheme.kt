package library.core.theme

import android.content.Context
import com.naul.customtheme.R
import library.core.theme.Theme

class LightTheme(context: Context) : Theme(context) {
    init {
        add(R.drawable.img_bg, R.drawable.img_bg)
        add(R.color.textColor, R.color.textColor)
        add(R.color.backgroundColor, R.color.backgroundColor)
        add(R.color.buttonBackground, R.color.buttonBackground)
        add(R.color.buttonTextColor, R.color.buttonTextColor)
    }
}