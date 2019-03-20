package com.gm.theme

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import com.gm.theme.tinting.ThemeTinter
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import android.content.res.Resources

/**
 * Author     : Gowtham
 * Email      : goutham.gm11@gmail.com
 * Github     : https://github.com/goutham106
 * Created on : 12/03/19.
 */
class ThemeResources(original: Resources, private val theme: com.gm.theme.Theme = com.gm.theme.Theme.instance)
    : Resources(original.assets, original.displayMetrics, original.configuration) {

    init {
        theme.tinter.setup(original, this)
    }

    /* Track resources so we don't attempt to modify the Drawable or ColorStateList more than once */
    private val tintTracker = TintTracker()

    @Throws(NotFoundException::class)
    override fun getDrawable(id: Int): Drawable {
        return this.getDrawable(id, null)
    }

    @SuppressLint("PrivateResource")
    @Throws(NotFoundException::class)
    override fun getDrawable(id: Int, theme: Theme?): Drawable {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            super.getDrawable(id, theme).let { drawable ->
                if (!tintTracker.contains(id, theme)) {
                    try {
                        this.theme.tinter.tint(drawable)
                    } catch (e: ThemeTinter.ThemeTintException) {
                        com.gm.theme.Theme.log(TAG, "Error tinting drawable", e)
                    }
                    tintTracker.add(id, theme)
                }
                return drawable
            }
        }
        return when (id) {
            R.color.gm_background_dark, R.drawable.gm_bg_dark
            -> ColorDrawable(this.theme.backgroundDark)
            R.color.gm_background_dark_darker, R.drawable.gm_bg_dark_darker
            -> ColorDrawable(this.theme.backgroundDarkDarker)
            R.color.gm_background_dark_lighter, R.drawable.gm_bg_dark_lighter
            -> ColorDrawable(this.theme.backgroundDarkLighter)
            R.color.gm_background_light, R.drawable.gm_bg_light
            -> ColorDrawable(this.theme.backgroundLight)
            R.color.gm_background_light_darker, R.drawable.gm_bg_light_darker
            -> ColorDrawable(this.theme.backgroundLightDarker)
            R.color.gm_background_light_lighter, R.drawable.gm_bg_light_lighter
            -> ColorDrawable(this.theme.backgroundLightLighter)
            else -> {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                    super.getDrawable(id) else super.getDrawable(id, theme)
            }
        }
    }

    @Throws(NotFoundException::class)
    override fun getColor(id: Int): Int {
        return this.getColor(id, null)
    }

    @SuppressLint("PrivateResource")
    @Throws(NotFoundException::class)
    override fun getColor(id: Int, theme: Theme?): Int = when (id) {
        // ------ PRIMARY COLORS ------
        R.color.gm_primary_reference, R.color.gm_primary -> this.theme.primary
        R.color.gm_primary_dark_reference, R.color.gm_primary_dark -> this.theme.primaryDark
        R.color.gm_primary_light_reference, R.color.gm_primary_light -> this.theme.primaryLight
        // ------ ACCENT COLORS ------
        R.color.gm_accent_reference, R.color.gm_accent -> this.theme.accent
        R.color.gm_accent_light_reference, R.color.gm_accent_light -> this.theme.accentLight
        R.color.gm_accent_dark_reference, R.color.gm_accent_dark -> this.theme.accentDark
        // ------ BACKGROUND COLORS ------
        R.color.gm_bg_dark, R.color.gm_background_dark -> this.theme.backgroundDark
        R.color.gm_background_dark_lighter -> this.theme.backgroundDarkLighter
        R.color.gm_background_dark_darker -> this.theme.backgroundDarkDarker
        R.color.gm_bg_light, R.color.gm_background_light -> this.theme.backgroundLight
        R.color.gm_background_light_darker -> this.theme.backgroundLightDarker
        R.color.gm_background_light_lighter -> this.theme.backgroundLightLighter
        else -> {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
                super.getColor(id) else super.getColor(id, theme)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Throws(NotFoundException::class)
    override fun getColorStateList(id: Int): ColorStateList? {
        return super.getColorStateList(id)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @Throws(NotFoundException::class)
    override fun getColorStateList(id: Int, theme: Theme?): ColorStateList? {
        val colorStateList = super.getColorStateList(id, theme)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!tintTracker.contains(id, theme)) {
                this.theme.tinter.tint(colorStateList)
                tintTracker.add(id, theme)
            }
        }
        return colorStateList
    }

    private inner class TintTracker {

        private val cache: MutableSet<Int> by lazy {
            Collections.newSetFromMap(ConcurrentHashMap<Int, Boolean>())
        }

        internal fun contains(id: Int, theme: Theme?): Boolean = cache.contains(key(id, theme))

        internal fun add(id: Int, theme: Theme?): Boolean = cache.add(key(id, theme))

        private fun key(id: Int, theme: Theme?): Int = id + (theme?.hashCode() ?: 0)

    }

    companion object {
        private const val TAG = "ThemeResources"

    }

}