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

import android.view.Menu
import android.view.MenuInflater
import androidx.fragment.app.Fragment
import com.gm.theme.Theme

/**
 * Author     : Gowtham
 * Email      : goutham.gm11@gmail.com
 * Github     : https://github.com/goutham106
 * Created on : 12/03/19.
 */
open class ThemeFragment : Fragment() {

    /**
     * The [Theme] instance used for styling.
     */
    open val theme: Theme get() = (activity as? BaseThemeActivity)?.theme ?: Theme.instance

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        applyMenuTint(menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    protected open fun applyMenuTint(menu: Menu) = theme.tint(menu, requireActivity())

}