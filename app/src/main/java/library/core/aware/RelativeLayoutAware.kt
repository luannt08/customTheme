package library.core.aware

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import library.core.annotation.Bind

@Bind(RelativeLayout::class)
class RelativeLayoutAware<V : RelativeLayout> : ViewAware<V>() {
    override fun obtainViewAttr(context: Context, view: V, attributeSet: AttributeSet) {
        super.obtainViewAttr(context, view, attributeSet)
    }
}