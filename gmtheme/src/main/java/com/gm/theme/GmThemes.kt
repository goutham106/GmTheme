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