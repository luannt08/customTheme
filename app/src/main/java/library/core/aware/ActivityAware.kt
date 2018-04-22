package library.core.aware

import android.app.Activity
import library.core.theme.Theme
import library.wrapper.ThemeWrapper

class ActivityAware : Aware {
    private var mActivity: Activity

    constructor(activity: Activity) {
        mActivity = activity
    }

    override fun onThemeChanged(theme: Theme) {
        ThemeWrapper.applyTheme(mActivity.window.decorView, theme)
    }
}