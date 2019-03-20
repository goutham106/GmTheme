package com.gm.theme.inflator

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.view.menu.ListMenuItemView
import androidx.appcompat.widget.AlertDialogLayout
import androidx.appcompat.widget.AppCompatDrawableManager
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.gm.theme.R
import com.gm.theme.Theme
import com.gm.theme.tinting.EdgeEffectTint
import com.gm.theme.tinting.WidgetTint
import com.gm.theme.utils.ColorUtils
import com.gm.theme.utils.Reflection
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputLayout

/**
 * Author     : Gowtham
 * Email      : goutham.gm11@gmail.com
 * Github     : https://github.com/goutham106
 * Created on : 12/03/19.
 */


/**
 * Class to process views in connection with the [ThemeLayoutInflater]. To add a [ThemeViewProcessor] you must
 * let your application or activity implement [ThemeViewProcessor.Provider]. When a view is created it will
 * call [ThemeViewProcessor.process] if [ThemeViewProcessor.shouldProcessView] returns true.
 */
abstract class ThemeViewProcessor<T : View> {

    /**
     * Process a newly created view.
     *
     * @param view
     * The newly created view.
     * @param attrs
     * The view's [attributes][AttributeSet]
     * @param theme
     * The [theme][Theme] instance used for styling views.
     */
    abstract fun process(view: T, attrs: AttributeSet?, theme: Theme)

    /**
     * Check if a view should be processed. By default, this checks if the view is an instance of [.getType].
     *
     * @param view
     * The view to check
     * @return True if this view should be processed.
     */
    open fun shouldProcessView(view: View) = getType().isInstance(view)

    /**
     * The class for the given view
     *
     * @return The class for T
     */
    protected abstract fun getType(): Class<T>

    /**
     * An interface that may be used in an [activity][Activity] to provide [view processors][ThemeViewProcessor]
     * to the [ThemeDelegate].
     */
    interface Provider {

        /**
         * Get an array of [view processors][ThemeViewProcessor] to style views.
         *
         * @return An array of decorators for the [ThemeDelegate].
         */
        fun getViewProcessors(): Array<ThemeViewProcessor<out View>>

    }

}

// ================================================================================================
// Processors

internal class AlertDialogProcessor : ThemeViewProcessor<View>() {

    override fun getType(): Class<View> = View::class.java

    override fun shouldProcessView(view: View): Boolean = view is AlertDialogLayout || CLASS_NAME == view.javaClass.name

    override fun process(view: View, attrs: AttributeSet?, theme: Theme) {
        view.setBackgroundColor(theme.backgroundColor) // Theme AlertDialog background
    }

    companion object {
        private const val CLASS_NAME = "com.android.internal.widget.AlertDialogLayout"
    }

}

internal class BottomAppBarProcessor : ThemeViewProcessor<BottomAppBar>() {

    override fun getType(): Class<BottomAppBar> = BottomAppBar::class.java

    override fun process(view: BottomAppBar, attrs: AttributeSet?, theme: Theme) {
        view.backgroundTint?.let { view.backgroundTint = theme.tinter.tint(it) }
        view.post {
            view.context?.let { context ->
                (context as? Activity)?.run {
                    theme.tint(view.menu, this)
                } ?: ((context as? ContextWrapper)?.baseContext as? Activity)?.run {
                    theme.tint(view.menu, this)
                }
            }
        }
    }

}

/**
 * A [ThemeViewProcessor] that styles [buttons][CompoundButton] in the overflow menu.
 */
@RequiresApi(Build.VERSION_CODES.M)
internal class CompoundButtonProcessor : ThemeViewProcessor<CompoundButton>() {

    override fun getType(): Class<CompoundButton> = CompoundButton::class.java

    @SuppressLint("PrivateResource")
    override fun process(view: CompoundButton, attrs: AttributeSet?, theme: Theme) {
        view.buttonTintList?.let { theme.tinter.tint(it) } ?: run {
            view.buttonTintList = theme.tinter.tint(
                view.context.getColorStateList(R.color.abc_tint_btn_checkable)
            )
        }
        val background = view.background
        if (background is RippleDrawable) {
            val resid = if (theme.isDark) R.color.ripple_material_dark else R.color.ripple_material_light
            val unchecked = ContextCompat.getColor(view.context, resid)
            val checked = ColorUtils.adjustAlpha(theme.accent, 0.4f)
            val csl = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_activated, -android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_activated),
                    intArrayOf(android.R.attr.state_checked)
                ),
                intArrayOf(unchecked, checked, checked)
            )
            background.setColor(csl)
        }
    }

}

internal class DatePickerProcessor : ThemeViewProcessor<DatePicker>() {

    override fun getType(): Class<DatePicker> = DatePicker::class.java

    override fun process(view: DatePicker, attrs: AttributeSet?, theme: Theme) {
        val datePickerId = view.context.resources.getIdentifier("date_picker_header", "id", "android")
        if (datePickerId != 0) {
            view.findViewById<ViewGroup>(datePickerId)?.let { layout ->
                theme.tinter.tint(layout.background)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    theme.tinter.tint(layout.backgroundTintList)
                }
            }
        }
    }

}

internal class FloatingActionButtonProcessor : ThemeViewProcessor<FloatingActionButton>() {

    override fun getType(): Class<FloatingActionButton> = FloatingActionButton::class.java

    override fun process(view: FloatingActionButton, attrs: AttributeSet?, theme: Theme) {
        theme.tinter.tint(view.backgroundTintList)
    }

}

@TargetApi(Build.VERSION_CODES.M)
internal class ImageButtonProcessor : ThemeViewProcessor<ImageButton>() {

    override fun getType(): Class<ImageButton> = ImageButton::class.java

    override fun process(view: ImageButton, attrs: AttributeSet?, theme: Theme) {
        theme.tinter.tint(view.background)
    }

}

/**
 * Style menu items
 */
internal class ListMenuItemViewProcessor : ThemeViewProcessor<View>() {

    override fun getType(): Class<View> = View::class.java

    override fun shouldProcessView(view: View): Boolean = view is ListMenuItemView || view.javaClass.name == CLASS_NAME

    override fun process(view: View, attrs: AttributeSet?, theme: Theme) {
        theme.tinter.tint(view)
    }

    companion object {
        private const val CLASS_NAME = "com.android.internal.view.menu.ListMenuItemView"
    }

}

internal class NavigationViewProcessor : ThemeViewProcessor<NavigationView>() {

    override fun getType(): Class<NavigationView> = NavigationView::class.java

    override fun process(view: NavigationView, attrs: AttributeSet?, theme: Theme) {
        val baseColor = if (theme.isDark) Color.WHITE else Color.BLACK
        val unselectedTextColor = ColorUtils.adjustAlpha(baseColor, 0.87f)
        val unselectedIconColor = ColorUtils.adjustAlpha(baseColor, 0.54f)
        val checkedColor = theme.accent

        view.apply {
            android.R.attr.state_checked
            itemTextColor = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_checked)
                ),
                intArrayOf(unselectedTextColor, checkedColor)
            )
            itemIconTintList = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_checked)
                ),
                intArrayOf(unselectedIconColor, checkedColor)
            )
        }
    }

}

@TargetApi(Build.VERSION_CODES.M)
internal class SearchAutoCompleteProcessor : ThemeViewProcessor<SearchView.SearchAutoComplete>() {

    override fun getType(): Class<SearchView.SearchAutoComplete> = SearchView.SearchAutoComplete::class.java

    override fun process(view: SearchView.SearchAutoComplete, attrs: AttributeSet?, theme: Theme) {
        WidgetTint.setCursorColor(view, theme.accent)
    }

}

@TargetApi(Build.VERSION_CODES.M)
internal class SwitchProcessor : ThemeViewProcessor<Switch>() {

    override fun getType(): Class<Switch> = Switch::class.java

    @SuppressLint("PrivateResource")
    override fun process(view: Switch, attrs: AttributeSet?, theme: Theme) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            theme.tinter.tint(view.thumbDrawable)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.trackTintList = ContextCompat.getColorStateList(view.context, R.color.abc_tint_switch_track)
        }
    }

}

internal class SwitchCompatProcessor : ThemeViewProcessor<SwitchCompat>() {

    override fun getType(): Class<SwitchCompat> = SwitchCompat::class.java

    @SuppressLint("RestrictedApi", "PrivateResource")
    override fun process(view: SwitchCompat, attrs: AttributeSet?, theme: Theme) {
        // SwitchCompat sets a ColorStateList on the drawable. Here, we get and modify the tint.
        val manager = AppCompatDrawableManager.get()
        Reflection.invoke<ColorStateList>(manager, "getTintList",
            arrayOf(Context::class.java, Int::class.java),
            view.context,
            androidx.appcompat.R.drawable.abc_switch_thumb_material
        )?.let { csl ->
            theme.tinter.tint(csl)
        }
    }

}

internal class TextInputLayoutProcessor : ThemeViewProcessor<TextInputLayout>() {

    override fun getType(): Class<TextInputLayout> = TextInputLayout::class.java

    override fun process(view: TextInputLayout, attrs: AttributeSet?, theme: Theme) {
        if (view.boxStrokeColor == Theme.getOriginalColor(R.color.gm_accent_reference)) {
            view.boxStrokeColor = theme.accent
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Reflection.getFieldValue<ColorStateList?>(view, "focusedTextColor")?.let { csl ->
                theme.tinter.tint(csl)
            }
        }
    }

}

internal class TextViewProcessor : ThemeViewProcessor<TextView>() {

    override fun getType(): Class<TextView> = TextView::class.java

    override fun process(view: TextView, attrs: AttributeSet?, theme: Theme) {
        view.textColors?.let { colors ->
            view.setTextColor(theme.tinter.tint(colors))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            theme.tinter.tint(view.backgroundTintList)
        }
        theme.tinter.tint(view.background)
    }

}

internal class TimePickerProcessor : ThemeViewProcessor<TimePicker>() {

    override fun getType(): Class<TimePicker> = TimePicker::class.java

    override fun process(view: TimePicker, attrs: AttributeSet?, theme: Theme) {
        theme.tinter.tint(view)
    }

}

@TargetApi(Build.VERSION_CODES.M)
internal class ViewGroupProcessor : ThemeViewProcessor<ViewGroup>() {

    override fun getType(): Class<ViewGroup> = ViewGroup::class.java

    override fun process(view: ViewGroup, attrs: AttributeSet?, theme: Theme) {
        EdgeEffectTint.setEdgeGlowColor(view, theme.primary)
        theme.tinter.tint(view.background)
        if (view is AbsListView) {
            WidgetTint.setFastScrollThumbColor(view, theme.accent)
        }
    }

}