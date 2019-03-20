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

package com.gm.theme.prefs

import android.content.res.AssetManager
import android.graphics.Color
import android.os.Build
import androidx.annotation.ColorInt
import com.gm.theme.R
import com.gm.theme.Theme
import com.gm.theme.utils.ColorUtils
import com.gm.theme.Theme.BaseTheme.DARK
import com.gm.theme.Theme.BaseTheme.LIGHT
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Author     : Gowtham
 * Email      : goutham.gm11@gmail.com
 * Github     : https://github.com/goutham106
 * Created on : 12/03/19.
 */


/**
 *  Load a list of themes using [GmTheme.from].
 */
data class GmTheme internal constructor(
    val themeName: String,
    val baseTheme: Theme.BaseTheme,
    @ColorInt val primary: Int,
    @ColorInt val primaryDark: Int,
    @ColorInt val primaryLight: Int,
    @ColorInt val accent: Int,
    @ColorInt val accentDark: Int,
    @ColorInt val accentLight: Int,
    @ColorInt val background: Int,
    @ColorInt val backgroundDark: Int,
    @ColorInt val backgroundLight: Int,
    @ColorInt val menuIconColor: Int,
    @ColorInt val subMenuIconColor: Int,
    @ColorInt val navigationBarColor: Int,
    val shouldTintStatusBar: Boolean,
    val shouldTintNavBar: Boolean
) {

    constructor(themeName: String, theme: Theme) : this(
        themeName,
        theme.baseTheme,
        theme.primary,
        theme.primaryDark,
        theme.primaryLight,
        theme.accent,
        theme.accentDark,
        theme.accentLight,
        theme.backgroundColor,
        theme.backgroundColorDark,
        theme.backgroundColorLight,
        theme.menuIconColor,
        theme.subMenuIconColor,
        theme.primary,
        theme.shouldTintStatusBar,
        theme.shouldTintNavBar
    )

    /**
     * Set the theme. The activity will need to be recreated for changes to be applied.
     */
    fun apply(theme: Theme) = theme.edit {
        baseTheme(baseTheme)
        primary(primary)
        primaryDark(primaryDark)
        primaryLight(primaryLight)
        accent(accent)
        accentDark(accentDark)
        accentLight(accentLight)
        background(background)
        when (baseTheme) {
            Theme.BaseTheme.LIGHT -> {
                backgroundLightDarker(backgroundDark)
                backgroundLightLighter(backgroundLight)
            }
            Theme.BaseTheme.DARK -> {
                backgroundDarkDarker(backgroundDark)
                backgroundDarkLighter(backgroundLight)
            }
        }
        menuIconColor(menuIconColor)
        subMenuIconColor(subMenuIconColor)
        navigationBar(navigationBarColor)
        shouldTintStatusBar(shouldTintStatusBar)
        shouldTintNavBar(shouldTintNavBar)
    }

    /**
     * Convert this theme to a JSON object.
     */
    fun toJson(): JSONObject = JSONObject().apply {
        put(THEME_NAME, themeName)
        put(BASE_THEME, baseTheme.name)
        put(PRIMARY_COLOR, ColorUtils.toHex(primary))
        put(PRIMARY_DARK_COLOR, ColorUtils.toHex(primaryDark))
        put(PRIMARY_LIGHT_COLOR, ColorUtils.toHex(primaryLight))
        put(ACCENT_COLOR, ColorUtils.toHex(accent))
        put(ACCENT_DARK_COLOR, ColorUtils.toHex(accentDark))
        put(ACCENT_LIGHT_COLOR, ColorUtils.toHex(accentLight))
        put(BACKGROUND_COLOR, ColorUtils.toHex(background))
        put(BACKGROUND_DARK_COLOR, ColorUtils.toHex(backgroundDark))
        put(BACKGROUND_LIGHT_COLOR, ColorUtils.toHex(backgroundLight))
    }

    /**
     * Check if this theme matches the current color scheme
     */
    fun isMatchingColorScheme(theme: Theme): Boolean = primary == theme.primary &&
            accent == theme.accent &&
            background == theme.backgroundColor

    companion object {

        private const val TAG = "GmTheme"

        // JSON Keys
        private const val THEME_NAME = "theme_name"
        private const val BASE_THEME = "base_theme"
        private const val PRIMARY_COLOR = "primary"
        private const val PRIMARY_DARK_COLOR = "primary_dark"
        private const val PRIMARY_LIGHT_COLOR = "primary_light"
        private const val ACCENT_COLOR = "accent"
        private const val ACCENT_DARK_COLOR = "accent_dark"
        private const val ACCENT_LIGHT_COLOR = "accent_light"
        private const val BACKGROUND_COLOR = "background"
        private const val BACKGROUND_DARK_COLOR = "background_dark"
        private const val BACKGROUND_LIGHT_COLOR = "background_light"
        private const val MENU_ICON_COLOR = "menu_icon_color"
        private const val SUB_MENU_ICON_COLOR = "sub_menu_icon_color"
        private const val NAVIGATION_BAR_COLOR = "navigation_bar"
        private const val SHOULD_TINT_STATUSBAR = "should_tint_statusbar"
        private const val SHOULD_TINT_NAVBAR = "should_tint_navbar"

        /**
         * Get a list of themes from a file containing the JSON
         */
        fun from(file: File) = from(file.inputStream().readBytes().toString(Charsets.UTF_8))

        /**
         * Get a list of themes from an asset containing the JSON
         */
        fun from(assets: AssetManager, path: String) = from(
            assets.open(path).bufferedReader().use { it.readText() }
        )

        /**
         * Get a list of themes from a JSON array.
         */
        fun from(json: String): List<GmTheme> {
            val themes = mutableListOf<GmTheme>()
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                try {
                    (array.get(i) as? JSONObject)?.let {
                        themes.add(newInstance(it))
                    }
                } catch (e: Exception) {
                    Theme.log(TAG, "Error reading theme #${i + 1}", e)
                }
            }
            return themes
        }

        /**
         * Deserialize JSON to a [GmTheme]
         */
        fun newInstance(json: JSONObject): GmTheme {
            // Get the theme name
            val themeName = json.optString(THEME_NAME)

            // Get the primary colors
            val primary = ColorUtils.parseColor(json.getString(PRIMARY_COLOR))
            val primaryDark = if (json.has(PRIMARY_DARK_COLOR)) {
                ColorUtils.parseColor(json.getString(PRIMARY_DARK_COLOR))
            } else {
                ColorUtils.darker(primary)
            }
            val primaryLight = if (json.has(PRIMARY_LIGHT_COLOR)) {
                ColorUtils.parseColor(json.getString(PRIMARY_LIGHT_COLOR))
            } else {
                ColorUtils.parseColor(json.getString(PRIMARY_LIGHT_COLOR))
            }

            // Get the accent colors
            val accent = ColorUtils.parseColor(json.getString(ACCENT_COLOR))
            val accentDark = if (json.has(ACCENT_DARK_COLOR)) {
                ColorUtils.parseColor(json.getString(ACCENT_DARK_COLOR))
            } else {
                ColorUtils.darker(accent)
            }
            val accentLight = if (json.has(ACCENT_LIGHT_COLOR)) {
                ColorUtils.parseColor(json.getString(ACCENT_LIGHT_COLOR))
            } else {
                ColorUtils.lighter(accent)
            }

            // Get the background colors
            val background = ColorUtils.parseColor(json.getString(BACKGROUND_COLOR))
            val backgroundDarker = if (json.has(BACKGROUND_DARK_COLOR)) {
                ColorUtils.parseColor(json.getString(BACKGROUND_DARK_COLOR))
            } else {
                ColorUtils.darker(background)
            }
            val backgroundLighter = if (json.has(BACKGROUND_LIGHT_COLOR)) {
                ColorUtils.parseColor(json.getString(BACKGROUND_LIGHT_COLOR))
            } else {
                ColorUtils.lighter(background)
            }

            // Get the base theme
            val baseTheme = if (json.has(BASE_THEME)) {
                if (json.getString(BASE_THEME) == Theme.BaseTheme.DARK.name) DARK else LIGHT
            } else {
                if (ColorUtils.isDarkColor(background)) DARK else LIGHT
            }

            // Get the menu item colors
            val menuIconColor = if (json.has(MENU_ICON_COLOR)) {
                ColorUtils.parseColor(json.getString(MENU_ICON_COLOR))
            } else {
                @Suppress("DEPRECATION")
                Theme.res.getColor(if (ColorUtils.isDarkColor(primary, 0.75))
                    R.color.gm_menu_icon_light
                else
                    R.color.gm_menu_icon_dark
                )
            }
            val subMenuIconColor = if (json.has(SUB_MENU_ICON_COLOR)) {
                ColorUtils.parseColor(json.getString(SUB_MENU_ICON_COLOR))
            } else {
                @Suppress("DEPRECATION")
                Theme.res.getColor(
                    if (baseTheme == LIGHT) R.color.gm_sub_menu_icon_dark else R.color.gm_sub_menu_icon_light)
            }

            // Get the navigation bar colors
            val navigationBarColor = if (json.has(NAVIGATION_BAR_COLOR)) {
                ColorUtils.parseColor(json.getString(NAVIGATION_BAR_COLOR))
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O || ColorUtils.isDarkColor(primary, 0.75)) {
                    primary
                } else {
                    Color.BLACK
                }
            }

            // Get the tinting flags
            val shouldTintStatusBar = if (json.has(SHOULD_TINT_STATUSBAR)) {
                json.getBoolean(SHOULD_TINT_STATUSBAR)
            } else {
                Theme.res.getBoolean(R.bool.should_tint_status_bar)
            }
            val shouldTintNavBar = if (json.has(SHOULD_TINT_NAVBAR)) {
                json.getBoolean(SHOULD_TINT_NAVBAR)
            } else {
                Theme.res.getBoolean(R.bool.should_tint_nav_bar)
            }

            return GmTheme(
                themeName,
                baseTheme,
                primary,
                primaryDark,
                primaryLight,
                accent,
                accentDark,
                accentLight,
                background,
                backgroundDarker,
                backgroundLighter,
                menuIconColor,
                subMenuIconColor,
                navigationBarColor,
                shouldTintStatusBar,
                shouldTintNavBar
            )
        }

    }

}