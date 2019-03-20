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

import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.annotation.StyleRes
import com.gm.theme.R
import com.gm.theme.Theme
import com.gm.theme.inflator.*
import com.gm.theme.inflator.decor.ThemeDecorator
import com.gm.theme.tinting.EdgeEffectTint
import com.gm.theme.tinting.MenuTint
import com.gm.theme.tinting.SystemBarTint

/**
 * Author     : Gowtham
 * Email      : goutham.gm11@gmail.com
 * Github     : https://github.com/goutham106
 * Created on : 12/03/19.
 */
internal open class ThemeDelegateImplBase(
    private val activity: Activity,
    private val theme: Theme,
    @StyleRes private var themeResId: Int
) : ThemeDelegate() {

    private val timestamp = theme.timestamp

    override fun wrap(newBase: Context): Context {
        return ThemeContextWrapper(newBase, getDecorators(), getViewFactory())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (theme.isThemeModified && getThemeResId() != 0) {
            activity.setTheme(themeResId)
        }
        if (theme.isThemeModified) {
            tintBars()
        } else {
            // We use a transparent primary dark color so the library user
            // is not required to specify a color value for gm_default_primary_dark
            // If the theme is using the transparent (fake) primary dark color, we need
            // to update the status bar background with the auto-generated primary
            // dark color.
            val defaultPrimaryDark = Theme.getOriginalColor(R.color.gm_default_primary_dark)
            val realPrimaryDark = Theme.getOriginalColor(R.color.gm_primary_dark_reference)
            if (defaultPrimaryDark == realPrimaryDark) {
                tintStatusBar()
            }
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        if (theme.isThemeModified) {
            val ta = activity.obtainStyledAttributes(intArrayOf(android.R.attr.windowIsTranslucent))
            try {
                val isTranslucent = ta.getBoolean(0, false)
                if (!isTranslucent) {
                    activity.window.setBackgroundDrawable(ColorDrawable(theme.backgroundColor))
                }
            } finally {
                ta.recycle()
            }
        }
    }

    override fun onStart() {
        if (theme.isThemeModified) {
            EdgeEffectTint(activity).tint(theme.primary)
            MenuTint.forceOverflow(activity)
        }
    }

    override fun onResume() {
        if (timestamp != theme.timestamp) {
            recreateActivity()
            if (activity is Theme.ThemeModifiedListener) {
                activity.onThemeModified()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu) {
        theme.tint(menu, activity)
    }

    override fun getViewFactory(): ThemeViewFactory {
        val processors = getViewProcessors()
        return ThemeViewFactory(theme, *processors)
    }

    override fun getViewProcessors(): Array<ThemeViewProcessor<View>> {
        val processors = mutableListOf<ThemeViewProcessor<View>>()
        // Add processors needed for tinting
        if (theme.isThemeModified) {
            processors.addAll(getProcessorsForTheming().filterIsInstance<ThemeViewProcessor<View>>())
        }
        // Add processors from application
        ((activity.application ?: Theme.app) as? ThemeViewProcessor.Provider)?.let { provider ->
            processors.addAll(provider.getViewProcessors().filterIsInstance<ThemeViewProcessor<View>>())
        }
        // Add processors from activity
        (activity as? ThemeViewProcessor.Provider)?.let { provider ->
            processors.addAll(provider.getViewProcessors().filterIsInstance<ThemeViewProcessor<View>>())
        }
        return processors.toTypedArray()
    }

    override fun getDecorators(): Array<ThemeDecorator> {
        val decorators = mutableListOf<ThemeDecorator>()

        // Add decorators from activity
        if (activity is ThemeDecorator.Provider) {
            activity.getDecorators().forEach { decorators.add(it) }
        }

        // Add decorators from application
        ((activity.application ?: Theme.app) as? ThemeDecorator.Provider)?.apply {
            this.getDecorators().forEach { decorators.add(it) }
        }

        return decorators.toTypedArray()
    }

    protected open fun recreateActivity() = activity.recreate()

    protected open fun tintBars() {
        SystemBarTint(activity).run {
            setActionBarColor(theme.primary)
            if (theme.shouldTintStatusBar) {
                tintStatusBar(theme.primaryDark, this)
            }
            if (theme.shouldTintNavBar) {
                tintNavigationBar(theme.navigationBar, this)
            }
        }
    }

    protected open fun tintStatusBar(
        color: Int = theme.primaryDark,
        tinter: SystemBarTint = SystemBarTint(activity)
    ) {
        tinter.setStatusBarColor(color)
    }

    protected open fun tintNavigationBar(
        color: Int = theme.navigationBar,
        tinter: SystemBarTint = SystemBarTint(activity)
    ) {
        tinter.setNavigationBarColor(color)
    }

    protected open fun getProcessorsForTheming(): List<ThemeViewProcessor<out View>> {
        return arrayListOf(
            ListMenuItemViewProcessor(),
            AlertDialogProcessor(),
            TextViewProcessor(),
            BottomAppBarProcessor(),
            FloatingActionButtonProcessor(),
            TextInputLayoutProcessor(),
            NavigationViewProcessor()
        )
    }

    @StyleRes
    override fun getThemeResId(): Int {
        if (themeResId == 0) {
            activity.theme?.obtainStyledAttributes(intArrayOf(R.attr.windowActionBar))?.let { styledAttrs ->
                val windowActionBar = styledAttrs.getBoolean(0, true)
                themeResId = if (windowActionBar) {
                    theme.themes.actionBarTheme
                } else {
                    theme.themes.noActionBarTheme
                }
            } ?: run {
                Theme.log(TAG, "Error getting styled attribute: 'windowActionBar'")
            }
        }
        return themeResId
    }

    companion object {
        private const val TAG = "ThemeDelegateImplBase"
    }

}