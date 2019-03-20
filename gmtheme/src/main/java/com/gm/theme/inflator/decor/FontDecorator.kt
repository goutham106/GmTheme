package com.gm.theme.inflator.decor

import android.content.res.AssetManager
import android.content.res.TypedArray
import android.graphics.Typeface
import android.widget.TextView
import com.gm.theme.R

/**
 * Author     : Gowtham
 * Email      : goutham.gm11@gmail.com
 * Github     : https://github.com/goutham106
 * Created on : 12/03/19.
 */


/**
 * Set a font from assets by adding an attribute to the view in your layout XML.
 *
 * ```xml
 * <TextView
 *     ...
 *     app:gmFont="fonts/CustomFont.ttf"
 *     tools:ignore="MissingPrefix" />
 * ```
 */
class FontDecorator : AttrsDecorator<TextView>() {

    private val cache = mutableMapOf<String, Typeface>()

    override fun getType(): Class<TextView> = TextView::class.java

    override fun styleable(): IntArray = R.styleable.FontDecorator

    override fun apply(view: TextView, typedArray: TypedArray) {
        typedArray.getString(R.styleable.FontDecorator_gmFont)?.let { path ->
            view.typeface = getFont(view.context.assets, path)
        }
    }

    private fun getFont(assets: AssetManager, path: String): Typeface? {
        return cache[path] ?: run {
            try {
                Typeface.createFromAsset(assets, path)?.let { font ->
                    cache[path] = font
                    font
                }
            } catch (e: Exception) {
                null
            }
        }
    }

}