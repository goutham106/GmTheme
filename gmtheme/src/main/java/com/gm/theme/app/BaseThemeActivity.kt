package com.gm.theme.app

import androidx.annotation.StyleRes
import com.gm.theme.Theme

/**
 * Author     : Gowtham
 * Email      : goutham.gm11@gmail.com
 * Github     : https://github.com/goutham106
 * Created on : 12/03/19.
 */
interface BaseThemeActivity {
    /**
     * The [Theme] instance used for styling.
     */
    val theme: Theme get() = Theme.instance

    /**
     * Get the theme resource id. You can use a pre-defined theme in [GmThemes] or use your own theme that inherits
     * from a Theme based theme.
     *
     * If 0 is returned then Theme will determine whether to use a NoActionBar theme based on the current theme.
     *
     * @return A theme theme
     *
     * @see [GmThemes.actionBarTheme]
     * @see [GmThemes.noActionBarTheme]
     */
    @StyleRes
    fun getThemeResId(): Int = 0

}