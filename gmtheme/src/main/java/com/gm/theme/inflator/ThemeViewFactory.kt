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

import android.util.AttributeSet
import android.view.View
import com.gm.theme.Theme
import java.util.*

/**
 * Author     : Gowtham
 * Email      : goutham.gm11@gmail.com
 * Github     : https://github.com/goutham106
 * Created on : 12/03/19.
 */
class ThemeViewFactory(val theme: Theme, vararg processors: ThemeViewProcessor<View>) {

    private val processors = hashSetOf<ThemeViewProcessor<View>>()

    init {
        Collections.addAll(this.processors, *processors)
    }

    fun onViewCreated(view: View, attrs: AttributeSet): View {
        for (processor in processors) {
            try {
                if (processor.shouldProcessView(view)) {
                    processor.process(view, attrs, theme)
                }
            } catch (e: Exception) {
                Theme.log(TAG, "Error processing view", e)
            }
        }
        return view
    }

    companion object {
        private const val TAG = "ThemeViewFactory"
    }

}