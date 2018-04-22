package library.core.attr

import android.content.Context
import android.util.AttributeSet
import android.util.Log

class AttrUtils {
    companion object {
        val mAttrs = IntArray(1)

        fun getAttrResId(context: Context, attributeSet: AttributeSet, attr: Int): Int {
            mAttrs[0] = attr
            val typeArray = context.obtainStyledAttributes(attributeSet, mAttrs)

            val attrResId = typeArray.getResourceId(0, 0)
            typeArray.recycle()

            return attrResId;
        }
    }
}