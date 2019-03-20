package com.gm.theme.delegate

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.gm.theme.R
import com.gm.theme.Theme
import com.gm.theme.getKey
import com.gm.theme.inflator.*
import com.gm.theme.tinting.SystemBarTint
import com.gm.theme.utils.ColorUtils
import com.gm.theme.utils.Reflection

/**
 * Author     : Gowtham
 * Email      : goutham.gm11@gmail.com
 * Github     : https://github.com/goutham106
 * Created on : 12/03/19.
 */
@RequiresApi(Build.VERSION_CODES.M)
@TargetApi(Build.VERSION_CODES.M)
internal open class ThemeDelegateImplV23(
    private val activity: Activity,
    private val theme: Theme,
    themeResId: Int
) : ThemeDelegateImplV21(activity, theme, themeResId) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (theme.isThemeModified) {
            preloadColors()
        }
    }

    @SuppressLint("PrivateApi")
    private fun preloadColors() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            try {
                val klass = Class.forName("android.content.res.ColorStateList\$ColorStateListFactory")
                val constructor = klass.getConstructor(ColorStateList::class.java).apply {
                    if (!isAccessible) isAccessible = true
                } ?: return

                val cache = Reflection.getFieldValue<Any?>(activity.resources, "sPreloadedColorStateLists") ?: return
                val method = Reflection.getMethod(cache, "put", Long::class.java, Object::class.java) ?: return

                for ((id, color) in hashMapOf<Int, Int>().apply {
                    put(R.color.gm_accent, theme.accent)
                    put(R.color.gm_primary, theme.primary)
                }) {
                    try {
                        constructor.newInstance(ColorStateList.valueOf(color))?.let { factory ->
                            val key = activity.resources.getKey(id)
                            method.invoke(cache, key, factory)
                        }
                    } catch (ex: Throwable) {
                        Theme.log(TAG, "Error preloading colors", ex)
                    }
                }
            } catch (ex: Throwable) {
                Theme.log(TAG, "Error preloading colors", ex)
            }
        }

        PRELOADED_COLORS.forEach {
            // Load and update the colors before views are inflated
            activity.resources.getColorStateList(it, activity.theme)
        }

        PRELOADED_DRAWABLES.forEach {
            // Update the drawable's ConstantState before views are inflated.
            activity.resources.getDrawable(it, activity.theme)
        }
    }

    override fun tintStatusBar(color: Int, tinter: SystemBarTint) {
        super.tintStatusBar(color, tinter)
        if (!ColorUtils.isDarkColor(color)) {
            activity.window.decorView.run {
                systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    override fun getProcessorsForTheming(): List<ThemeViewProcessor<*>> {
        val processors = mutableListOf<ThemeViewProcessor<*>>()
        processors.addAll(super.getProcessorsForTheming())
        processors.addAll(
            arrayOf(
                AlertDialogProcessor(),
                BottomAppBarProcessor(),
                CompoundButtonProcessor(),
                DatePickerProcessor(),
                ImageButtonProcessor(),
                ListMenuItemViewProcessor(),
                SearchAutoCompleteProcessor(),
                SwitchProcessor(),
                SwitchCompatProcessor(),
                TimePickerProcessor(),
                TextViewProcessor(),
                ViewGroupProcessor()
            )
        )
        return processors
    }

    companion object {

        private const val TAG = "ThemeDelegateImplV23"

        @SuppressLint("PrivateResource")
        private val PRELOADED_COLORS = intArrayOf(
            R.color.gm_primary,
            R.color.gm_primary_light,
            R.color.gm_primary_dark,
            R.color.gm_accent,
            R.color.gm_accent_light,
            R.color.gm_accent_dark,
            R.color.gm_bg_dark,
            R.color.gm_bg_dark_lighter,
            R.color.gm_bg_dark_darker,
            R.color.gm_bg_light,
            R.color.gm_bg_light_lighter,
            R.color.gm_bg_light_darker,
            R.color.gm_background_dark,
            R.color.gm_background_dark_lighter,
            R.color.gm_background_dark_darker,
            R.color.gm_background_light,
            R.color.gm_background_light_lighter,
            R.color.gm_background_light_darker
        )

        private val PRELOADED_DRAWABLES = intArrayOf(
            R.drawable.gm_bg_button_primary,
            R.drawable.gm_primary,
            R.drawable.gm_primary_dark,
            R.drawable.gm_bg_button_accent,
            R.drawable.gm_accent,
            R.drawable.gm_bg_dark,
            R.drawable.gm_bg_dark_lighter,
            R.drawable.gm_bg_dark_darker,
            R.drawable.gm_bg_light,
            R.drawable.gm_bg_light_lighter,
            R.drawable.gm_bg_light_darker
        )

    }

}