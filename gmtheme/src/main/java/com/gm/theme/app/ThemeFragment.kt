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