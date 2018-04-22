package library.core.attr

import android.view.View
import library.core.theme.Theme

abstract class Attr<V: View> {

    private var mAttrResId: Int = 0

    constructor(attrResId: Int) {
        mAttrResId = attrResId
    }

    fun onThemeChanged(theme: Theme, view: V) {
        apply(view, theme, mAttrResId)
    }

     abstract fun apply(view: V, theme: Theme, attrResId: Int)
}