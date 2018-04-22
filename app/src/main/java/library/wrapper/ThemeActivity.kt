package library.wrapper

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import library.core.theme.Theme

open class ThemeActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeWrapper.init(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ThemeWrapper.destroy(this)
    }

    /*fun notifyChanged(theme: Theme) {
        onSkinChanged(theme)
    }*/

    open fun onThemeChanged(theme: Theme) {
        ThemeWrapper.applyTheme(this, theme)
    }
}