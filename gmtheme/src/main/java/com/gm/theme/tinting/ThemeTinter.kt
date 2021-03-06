/*
 * Copyright 2019 Gowtham Parimelazhagan.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gm.theme.tinting

import android.annotation.TargetApi
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.*
import android.os.Build
import android.view.View
import android.view.ViewGroup
import com.gm.theme.R
import com.gm.theme.Theme
import com.gm.theme.ThemeResources
import com.gm.theme.utils.ColorUtils
import com.gm.theme.utils.Reflection
import com.gm.theme.utils.Reflection.Companion.getFieldValue
import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * Author     : Gowtham
 * Email      : goutham.gm11@gmail.com
 * Github     : https://github.com/goutham106
 * Created on : 12/03/19.
 */
class ThemeTinter {

    private val colors = HashMap<Int, Int>()

    /**
     * Tints the [Drawable.ConstantState] to match the colors from the [resources][ThemeResources]
     *
     * @param drawable The [drawable][Drawable] to modify.
     */
    @Throws(ThemeTintException::class)
    fun tint(drawable: Drawable?) {
        if (drawable is GradientDrawable) {
            tint(drawable)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && drawable is RippleDrawable) {
            tint(drawable)
        } else if (drawable is LayerDrawable) {
            tint(drawable)
        } else if (drawable is DrawableContainer) {
            tint(drawable)
        } else if (drawable is NinePatchDrawable) {
            tint(drawable)
        } else if (drawable is ColorDrawable) {
            tint(drawable)
        }
    }

    /**
     * Updates the colors in a [ColorStateList] to match the colors from the [resources][ThemeResources]
     *
     * @param colorStateList The [color][ColorStateList] to modify
     * @return The modified [ColorStateList]
     */
    fun tint(colorStateList: ColorStateList?): ColorStateList? {
        return colorStateList?.let { csl ->
            fun updateColors(colors: IntArray): Boolean {
                var changed = false
                for (i in colors.indices) {
                    this.colors[colors[i]]?.let { color ->
                        if (color != colors[i]) {
                            colors[i] = color
                            changed = true
                        }
                    } ?: run {
                        val stripAlpha = ColorUtils.stripAlpha(colors[i])
                        this.colors[stripAlpha]?.run {
                            val color = Color.argb(Color.alpha(colors[i]), Color.red(this), Color.green(this), Color.blue(this))
                            colors[i] = color
                            changed = true
                        }
                    }
                }
                return changed
            }

            try {
                var changed = false
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    Reflection.getFieldValue<IntArray?>(csl, "mColors")?.let { colors ->
                        changed = updateColors(colors)
                    }
                } else {
                    Reflection.invoke<IntArray?>(csl, "getColors")?.let { colors ->
                        changed = updateColors(colors)
                    }
                }
                if (changed && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Reflection.invoke<Any?>(csl, "onColorsChanged")
                }
            } catch (e: Exception) {
                Theme.log(TAG, "Error tinting ColorStateList", e)
            }
            csl
        }
    }

    /**
     * Tint all drawables and color state lists in a view.
     */
    @JvmOverloads
    fun tint(view: View, recursive: Boolean = true) {
        try {
            var klass: Class<*>? = view.javaClass
            do {
                klass?.declaredFields?.forEach { field ->
                    if (Modifier.isStatic(field.modifiers)) return@forEach
                    if (field.type == ColorStateList::class.java) {
                        get<ColorStateList>(field, view)?.let { csl ->
                            tint(csl)
                        }
                    } else if (field.type == Drawable::class.java) {
                        get<Drawable>(field, view)?.let { drawable ->
                            tint(drawable)
                        }
                    }
                }
                klass = klass?.superclass
            } while (klass != null)
        } catch (e: Exception) {
            Theme.log(TAG, "Error tinting view: $view", e)
        }
        if (recursive && view is ViewGroup) {
            for (i in 0 until view.childCount) {
                view.getChildAt(i)?.let { v ->
                    tint(v, recursive)
                }
            }
        }
    }

    /**
     * Setup the colors for tinting drawables and color state lists on API 23+
     *
     * @param original The original resources. i.e. not the [ThemeResources]
     * @param resources The [ThemeResources] used to tint [drawables][Drawable] and [colors][ColorStateList]
     */
    @Suppress("DEPRECATION")
    internal fun setup(original: Resources, resources: ThemeResources) {
        COLOR_IDS.forEach { id ->
            colors[original.getColor(id)] = resources.getColor(id)
        }
    }

    private inline fun <reified T> get(field: Field, obj: Any): T? {
        if (!field.isAccessible) {
            field.isAccessible = true
        }
        if (Modifier.isFinal(field.modifiers)) {
            val modifiersField = Field::class.java.getDeclaredField("modifiers")
            modifiersField.isAccessible = true
            modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())
        }
        return field.get(obj) as? T
    }

    @Throws(ThemeTintException::class)
    private fun tint(drawable: GradientDrawable) {
        try {
            getFieldValue<Any?>(drawable, "mGradientState")?.let { state ->
                getFieldValue<ColorStateList?>(state, "mSolidColors")?.let { solidColors ->
                    tint(solidColors)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        drawable.color = solidColors
                    }
                }
            }
        } catch (e: Exception) {
            throw ThemeTintException("Error tinting GradientDrawable", e)
        }
    }

    @Throws(ThemeTintException::class)
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun tint(drawable: RippleDrawable) {
        try {
            getFieldValue<Any?>(drawable, "mState")?.let { state ->
                getFieldValue<ColorStateList?>(state, "mColor")?.let { color ->
                    tint(color)
                    Reflection.getField(state.javaClass.superclass, "mChildren")?.let { fChildren ->
                        (fChildren.get(state) as? Array<*>)?.forEach {
                            getFieldValue<Drawable?>(it, "mDrawable")?.let { drawable -> tint(drawable) }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            throw ThemeTintException("Error tinting RippleDrawable", e)
        }
    }

    @Throws(ThemeTintException::class)
    private fun tint(drawable: LayerDrawable) {
        try {
            getFieldValue<Any?>(drawable, "mLayerState")?.let { state ->
                getFieldValue<Array<Any?>>(state, "mChildren")?.let { children ->
                    children.forEach { child ->
                        getFieldValue<Drawable?>(child, "mDrawable")?.let { drawable ->
                            tint(drawable)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            throw ThemeTintException("Error tinting LayerDrawable", e)
        }
    }

    @Throws(ThemeTintException::class)
    private fun tint(drawable: DrawableContainer) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getFieldValue<DrawableContainer.DrawableContainerState?>(drawable, "mDrawableContainerState")?.let { state ->
                    for (i in 0 until state.childCount) {
                        tint(state.getChild(i))
                    }
                }
            }
        } catch (e: Exception) {
            throw ThemeTintException("Error tinting DrawableContainer", e)
        }
    }

    @Throws(ThemeTintException::class)
    private fun tint(drawable: NinePatchDrawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Android API 28 blocks getting the field NinePatchState#mTint =(
            return
        }
        try {
            Reflection.getFieldValue<Any?>(drawable, "mNinePatchState")?.let { ninePatchState ->
                Reflection.getFieldValue<ColorStateList?>(ninePatchState, "mTint")?.let { colorStateList ->
                    tint(colorStateList)
                }
            }
        } catch (e: Exception) {
            throw ThemeTintException("Error tinting NinePatchDrawable", e)
        }
    }

    private fun tint(drawable: ColorDrawable) {
        drawable.color = this.colors[drawable.color] ?: drawable.color
    }

    /**
     * Exception thrown when tinting a view fails.
     */
    class ThemeTintException(msg: String, e: Exception) : Exception(msg, e)

    companion object {

        private const val TAG = "ThemeTinter"

        private val COLOR_IDS = intArrayOf(
            R.color.gm_background_dark,
            R.color.gm_background_dark_darker,
            R.color.gm_background_dark_lighter,
            R.color.gm_background_light,
            R.color.gm_background_light_darker,
            R.color.gm_background_light_lighter,
            R.color.gm_accent,
            R.color.gm_accent_dark,
            R.color.gm_accent_dark_reference,
            R.color.gm_accent_light,
            R.color.gm_accent_light_reference,
            R.color.gm_accent_reference,
            R.color.gm_bg_light,
            R.color.gm_primary,
            R.color.gm_primary_dark,
            R.color.gm_primary_dark_reference,
            R.color.gm_primary_light,
            R.color.gm_primary_light_reference,
            R.color.gm_primary_reference,
            R.color.gm_bg_dark)

    }

}