package library.core.annotation

import android.view.View
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target
import kotlin.reflect.KClass

@Retention(RetentionPolicy.RUNTIME)
annotation class Bind(val value: KClass<out View>)
