package com.gm.theme.delegate

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.gm.theme.R
import com.gm.theme.Theme
import com.gm.theme.getKey
import com.gm.theme.utils.Reflection

/**
 * Author     : Gowtham
 * Email      : goutham.gm11@gmail.com
 * Github     : https://github.com/goutham106
 * Created on : 12/03/19.
 */
@RequiresApi(Build.VERSION_CODES.N)
@TargetApi(Build.VERSION_CODES.N)
internal open class ThemeDelegateImplV24(
    private val activity: Activity,
    private val theme: Theme,
    themeResId: Int
) : ThemeDelegateImplV23(activity, theme, themeResId) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (theme.isThemeModified) {
            preloadColors()
        }
    }

    @SuppressLint("PrivateApi")
    private fun preloadColors() {
        try {
            val klass = Class.forName("android.content.res.ColorStateList\$ColorStateListFactory")
            val constructor = klass.getConstructor(ColorStateList::class.java).apply {
                if (!isAccessible) isAccessible = true
            } ?: return

            val mResourcesImpl = Reflection.getFieldValue<Any?>(activity.resources, "mResourcesImpl") ?: return
            val cache = Reflection.getFieldValue<Any?>(mResourcesImpl, "sPreloadedComplexColors") ?: return
            val method = Reflection.getMethod(cache, "put", Long::class.java, Object::class.java) ?: return

            for ((id, color) in hashMapOf<Int, Int>().apply {
                put(R.color.gm_accent, theme.accent)
            }) {
                constructor.newInstance(ColorStateList.valueOf(color))?.let { factory ->
                    val key = activity.resources.getKey(id)
                    method.invoke(cache, key, factory)
                }
            }
        } catch (ex: Throwable) {
            Theme.log(TAG, "Error preloading colors", ex)
        }
    }

    companion object {
        private const val TAG = "ThemeDelegateImplV24"
    }

}