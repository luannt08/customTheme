package library.core.inflater

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.InflateException
import android.view.LayoutInflater
import android.view.View
import java.lang.reflect.Field
import java.lang.reflect.Method

class ThemeLayoutInflater : LayoutInflater {

    private val mClassPrefixList = arrayOf("android.widget.", "android.webkit.", "android.app.")
    private val TAG: String = ThemeLayoutInflater::class.java.name

    private var mObservers: ArrayList<InflaterObserver> = ArrayList()

    var mOriginalLayoutInflater: LayoutInflater? = null

    companion object {
        //for factory
        lateinit var sConstructorArgsField: Field
        lateinit var sFactoryField: Field

        //for factory2
        lateinit var sSetPrivateFactoryMethod: Method
        lateinit var sPrivateFactoryField: Field
        lateinit var sFactory2Field: Field
    }

    init {
        try {
            //factory 1
            sConstructorArgsField = LayoutInflater::class.java.getDeclaredField("mConstructorArgs")
            sConstructorArgsField.setAccessible(true)

            sFactoryField = LayoutInflater::class.java.getDeclaredField("mFactory")
            sFactoryField.isAccessible = true

            //factory 2
            sSetPrivateFactoryMethod = LayoutInflater::class.java.getDeclaredMethod("setPrivateFactory", Factory2::class.java)
            sSetPrivateFactoryMethod.isAccessible = true

            sPrivateFactoryField = LayoutInflater::class.java.getDeclaredField("mPrivateFactory")
            sPrivateFactoryField.isAccessible = true

            sFactory2Field = LayoutInflater::class.java.getDeclaredField("mFactory2")
            sFactory2Field.isAccessible = true
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    constructor(original: LayoutInflater?, newContext: Context?) : super(original, newContext) {
        mOriginalLayoutInflater = original;

        initFactory()
        initFactory2()
        initPrivateWrapperFactory2()
    }

    override fun setFactory(factory: Factory?) {
        try {
            var originFactory: LayoutInflater.Factory? = sFactoryField.get(this) as LayoutInflater.Factory

            if (originFactory != null && originFactory is FactoryWrapper) {
                originFactory = originFactory.innerFactory
                sFactoryField.set(this, originFactory)
            }

            super.setFactory(factory)
            initFactory()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    override fun setFactory2(factory: Factory2?) {
        try {
            var originFactory = sFactory2Field.get(this) as? Factory2

            if (originFactory != null && originFactory is Factory2Wrapper) {
                originFactory = originFactory.innerFactory2
                sFactory2Field.set(this, originFactory)
            }

            super.setFactory2(factory)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

        initFactory2()
        initPrivateWrapperFactory2()
    }

    override fun cloneInContext(p0: Context?): LayoutInflater {
        return this;
    }

    override fun onCreateView(name: String?, attrs: AttributeSet?): View? {
        var view: View? = null

        for (prefix in mClassPrefixList) {
            try {
                view = createView(name, prefix, attrs)
                if (view != null) {
                    break
                }
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }

        if (view == null) {
            view = super.onCreateView(name, attrs)
        }

        /*var args: Array<Any>? = null
        try {
            args = sConstructorArgsField.get(this) as Array<Any>
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        onViewCreated(view, null, name!!, args!![0] as Context, attrs!!)*/

        return view
    }

    private fun initFactory() {
        val originFactory = sFactoryField.get(this) as LayoutInflater.Factory

        try {
            if (originFactory == null) return
            if (originFactory is FactoryWrapper) return
            sFactoryField.set(this, FactoryWrapper(originFactory))
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    private fun initFactory2() {
        val originFactory2 = sFactory2Field.get(this) as Factory2

        try {
            if (originFactory2 == null) return
            if (originFactory2 is Factory2Wrapper) return
            sFactory2Field.set(this, Factory2Wrapper(originFactory2))
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    private fun initPrivateWrapperFactory2() {
        val originPrivateFactory = sPrivateFactoryField.get(this) as Factory2

        try {
            if (originPrivateFactory == null) return
            if (originPrivateFactory is PrivateFactoryWrapper) return
            sPrivateFactoryField.set(this, PrivateFactoryWrapper(originPrivateFactory))
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    private fun createView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
        var view: View? = null
        val constructorArgs = getConstructorArgs() ?:
                throw InflateException("mConstructorArgs is null, maybe device platform differs, too much to stack android!")
        val lastContext = constructorArgs[0]

        constructorArgs[0] = context
        Log.e(TAG, "createView  - name: " + name)
        try {
            if (-1 == name.indexOf('.')) {
                view = onCreateViewCompat(parent, name, attrs)
            } else {
                view = createView(name, null, attrs)
            }
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } finally {
            constructorArgs[0] = lastContext
        }

        Log.e(TAG, "createView - done : " + view)
        return view
    }

    @Throws(ClassNotFoundException::class)
    private fun onCreateViewCompat(parent: View?, name: String, attrs: AttributeSet): View? {
        Log.e(TAG, "onCreateViewCompat  ")
        return onCreateView(name, attrs)
    }

    private fun getConstructorArgs(): Array<Any>? {
        try {
            return sConstructorArgsField.get(this) as Array<Any>
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

        return null
    }

    private fun onViewCreated(view: View?, parent: View?, name: String, context: Context, attrs: AttributeSet) {
        Log.e(TAG, "onViewCreated - view: " + view);
        if (view == null) {
            return
        }

        notifyViewCreated(view, parent, name, context, attrs)
    }

    private fun notifyViewCreated(view: View, parent: View?, name: String, context: Context, attrs: AttributeSet) {

        Log.e(TAG, "notifyViewCreated: " + this + " view--" + view)

        for (observer in mObservers) {
            observer.onViewCreated(view, parent, name, context, attrs)
        }

        /*if (mOriginalLayoutInflater != null) {
            for (observer in mObservers) {
                observer.onViewCreated(view, parent!!, name, context, attrs)
            }
        }*/
    }

    fun registerLayoutObserver(observer: InflaterObserver) {
        if (!mObservers.contains(observer)) {
            mObservers.add(observer)
        }
    }

    fun unregisterLayoutObserver(observer: InflaterObserver) {
        if (mObservers.contains(observer)) {
            mObservers.remove(observer)
        }
    }

    inner class FactoryWrapper(val innerFactory: LayoutInflater.Factory) : LayoutInflater.Factory {

        override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
            var view: View? = innerFactory.onCreateView(name, context, attrs)
            if (view != null) {
                if (innerFactory !is FactoryWrapper) {
                    onViewCreated(view, null, name, context, attrs)
                }
            } else {
                view = createView(null, name, context, attrs)
                onViewCreated(view, null, name, context, attrs)
            }

            return view
        }
    }

    inner open class Factory2Wrapper(val innerFactory2: Factory2) : Factory2 {

        override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
            val view = onFactory2CreateView(name, context, attrs)
            Log.e(TAG, "---------onCreateView 1 : " + view)
            if (view != null && innerFactory2 !is Factory2Wrapper) {
                onViewCreated(view, null, name, context, attrs)
            }
            Log.e(TAG, "onCreateView 1 - return: " + view)
            return view
        }

        open fun onFactory2CreateView(name: String, context: Context, attrs: AttributeSet): View? {
            return innerFactory2.onCreateView(name, context, attrs)
        }

        override fun onCreateView(parent: View?, name: String, context: Context,
                                  attrs: AttributeSet): View? {
            val view = onFactory2CreateView(parent, name, context, attrs)
            Log.e(TAG, "--------onCreateView 2 : " + view)
            if (view != null && innerFactory2 !is Factory2Wrapper) {
                onViewCreated(view, parent, name, context, attrs)
            }

            Log.e(TAG, "onCreateView 2 - return: " + view)
            return view
        }

        open fun onFactory2CreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
            return innerFactory2.onCreateView(parent, name, context, attrs)
        }
    }

    inner class PrivateFactoryWrapper(delegateFactory: Factory2) : Factory2Wrapper(delegateFactory) {

        override fun onFactory2CreateView(name: String, context: Context, attrs: AttributeSet): View? {
            var view: View? = super.onFactory2CreateView(name, context, attrs)
            Log.e(TAG, "onFactory2CreateView 1 : " + view)
            if (view == null) {
                view = createView(null, name, context, attrs)
            }
            return view
        }

        override fun onFactory2CreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
            var view: View? = super.onFactory2CreateView(parent, name, context, attrs)
            Log.e(TAG, "onFactory2CreateView 2 : " + view)
            if (view == null) {
                view = createView(parent, name, context, attrs)
            }
            return view
        }
    }
}