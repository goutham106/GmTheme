package com.gm.theme

import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity

/**
 * Author     : Gowtham
 * Email      : goutham.gm11@gmail.com
 * Github     : https://github.com/goutham106
 * Created on : 12/03/19.
 */


/**
 * Theme that can be used in your Theme based activity.
 *
 * If using your own Toolbar via [AppCompatActivity.setSupportActionBar] use the following theme:
 *
 * ```kotlin
 * override fun getThemeResId(): Int = theme.themes.actionBarTheme
 * ```
 *
 * If using the default ActionBar use the following theme:
 *
 * ```kotlin
 * override fun getThemeResId(): Int = theme.themes.noActionBarTheme
 * ```
 */
class GmThemes internal constructor(private val theme: Theme) {

    /**
     * Get a ActionBar theme.
     *
     * @return One of the following themes:
     * [R.style.Theme_Gm_Dark],
     * [R.style.Theme_Gm_Dark_LightActionBar],
     * [R.style.Theme_Gm_Light],
     * [R.style.Theme_Gm_Light_DarkActionBar]
     */
    @get:StyleRes
    val actionBarTheme: Int
        get() = when (theme.baseTheme) {
            Theme.BaseTheme.DARK ->
                if (theme.isActionBarLight)
                    R.style.Theme_Gm_Dark_LightActionBar
                else
                    R.style.Theme_Gm_Dark
            Theme.BaseTheme.LIGHT ->
                if (theme.isActionBarDark)
                    R.style.Theme_Gm_Light_DarkActionBar
                else
                    R.style.Theme_Gm_Light
        }

    /**
     * Get a NoActionBar theme
     *
     * @return One of the following themes:
     * [R.style.Theme_Gm_Dark_NoActionBar],
     * [R.style.Theme_Gm_Light_NoActionBar],
     * [R.style.Theme_Gm_Light_DarkActionBar_NoActionBar],
     * [R.style.Theme_Gm_Dark_LightActionBar_NoActionBar]
     */
    @get:StyleRes
    val noActionBarTheme: Int
        get() = when (theme.baseTheme) {
            Theme.BaseTheme.DARK ->
                if (theme.isActionBarLight) // Check primary color for correct actionBarTheme
                    R.style.Theme_Gm_Dark_LightActionBar_NoActionBar
                else
                    R.style.Theme_Gm_Dark_NoActionBar
            Theme.BaseTheme.LIGHT ->
                if (theme.isActionBarDark) // Check primary color for correct actionBarTheme
                    R.style.Theme_Gm_Light_DarkActionBar_NoActionBar
                else
                    R.style.Theme_Gm_Light_NoActionBar
        }

}