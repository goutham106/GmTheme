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