package library.wrapper

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import library.core.aware.*
import library.core.inflater.InflaterObserver
import library.core.inflater.ThemeLayoutInflater
import library.core.theme.Theme
import java.lang.ref.WeakReference

class ThemeWrapper {
    companion object {
        private val TAG = ThemeWrapper::class.java.name
        private var mCurrentTheme : Theme? = null

        //key must be an application-specific resource id PLEASE DO NOT MAKE A CHANGE ON
        private val TAG_AWARE_HOLDER = 0x10000000
        private val TAG_VIEW_AWARE = 0x20000000
        //-----------------------------------------------------------------------

        private val mActivities = ArrayList<WeakReference<Activity>>()

        private val mLayoutObserver = object : InflaterObserver {
            override fun onViewCreated(view: View, parent: View?, name: String, context: Context, attrs: AttributeSet) {
                val awareCls = findViewAwareClass(view)
                Log.e(TAG, "awareClas: " + awareCls)
                if (awareCls != null) {
                    val viewAware = instanceAware(awareCls)
                    viewAware!!.obtainViewAttr(view.context, view, attrs)
                    Log.e(TAG, "viewAware: " + viewAware)
                    if (viewAware.hasAttr()) {
                        view.setTag(TAG_VIEW_AWARE, viewAware)

                        //do first init for default theme
                        mCurrentTheme?.let { viewAware.onThemeChanged(mCurrentTheme!!) }
                    }
                }
            }
        }

        fun init(activity: Activity) {
            mActivities.add(WeakReference(activity))

            val mInflater: LayoutInflater = activity.window.layoutInflater
            val customInflater = ThemeLayoutInflater(mInflater, activity)

            injectLayoutInflater(customInflater, activity.window, activity.window.javaClass, "mLayoutInflater")
            injectLayoutInflater(customInflater, activity, ContextThemeWrapper::class.java, "mInflater")
            customInflater.registerLayoutObserver(mLayoutObserver)

            //chain aware holder to root view of activity
            val awareHolder = AwareHolder()
            val activityAware = ActivityAware(activity)
            awareHolder.addAware(activityAware)
            val decorView = activity.window.decorView
            decorView.setTag(TAG_AWARE_HOLDER, awareHolder);
        }

        fun destroy(activity: Activity) {
            val layoutInflater = activity.layoutInflater
            if (layoutInflater is ThemeLayoutInflater) {
                layoutInflater.unregisterLayoutObserver(mLayoutObserver)
            }

            mActivities
                    .filter { it.get() == activity }
                    .forEach { mActivities.remove(it) }
        }

        private fun injectLayoutInflater(layoutInflater: LayoutInflater, src: Any, clz: Class<*>, name: String) {
            try {
                val field = clz.getDeclaredField(name)
                field.isAccessible = true
                field.set(src, layoutInflater)
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }

        fun findViewAwareClass(view: View): Class<out ViewAware<View>>? {
            return ViewAwareTable.findViewAwareByClass(view.javaClass)
        }

        fun <E> instanceAware(awareCls: Class<E>): E? {
            try {
                return awareCls.newInstance()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }

            return null
        }

        fun addAwareHolder(activity: Activity, aware: Aware) {
            val rootView = activity.window.decorView
            val awareHolder = rootView.getTag(TAG_AWARE_HOLDER) as AwareHolder
            awareHolder.addAware(aware)
        }

        private fun getViewAware(view: View): ViewAware<View>? {
            return view.getTag(TAG_VIEW_AWARE) as ViewAware<View>?
        }

        //it must be called in very first time - should be at onCreate - Application
        fun setTheme(theme: Theme) {
            mCurrentTheme = theme
        }

        fun applyTheme(theme: Theme) {
            for (activityRef in mActivities) {
                (activityRef.get() as ThemeActivity).onThemeChanged(theme)
            }
        }

        fun applyTheme(activity: Activity, theme: Theme) {
            val awareHolder = activity.window.decorView.getTag(TAG_AWARE_HOLDER) as AwareHolder
            awareHolder.notifyThemeChanged(theme)
        }

        fun applyTheme(view: View, theme: Theme) {
            if (view is ViewGroup) {
                for (i in 0..view.childCount) {
                    val childView = view.getChildAt(i)
                    childView ?. let {
                        applyTheme(childView, theme)
                    } ?: let {
                        val viewAware = getViewAware(view)
                        Log.e(TAG, "applyThem: " + viewAware)
                        viewAware?.onThemeChanged(theme)
                    }
                }
            } else {
                val viewAware = getViewAware(view)
                Log.e(TAG, "applyThem: " + viewAware)
                viewAware?.onThemeChanged(theme)
            }
        }
    }
}