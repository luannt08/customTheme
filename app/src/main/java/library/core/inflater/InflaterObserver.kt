package library.core.inflater

import android.content.Context
import android.util.AttributeSet
import android.view.View

interface InflaterObserver {
    fun onViewCreated(view: View, parent: View?, name: String, context: Context, attrs: AttributeSet)
}