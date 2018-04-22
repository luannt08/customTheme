package library.core.aware

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.widget.TextView
import com.naul.customtheme.R
import library.core.annotation.Bind
import library.core.theme.Theme
import library.core.attr.Attr

@Bind(TextView::class)
class TextViewAware<V : TextView> : ViewAware<V>() {

    override fun obtainViewAttr(context: Context, view: V, attributeSet: AttributeSet) {
        super.obtainViewAttr(context, view, attributeSet)

        val textColorResId = getAttrResId(context, attributeSet, R.attr.theme_textColor)
        if (textColorResId != 0) {
            val textColorAttr = TextColorAttr(textColorResId)
            addAttr(textColorAttr)
        }
    }

    inner class TextColorAttr(attr: Int) : Attr<V>(attr) {

        override fun apply(view: V, theme: Theme, attrResId: Int) {
            val colour = theme.getColour(attrResId)
            colour ?. let { view.setTextColor(colour) }
        }
    }
}