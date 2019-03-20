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