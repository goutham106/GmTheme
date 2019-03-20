package com.gm.theme.inflator

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * Author     : Gowtham
 * Email      : goutham.gm11@gmail.com
 * Github     : https://github.com/goutham106
 * Created on : 12/03/19.
 */

interface ThemeInflationDelegate {

    /**
     * Hook you can supply that is called when inflating from a [ThemeLayoutInflater].
     *
     * @param parent The parent that the created view will be placed in; <em>note that this may be null</em>.
     * @param name Tag name to be inflated.
     * @param context The context the view is being created in.
     * @param attrs Inflation attributes as specified in XML file.
     * @return Newly created view. Return null for the default behavior.
     */
    fun createView(parent: View?, name: String, context: Context, attrs: AttributeSet): View?

}