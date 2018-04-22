package library.core.aware

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import com.naul.customtheme.R
import library.core.annotation.Bind
import library.core.attr.Attr
import library.core.theme.Theme

@Bind(ImageView::class)
class ImageViewAware<V : ImageView> : ViewAware<V>() {

    override fun obtainViewAttr(context: Context, view: V, attributeSet: AttributeSet) {
        super.obtainViewAttr(context, view, attributeSet)

        val imgSrcResId = getAttrResId(context, attributeSet, R.attr.theme_src)
        if (imgSrcResId != 0) {
            val imgSrcAttr = ImgSrcAttr(imgSrcResId)
            addAttr(imgSrcAttr)
        }
    }

    inner class ImgSrcAttr(attr: Int) : Attr<V>(attr) {
        override fun apply(view: V, theme: Theme, attrResId: Int) {
            val src = theme.getResId(attrResId)
            src ?. let { view.setImageResource(src) }
        }
    }
}
