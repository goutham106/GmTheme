package com.gm.theme.delegate

import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar

/**
 * Author     : Gowtham
 * Email      : goutham.gm11@gmail.com
 * Github     : https://github.com/goutham106
 * Created on : 12/03/19.
 */
interface BaseAppCompatDelegate {

    /**
     * Support library version of [android.app.Activity.getActionBar].
     *
     * Retrieve a reference to this activity's ActionBar.
     *
     * @return The Activity's ActionBar, or null if it does not have one.
     */
    fun getSupportActionBar(): ActionBar?

    /**
     * Set a [Toolbar][android.widget.Toolbar] to act as the [androidx.appcompat.app.ActionBar] for this Activity window.
     *
     * When set to a non-null value the [android.app.Activity.getActionBar] method will return an
     * [ActionBar][androidx.appcompat.app.ActionBar] object that can be used to control the given toolbar as if it were a
     * traditional window decor action bar. The toolbar's menu will be populated with the Activity's options menu and
     * the navigation button will be wired through the standard [home][android.R.id.home] menu select action.
     *
     * In order to use a Toolbar within the Activity's window content the application must not request the window
     * feature [FEATURE_SUPPORT_ACTION_BAR][android.view.Window.FEATURE_ACTION_BAR].
     *
     * @param toolbar Toolbar to set as the Activity's action bar, or `null` to clear it
     */
    fun setSupportActionBar(toolbar: Toolbar?)

    /**
     * @return The [AppCompatDelegate] being used by this Activity.
     */
    fun getDelegate(): AppCompatDelegate

}