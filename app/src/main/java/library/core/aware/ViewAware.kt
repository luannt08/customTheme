package library.core.aware

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.naul.customtheme.R
import library.core.attr.Attr
import library.core.attr.AttrUtils
import library.core.theme.Theme
import java.lang.ref.WeakReference

open class ViewAware<V : View> : Aware {
    private lateinit var mViewRef: WeakReference<V>
    private var mAttrs: ArrayList<Attr<V>> = ArrayList()

    override fun onThemeChanged(theme: Theme) {
        for (attr in mAttrs) {
            attr.onThemeChanged(theme, mViewRef.get()!!)
        }
    }

    open fun obtainViewAttr(context: Context, view: V, attributeSet: AttributeSet) {
        mViewRef = WeakReference(view)

        attributeSet ?: return

        //init two basic attr of an view here
        val backgroundResId = getAttrResId(context, attributeSet, R.attr.theme_background)
        if (backgroundResId != 0) {
            val backgroundAttr = BackgroundAttr(context, backgroundResId)
            addAttr(backgroundAttr)
        }

        val backgroundTintResId = getAttrResId(context, attributeSet, R.attr.theme_backgroundTint)
        if (backgroundTintResId != 0) {
            val backgroundTint = BackgroundTintAttr(backgroundTintResId)
            addAttr(backgroundTint)
        }
    }

    protected fun getAttrResId(context: Context, attributeSet: AttributeSet, attr: Int): Int {
        return AttrUtils.getAttrResId(context, attributeSet, attr)
    }

    protected fun addAttr(attr: Attr<V>) {
        mAttrs.add(attr)
    }

    open fun hasAttr(): Boolean {
        return !mAttrs.isEmpty()
    }

    inner class BackgroundAttr : Attr<V> {
        private var isSupportColor: Boolean = false

        constructor(context: Context, attrResId: Int) : super(attrResId) {
            isSupportColor = "color".equals(context.resources.getResourceTypeName(attrResId))
        }

        override fun apply(view: V, theme: Theme, resId: Int) {
            isSupportColor ?. let {
                Log.e("Luan", "background: " + view + " - color: " + resId)
                val color = theme.getColour(resId)
                color?.let { view.setBackgroundColor(color) }
            } ?: let {
                val resIdBg = theme.getResId(resId)
                resIdBg?.let { view.setBackgroundResource(resIdBg) }
            }
        }
    }

    inner class BackgroundTintAttr(attrResId: Int) : Attr<V>(attrResId) {
        override fun apply(view: V, theme: Theme, resId: Int) {
        }
    }
}