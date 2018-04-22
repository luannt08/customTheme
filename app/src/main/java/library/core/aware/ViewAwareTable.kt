package library.core.aware

import android.view.View
import library.core.annotation.Bind
import java.util.HashMap

class ViewAwareTable {
    companion object {
        private val mViewAwareMap = HashMap<Class<out View>, Class<out ViewAware<*>>>()

        init {
            add(ViewAware::class.java)
            add(TextViewAware::class.java)
            add(ButtonAware::class.java)
            add(ImageViewAware::class.java)
            add(RelativeLayoutAware::class.java)
        }

        fun add(viewAwareCls: Class<out ViewAware<*>>) {
            val bindViewAnnotation = viewAwareCls.getAnnotation(Bind::class.java)
            bindViewAnnotation?.let { add(viewAwareCls, bindViewAnnotation.value.java) }
        }

        private fun add(viewAwareCls: Class<out ViewAware<*>>, viewCls: Class<out View>) {
            mViewAwareMap.put(viewCls, viewAwareCls)
        }

        fun findViewAwareByClass(viewCls: Class<out View>): Class<out ViewAware<View>>? {
            var viewCls = viewCls
            var viewAwareCls = mViewAwareMap[viewCls]
            while (viewAwareCls == null && viewCls != View::class.java) {
                viewCls = viewCls.superclass as Class<out View>
                viewAwareCls = mViewAwareMap[viewCls]
            }

            viewAwareCls?.let { return viewAwareCls as? Class<out ViewAware<View>> }
                    ?: let { return null }
        }
    }
}
