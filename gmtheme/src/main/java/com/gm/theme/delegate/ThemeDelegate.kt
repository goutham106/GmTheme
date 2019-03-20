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

package com.gm.theme.delegate

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.annotation.StyleRes
import com.gm.theme.Theme
import com.gm.theme.inflator.ThemeViewFactory
import com.gm.theme.inflator.ThemeViewProcessor
import com.gm.theme.inflator.decor.ThemeDecorator

/**
 * Author     : Gowtham
 * Email      : goutham.gm11@gmail.com
 * Github     : https://github.com/goutham106
 * Created on : 12/03/19.
 */


/**
 * This class represents a delegate which you can use to extend [Theme]'s support to any [Activity].
 *
 * When using a [delegate][ThemeDelegate] the following methods should be called in the corresponding activity:
 *
 * * [ThemeDelegate.onCreate]
 * * [ThemeDelegate.onPostCreate]
 * * [ThemeDelegate.onStart]
 * * [ThemeDelegate.onResume]
 * * [ThemeDelegate.onCreateOptionsMenu]
 *
 * The method [ThemeDelegate.wrap] should also be used in [Activity.attachBaseContext].
 */
abstract class ThemeDelegate {
    /**
     * Wrap the context in a [ThemeContextWrapper].
     *
     * @param newBase The base context
     * @return The wrapped context
     */
    abstract fun wrap(newBase: Context): Context

    /**
     * Should be called from [Activity.onCreate()][Activity.onCreate].
     *
     * This should be called before `super.onCreate()` as so:
     *
     * ```
     * override fun onCreate(savedInstanceState: Bundle?) {
     *     getThemeDelegate().onCreate(savedInstanceState)
     *     super.onCreate(savedInstanceState)
     *     // ...
     * }
     * ```
     */
    abstract fun onCreate(savedInstanceState: Bundle?)

    /**
     * Should be called from [Activity.onPostCreate]
     */
    abstract fun onPostCreate(savedInstanceState: Bundle?)

    /**
     * Should be called from [Activity.onStart]
     */
    abstract fun onStart()

    /**
     * Should be called from [Activity.onResume()][Activity.onResume].
     *
     * This should be called after `super.onResume()` as so:
     *
     * ```
     * override fun onResume() {
     *     super.onResume()
     *     getThemeDelegate().onResume()
     *     // ...
     * }
     * ```
     */
    abstract fun onResume()

    /**
     * Should be called from [Activity.onCreateOptionsMenu] after inflating the menu.
     */
    abstract fun onCreateOptionsMenu(menu: Menu)

    protected abstract fun getViewFactory(): ThemeViewFactory

    protected abstract fun getViewProcessors(): Array<ThemeViewProcessor<View>>

    protected abstract fun getDecorators(): Array<ThemeDecorator>

    @StyleRes
    protected abstract fun getThemeResId(): Int

    companion object {

        /**
         * Create a [ThemeDelegate] to be used in an [Activity].
         *
         * @param activity The activity
         * @param theme The theme instance for theming
         * @param themeResId The theme resource id
         * @return The delegate
         */
        @SuppressLint("NewApi") // Needed for Android Pie (API 28) for whatever reason ¯\_(ツ)_/¯
        @JvmStatic
        fun create(activity: Activity, theme: Theme, @StyleRes themeResId: Int): ThemeDelegate {
            return when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> ThemeDelegateImplV26(activity, theme, themeResId)
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> ThemeDelegateImplV24(activity, theme, themeResId)
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> ThemeDelegateImplV23(activity, theme, themeResId)
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> ThemeDelegateImplV21(activity, theme, themeResId)
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> ThemeDelegateImplV19(activity, theme, themeResId)
                else -> ThemeDelegateImplBase(activity, theme, themeResId)
            }
        }

    }

}