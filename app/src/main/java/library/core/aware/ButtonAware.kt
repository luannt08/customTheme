package library.core.aware

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.Button
import com.naul.customtheme.R
import library.core.annotation.Bind
import library.core.attr.Attr
import library.core.theme.Theme

@Bind(Button::class)
class ButtonAware<V : Button> : ViewAware<V>() {
    override fun obtainViewAttr(context: Context, view: V, attributeSet: AttributeSet) {
        super.obtainViewAttr(context, view, attributeSet)

        val buttonTextColorResId = getAttrResId(context, attributeSet, R.attr.theme_textColor)
        if (buttonTextColorResId != 0) {
            val buttonTextColorAttr = ButtonTextColorAttr(buttonTextColorResId)
            addAttr(buttonTextColorAttr)
        }
    }

    inner class ButtonTextColorAttr(attr: Int) : Attr<V>(attr) {
        override fun apply(view: V, theme: Theme, attrResId: Int) {
            val color = theme.getColour(attrResId)
            color ?. let { view.setTextColor(color) }
        }
    }
}