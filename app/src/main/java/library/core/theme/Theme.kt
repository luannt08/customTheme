package library.core.theme

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.SparseArray

open class Theme(context: Context) {
    private val mResMap: HashMap<Int, Int?> = HashMap<Int, Int?>()
    private val mContext = context

    fun add(key: Int, value: Int) {
        mResMap.put(key, value)
    }

    open fun getResId(keyResId: Int): Int? {
        return mResMap.get(keyResId)
    }

    open fun getColorStateList(colorId: Int): ColorStateList? {
        val res = getResId(colorId)
        val colorState = res?.let { ContextCompat.getColorStateList(mContext, res) } ?: null

        return colorState
    }

    open fun getDrawable(drawableId: Int): Drawable? {
        val res = getResId(drawableId)
        val drawable = res?.let { ContextCompat.getDrawable(mContext, res) } ?: null

        return drawable
    }

    open fun getColour(colourId: Int): Int? {
        val res = getResId(colourId)
        val colour = res?.let { ContextCompat.getColor(mContext, res) } ?: null

        return colour
    }
}