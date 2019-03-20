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