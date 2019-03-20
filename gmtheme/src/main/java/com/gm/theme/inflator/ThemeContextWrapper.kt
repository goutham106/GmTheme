package com.gm.theme.inflator

import android.content.Context
import android.content.ContextWrapper
import com.gm.theme.inflator.decor.ThemeDecorator

/**
 * Author     : Gowtham
 * Email      : goutham.gm11@gmail.com
 * Github     : https://github.com/goutham106
 * Created on : 12/03/19.
 */
class ThemeContextWrapper(context: Context,
                          private val decorators: Array<ThemeDecorator>? = null,
                          private val viewFactory: ThemeViewFactory? = null)
    : ContextWrapper(context) {

    private val inflater: ThemeLayoutInflater by lazy {
        ThemeLayoutInflater(this).apply {
            this.decorators = this@ThemeContextWrapper.decorators
            this.viewFactory = this@ThemeContextWrapper.viewFactory
        }
    }

    override fun getSystemService(name: String): Any? = when (name) {
        LAYOUT_INFLATER_SERVICE -> inflater
        else -> super.getSystemService(name)
    }

}