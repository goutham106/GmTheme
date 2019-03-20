package com.gm.theme

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.Keep
import androidx.annotation.MainThread
import com.gm.theme.Theme.BaseTheme.DARK
import com.gm.theme.Theme.BaseTheme.LIGHT
import com.gm.theme.Constants.LIGHT_ACTIONBAR_LUMINANCE_FACTOR
import com.gm.theme.Constants.NONE_TIMESTAMP
import com.gm.theme.Defaults.DEFAULT_DARKER_FACTOR
import com.gm.theme.Defaults.DEFAULT_LIGHTER_FACTOR
import com.gm.theme.PrefKeys.PREF_ACCENT
import com.gm.theme.PrefKeys.PREF_ACCENT_DARK
import com.gm.theme.PrefKeys.PREF_ACCENT_LIGHT
import com.gm.theme.PrefKeys.PREF_BACKGROUND_DARK
import com.gm.theme.PrefKeys.PREF_BACKGROUND_DARK_DARKER
import com.gm.theme.PrefKeys.PREF_BACKGROUND_DARK_LIGHTER
import com.gm.theme.PrefKeys.PREF_BACKGROUND_LIGHT
import com.gm.theme.PrefKeys.PREF_BACKGROUND_LIGHT_DARKER
import com.gm.theme.PrefKeys.PREF_BACKGROUND_LIGHT_LIGHTER
import com.gm.theme.PrefKeys.PREF_BASE_THEME
import com.gm.theme.PrefKeys.PREF_FILE_NAME
import com.gm.theme.PrefKeys.PREF_MENU_ICON_COLOR
import com.gm.theme.PrefKeys.PREF_NAVIGATION_BAR
import com.gm.theme.PrefKeys.PREF_PRIMARY
import com.gm.theme.PrefKeys.PREF_PRIMARY_DARK
import com.gm.theme.PrefKeys.PREF_PRIMARY_LIGHT
import com.gm.theme.PrefKeys.PREF_SHOULD_TINT_NAV_BAR
import com.gm.theme.PrefKeys.PREF_SHOULD_TINT_STATUS_BAR
import com.gm.theme.PrefKeys.PREF_SUB_MENU_ICON_COLOR
import com.gm.theme.PrefKeys.PREF_TIMESTAMP
import com.gm.theme.inflator.ThemeInflationDelegate
import com.gm.theme.inflator.ThemeLayoutInflater
import com.gm.theme.tinting.MenuTint
import com.gm.theme.tinting.ThemeTinter
import com.gm.theme.utils.ColorUtils
import kotlin.properties.Delegates

/**
 * Author     : Gowtham
 * Email      : goutham.gm11@gmail.com
 * Github     : https://github.com/goutham106
 * Created on : 12/03/19.
 */


/**
 * Contains colors for an application theme.
 *
 * Before using Theme you must initialize it in your application class or have the application class be [ThemeApp].
 *
 * To retrieve a color from a Theme based activity, simply call:
 *
 * ```kotlin
 * val primaryColor = theme.primary // application's primary color
 * val accentColor = theme.accent // application's accent color
 * ```
 *
 * To dynamically edit a theme you can use [Theme.Editor].
 *
 * Example:
 *
 * ```kotlin
 * Theme.instance.edit {
 *   primary(Color.RED)
 *   accent(Color.YELLOW)
 *   background(Color.BLACK)
 * }
 * ```
 *
 * After editing a theme you must recreate the activity for changes to apply.
 */
class Theme private constructor(private val prefs: SharedPreferences) {

    /** The primary color displayed most frequently across your app */
    @delegate:ColorInt
    var primary by Delegates.notNull<Int>()
        private set
    /** A lighter version of the [primary] color */
    @delegate:ColorInt
    var primaryLight by Delegates.notNull<Int>()
        private set
    /** A darker version of the [primary] color */
    @delegate:ColorInt
    var primaryDark by Delegates.notNull<Int>()
        private set

    /** The accent color that accents select parts of the UI */
    @delegate:ColorInt
    var accent by Delegates.notNull<Int>()
        private set
    /** A lighter version of the [accent] color */
    @delegate:ColorInt
    var accentLight by Delegates.notNull<Int>()
        private set
    /** A darker version of the [accent] color */
    @delegate:ColorInt
    var accentDark by Delegates.notNull<Int>()
        private set

    /** The background color used as the underlying color of the app's content */
    val backgroundColor: Int
        get() = when (baseTheme) {
            LIGHT -> backgroundLight
            DARK -> backgroundDark
        }
    /* A lighter version of the [background] color */
    val backgroundColorLight: Int
        get() = when (baseTheme) {
            LIGHT -> backgroundLightLighter
            DARK -> backgroundDarkLighter
        }
    /* A darker version of the [background] color */
    val backgroundColorDark: Int
        get() = when (baseTheme) {
            LIGHT -> backgroundLightDarker
            DARK -> backgroundDarkDarker
        }

    /** The color of icons in a [Menu] */
    @delegate:ColorInt
    var menuIconColor by Delegates.notNull<Int>()
        private set
    /** The color of icons in a [menu's][Menu] sub-menu */
    @delegate:ColorInt
    var subMenuIconColor by Delegates.notNull<Int>()
        private set

    /** The color of the navigation bar, usually is black or the [primary] color */
    @delegate:ColorInt
    var navigationBar by Delegates.notNull<Int>()
        private set
    /** True to set the [primaryDark] color on the system status bar */
    var shouldTintStatusBar by Delegates.notNull<Boolean>()
        private set
    /** True to set the [navigationBar] color on the system navigation bar */
    var shouldTintNavBar by Delegates.notNull<Boolean>()
        private set

    /** The base theme. Either [LIGHT] or [DARK] */
    var baseTheme by Delegates.notNull<BaseTheme>()
        internal set
    /** True if the [baseTheme] is [DARK] */
    val isDark get() = baseTheme == DARK
    /** True if the [baseTheme] is [LIGHT] */
    val isLight get() = baseTheme == LIGHT
    /** True if the [primary] color is a dark color */
    val isActionBarDark get() = ColorUtils.isDarkColor(primary, 0.75)
    /** True if the [primary] color is a light color */
    val isActionBarLight get() = !isActionBarDark
    /** True if the theme has been modified at least once */
    val isThemeModified get() = timestamp != NONE_TIMESTAMP

    /** Helper to tint a [Drawable], [ColorStateList] or a [View] */
    val tinter by lazy { ThemeTinter() }
    val themes by lazy { GmThemes(this) }

    @delegate:ColorInt
    internal var backgroundDark by Delegates.notNull<Int>()
    @delegate:ColorInt
    internal var backgroundDarkLighter by Delegates.notNull<Int>()
    @delegate:ColorInt
    internal var backgroundDarkDarker by Delegates.notNull<Int>()
    @delegate:ColorInt
    internal var backgroundLight by Delegates.notNull<Int>()
    @delegate:ColorInt
    internal var backgroundLightLighter by Delegates.notNull<Int>()
    @delegate:ColorInt
    internal var backgroundLightDarker by Delegates.notNull<Int>()

    internal var timestamp: Long = 0L
        private set

    init {
        loadDefaults()
    }

    /**
     * Tint all items and sub-menu items in a [menu][Menu]
     *
     * @param menu the Menu to tint
     * @param activity the current Activity
     * @param forceIcons False to hide sub-menu icons from showing. True by default.
     */
    @JvmOverloads
    fun tint(menu: Menu, activity: Activity, forceIcons: Boolean = true) =
        MenuTint(menu,
            menuIconColor = menuIconColor,
            subIconColor = subMenuIconColor,
            forceIcons = forceIcons
        ).apply(activity)

    /**
     * Create a new [Editor] to edit this instance
     */
    fun edit() = Editor(this)

    /**
     * Reset all theme values. The activity must be recreated after resetting.
     */
    fun reset() = prefs.edit().clear().apply().also { loadDefaults() }.run { Recreator() }

    /**
     * Creates a new editor and applys any edits in the action parameter
     */
    inline fun edit(action: Theme.Editor.() -> Unit) = edit().also { editor -> action(editor) }.apply()

    private fun loadDefaults() {
        primary = prefs.getInt(PREF_PRIMARY,
            res.getColor(R.color.gm_primary_reference))
        primaryDark = prefs.getInt(PREF_PRIMARY_DARK,
            res.getColor(R.color.gm_primary_dark_reference))
        primaryLight = prefs.getInt(PREF_PRIMARY_LIGHT,
            res.getColor(R.color.gm_primary_light_reference))

        accent = prefs.getInt(PREF_ACCENT,
            res.getColor(R.color.gm_accent_reference))
        accentDark = prefs.getInt(PREF_ACCENT_DARK,
            res.getColor(R.color.gm_accent_dark_reference))
        accentLight = prefs.getInt(PREF_ACCENT_LIGHT,
            res.getColor(R.color.gm_accent_light_reference))

        backgroundLight = prefs.getInt(PREF_BACKGROUND_LIGHT,
            res.getColor(R.color.gm_bg_light))
        backgroundLightDarker = prefs.getInt(PREF_BACKGROUND_LIGHT_DARKER,
            res.getColor(R.color.gm_bg_light_darker))
        backgroundLightLighter = prefs.getInt(PREF_BACKGROUND_LIGHT_LIGHTER,
            res.getColor(R.color.gm_bg_light_lighter))

        backgroundDark = prefs.getInt(PREF_BACKGROUND_DARK,
            res.getColor(R.color.gm_bg_dark))
        backgroundDarkDarker = prefs.getInt(PREF_BACKGROUND_DARK_DARKER,
            res.getColor(R.color.gm_bg_dark_darker))
        backgroundDarkLighter = prefs.getInt(PREF_BACKGROUND_DARK_LIGHTER,
            res.getColor(R.color.gm_bg_dark_lighter))

        baseTheme = getBaseTheme(prefs, res)

        menuIconColor = prefs.getInt(PREF_MENU_ICON_COLOR,
            res.getColor(if (isActionBarLight) R.color.gm_menu_icon_dark else R.color.gm_menu_icon_light))
        subMenuIconColor = prefs.getInt(PREF_SUB_MENU_ICON_COLOR,
            res.getColor(if (baseTheme == LIGHT) R.color.gm_sub_menu_icon_dark else R.color.gm_sub_menu_icon_light))

        navigationBar = prefs.getInt(PREF_NAVIGATION_BAR,
            res.getColor(R.color.gm_navigation_bar_reference))

        shouldTintStatusBar = prefs.getBoolean(PREF_SHOULD_TINT_STATUS_BAR,
            res.getBoolean(R.bool.should_tint_status_bar))
        shouldTintNavBar = prefs.getBoolean(PREF_SHOULD_TINT_NAV_BAR,
            res.getBoolean(R.bool.should_tint_nav_bar))

        timestamp = prefs.getLong(PREF_TIMESTAMP, NONE_TIMESTAMP)

        setDefaultDarkerAndLighterColors()
    }

    private fun setDefaultDarkerAndLighterColors() {
        // We use a transparent primary|accent dark|light colors so the library user
        // is not required to specify a color value for for accent|primary light|dark
        // If the theme is using the transparent (fake) primary dark color, we need
        // to update our color values and create light|dark variants for them.
        if (primaryDark == getOriginalColor(R.color.gm_default_primary_dark)) {
            primaryDark = ColorUtils.darker(primary, DEFAULT_DARKER_FACTOR)
        }
        if (primaryLight == getOriginalColor(R.color.gm_default_primary_light)) {
            primaryLight = ColorUtils.lighter(primary, DEFAULT_LIGHTER_FACTOR)
        }
        if (accentDark == getOriginalColor(R.color.gm_default_accent_dark)) {
            accentDark = ColorUtils.darker(accent, DEFAULT_DARKER_FACTOR)
        }
        if (accentLight == getOriginalColor(R.color.gm_default_accent_light)) {
            accentLight = ColorUtils.lighter(accent, DEFAULT_LIGHTER_FACTOR)
        }
    }

    companion object {

        @SuppressLint("StaticFieldLeak") // application context is safe
        internal lateinit var app: Application
        lateinit var res: Resources

        /**
         * Initialize Cyanea. This should be done in the [application][Application] class.
         */
        @JvmStatic
        fun init(app: Application, res: Resources) {
            this.app = app
            this.res = res
        }

        /**
         * Check if Cyanea has been initialized.
         *
         * @see [init]
         */
        @JvmStatic
        fun isInitialized(): Boolean {
            return try {
                app
                res
                true
            } catch (e: UninitializedPropertyAccessException) {
                false
            }
        }

        private object Holder {

            val INSTANCE: Theme
                get() {
                    try {
                        val preferences = app.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
                        return Theme(preferences)
                    } catch (e: UninitializedPropertyAccessException) {
                        throw IllegalStateException("Theme.init must be called before referencing the singleton instance")
                    }
                }
        }

        private val instances by lazy { mutableMapOf<String, Theme>() }

        /**
         * The singleton [Theme] instance that you can use throughout the application.
         */
        @JvmStatic
        val instance: Theme by lazy { Holder.INSTANCE }

        /**
         * Get a instance of [Theme] by name. This will create a new instance if none exist.
         *
         * This allows you to have more than one color scheme in an app. You must override Activity#getTheme().
         */
        @JvmStatic
        fun getInstance(name: String): Theme {
            instances[name]?.let { theme ->
                return theme
            } ?: run {
                val preferences = app.getSharedPreferences(name, Context.MODE_PRIVATE)
                val theme = Theme(preferences)
                instances[name] = theme
                return theme
            }
        }

        /**
         * Intercept and create views at inflation time
         *
         * @delegate The delegate used to intercept and create views
         */
        @JvmStatic
        @MainThread
        fun setInflationDelegate(delegate: ThemeInflationDelegate) {
            ThemeLayoutInflater.inflationDelegate = delegate
        }

        /**
         * Turns on logging for the [Theme] library
         */
        @JvmStatic
        var loggingEnabled = false

        @JvmStatic
        fun log(tag: String, msg: String, ex: Throwable? = null) {
            if (loggingEnabled) {
                Log.d(tag, msg, ex)
            }
        }

        /**
         * Get the original color of a color resource.
         *
         * @param resid The color resource to retrieve
         */
        @JvmStatic
        @ColorInt
        fun getOriginalColor(@ColorRes resid: Int): Int = res.getColor(resid)

        private fun getBaseTheme(prefs: SharedPreferences, res: Resources): BaseTheme {
            val themeName = prefs.getString(PREF_BASE_THEME, null)
            return when (themeName) {
                LIGHT.name -> LIGHT
                DARK.name -> DARK
                else -> {
                    TypedValue().also {
                        app.theme?.resolveAttribute(android.R.attr.windowBackground, it, true)
                    }.let {
                        return if (it.type >= TypedValue.TYPE_FIRST_COLOR_INT && it.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                            if (ColorUtils.isDarkColor(it.data, LIGHT_ACTIONBAR_LUMINANCE_FACTOR)) DARK else LIGHT
                        } else if (res.getBoolean(R.bool.is_default_theme_light)) LIGHT else DARK
                    }
                }
            }
        }

    }

    /**
     * An editor for Theme to change colors and other values
     */
    @Suppress("MemberVisibilityCanBePrivate")
    class Editor internal constructor(private val theme: Theme) {

        private val editor = theme.prefs.edit()

        /**
         * Set the [primary] color using a color resource.
         *
         * The [primaryDark], [primaryLight], [navigationBar], and [menuIconColor] will also be updated to match the theme.
         */
        fun primaryResource(@ColorRes resid: Int) = primary(res.getColor(resid))

        /** Set the [primary] dark color using a color resource. */
        fun primaryDarkResource(@ColorRes resid: Int) = primaryDark(res.getColor(resid))

        /** Set the [primary] light color using a color resource. */
        fun primaryLightResource(@ColorRes resid: Int) = primaryLight(res.getColor(resid))

        /**
         * Set the [accent] dark color using a color resource.
         *
         * The [accentDark] and [accentLight] colors will also be updated.
         */
        fun accentResource(@ColorRes resid: Int): Editor = accent(res.getColor(resid))

        /** Set the [accent] dark color using a color resource. */
        fun accentDarkResource(@ColorRes resid: Int) = accentDark(res.getColor(resid))

        /** Set the [accent] light color using a color resource. */
        fun accentLightResource(@ColorRes resid: Int) = accentLight(res.getColor(resid))

        /**
         * Set the background color using a color resource.
         *
         * The [baseTheme], [backgroundLight], [backgroundDark] and [subMenuIconColor] will also be updated.
         */
        fun backgroundResource(@ColorRes resid: Int) = background(res.getColor(resid))

        /** Set the background color for a [LIGHT] theme using a color resource. */
        fun backgroundLightResource(@ColorRes resid: Int) = backgroundLight(res.getColor(resid))

        /** Set the background dark color for a [LIGHT] theme using a color resource. */
        fun backgroundLightDarkerResource(@ColorRes resid: Int) = backgroundLightDarker(res.getColor(resid))

        /** Set the background light color for a [LIGHT] theme using a color resource. */
        fun backgroundLightLighterResource(@ColorRes resid: Int) = backgroundLightLighter(res.getColor(resid))

        /** Set the background color for a [DARK] theme using a color resource. */
        fun backgroundDarkResource(@ColorRes resid: Int) = backgroundDark(res.getColor(resid))

        /** Set the background dark color for a [DARK] theme using a color resource. */
        fun backgroundDarkDarkerResource(@ColorRes resid: Int) = backgroundDarkDarker(res.getColor(resid))

        /** Set the background light color for a [DARK] theme using a color resource. */
        fun backgroundDarkLighterResource(@ColorRes resid: Int) = backgroundDarkLighter(res.getColor(resid))

        /** Set the [menuIconColor] using a color resource */
        fun menuIconColorResource(@ColorRes resid: Int) = menuIconColor(res.getColor(resid))

        /** Set the [subMenuIconColor] using a color resource */
        fun subMenuIconColorResource(@ColorRes resid: Int) = subMenuIconColor(res.getColor(resid))

        /** Set the [navigationBar] color using a color resource */
        fun navigationBarResource(@ColorRes resid: Int) = navigationBar(res.getColor(resid))

        /**
         * Set the [primary] color using a color resource.
         *
         * The [primaryDark], [primaryLight], [navigationBar], and [menuIconColor] will also be updated to match the theme.
         */
        fun primary(@ColorInt color: Int): Editor {
            theme.primary = color
            editor.putInt(PREF_PRIMARY, color)
            val isDarkColor = ColorUtils.isDarkColor(color, LIGHT_ACTIONBAR_LUMINANCE_FACTOR)
            val menuIconColorRes = if (isDarkColor) R.color.gm_menu_icon_light else R.color.gm_menu_icon_dark
            primaryDark(ColorUtils.darker(color, DEFAULT_DARKER_FACTOR))
            primaryLight(ColorUtils.lighter(color, DEFAULT_LIGHTER_FACTOR))
            menuIconColor(res.getColor(menuIconColorRes))
            navigationBar(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O || isDarkColor) color else Color.BLACK)
            return this
        }

        /** Set the [primary] dark color using a color resource. */
        fun primaryDark(@ColorInt color: Int): Editor {
            theme.primaryDark = color
            editor.putInt(PREF_PRIMARY_DARK, color)
            return this
        }

        /** Set the [primary] light color using a color resource. */
        fun primaryLight(@ColorInt color: Int): Editor {
            theme.primaryLight = color
            editor.putInt(PREF_PRIMARY_LIGHT, color)
            return this
        }

        /**
         * Set the [accent] dark color using a color resource.
         *
         * The [accentDark] and [accentLight] colors will also be updated.
         */
        fun accent(@ColorInt color: Int): Editor {
            theme.accent = color
            editor.putInt(PREF_ACCENT, color)
            accentDark(ColorUtils.darker(color, DEFAULT_DARKER_FACTOR))
            accentLight(ColorUtils.lighter(color, DEFAULT_LIGHTER_FACTOR))
            return this
        }

        /** Set the [accent] dark color using a color resource. */
        fun accentDark(@ColorInt color: Int): Editor {
            theme.accentDark = color
            editor.putInt(PREF_ACCENT_DARK, color)
            return this
        }

        /** Set the [accent] light color using a color resource. */
        fun accentLight(@ColorInt color: Int): Editor {
            theme.accentLight = color
            editor.putInt(PREF_ACCENT_LIGHT, color)
            return this
        }

        /**
         * Set the background color using a color resource.
         *
         * The [baseTheme], [backgroundLight], [backgroundDark] and [subMenuIconColor] will also be updated.
         */
        fun background(@ColorInt color: Int): Editor {
            val lighter = ColorUtils.lighter(color, DEFAULT_LIGHTER_FACTOR)
            val darker = ColorUtils.darker(color, DEFAULT_DARKER_FACTOR)
            val isDarkColor = ColorUtils.isDarkColor(color, LIGHT_ACTIONBAR_LUMINANCE_FACTOR)
            if (isDarkColor) {
                baseTheme(BaseTheme.DARK)
                backgroundDark(color)
                backgroundDarkDarker(darker)
                backgroundDarkLighter(lighter)
                subMenuIconColor(res.getColor(R.color.gm_sub_menu_icon_light))
            } else {
                baseTheme(BaseTheme.LIGHT)
                backgroundLight(color)
                backgroundLightDarker(darker)
                backgroundLightLighter(lighter)
                subMenuIconColor(res.getColor(R.color.gm_sub_menu_icon_dark))
            }
            return this
        }

        /** Set the background color for a [LIGHT] theme using a literal (hardcoded) color integer. */
        fun backgroundLight(@ColorInt color: Int): Editor {
            theme.backgroundLight = color
            editor.putInt(PREF_BACKGROUND_LIGHT, color)
            return this
        }

        /** Set the background dark color for a [LIGHT] theme using a literal (hardcoded) color integer. */
        fun backgroundLightDarker(@ColorInt color: Int): Editor {
            theme.backgroundDarkDarker = color
            editor.putInt(PREF_BACKGROUND_LIGHT_DARKER, color)
            return this
        }

        /** Set the background light color for a [LIGHT] theme using a literal (hardcoded) color integer. */
        fun backgroundLightLighter(@ColorInt color: Int): Editor {
            theme.backgroundLightLighter = color
            editor.putInt(PREF_BACKGROUND_LIGHT_LIGHTER, color)
            return this
        }

        /** Set the background color for a [DARK] theme using a literal (hardcoded) color integer. */
        fun backgroundDark(@ColorInt color: Int): Editor {
            theme.backgroundDark = color
            editor.putInt(PREF_BACKGROUND_DARK, color)
            return this
        }

        /** Set the background dark color for a [DARK] theme using a literal (hardcoded) color integer. */
        fun backgroundDarkDarker(@ColorInt color: Int): Editor {
            theme.backgroundDarkDarker = color
            editor.putInt(PREF_BACKGROUND_DARK_DARKER, color)
            return this
        }

        /** Set the background light color for a [DARK] theme using a literal (hardcoded) color integer. */
        fun backgroundDarkLighter(@ColorInt color: Int): Editor {
            theme.backgroundDarkLighter = color
            editor.putInt(PREF_BACKGROUND_DARK_LIGHTER, color)
            return this
        }

        /** Set the [menuIconColor] using a literal (hardcoded) color integer */
        fun menuIconColor(@ColorInt color: Int): Editor {
            theme.menuIconColor = color
            editor.putInt(PREF_MENU_ICON_COLOR, color)
            return this
        }

        /** Set the [subMenuIconColor] using a literal (hardcoded) color integer */
        fun subMenuIconColor(@ColorInt color: Int): Editor {
            theme.subMenuIconColor = color
            editor.putInt(PREF_SUB_MENU_ICON_COLOR, color)
            return this
        }

        /** Set the [navigationBar] color using a literal (hardcoded) color integer */
        fun navigationBar(@ColorInt color: Int): Editor {
            theme.navigationBar = color
            editor.putInt(PREF_NAVIGATION_BAR, color)
            return this
        }

        /** Set whether or not to tint the system status bar */
        fun shouldTintStatusBar(choice: Boolean): Editor {
            theme.shouldTintStatusBar = choice
            editor.putBoolean(PREF_SHOULD_TINT_STATUS_BAR, choice)
            return this
        }

        /** Set whether or not to tint the system navigation bar */
        fun shouldTintNavBar(choice: Boolean): Editor {
            theme.shouldTintNavBar = choice
            editor.putBoolean(PREF_SHOULD_TINT_NAV_BAR, choice)
            return this
        }

        /** Set the base theme. Either [LIGHT] or [DARK]. This should correlate with the [backgroundColor] */
        fun baseTheme(theme: BaseTheme): Editor {
            this.theme.baseTheme = theme
            editor.putString(PREF_BASE_THEME, theme.name)
            return this
        }

        /**
         * Apply preferences to the editor. For theme changes to be applied you must recreate the activity.
         */
        fun apply(): Recreator {
            theme.timestamp = System.currentTimeMillis()
            editor.putLong(PREF_TIMESTAMP, theme.timestamp)
            editor.apply()
            return Recreator()
        }

    }

    /**
     * Helper to recreate a modified themed activity
     */
    class Recreator {

        /**
         * Recreate the current activity
         *
         * @param activity The current activity
         * @param delay The delay in milliseconds until the activity is recreated
         * @param smooth True to use a fade-in/fade-out animation when re-creating.
         * Use with caution, this will create a new instance of the activity.
         */
        @JvmOverloads
        fun recreate(activity: Activity, delay: Long = DEFAULT_DELAY, smooth: Boolean = false) {
            Handler().postDelayed({
                activity.run {
                    if (smooth) {
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish()
                    } else {
                        recreate()
                    }
                }
            }, delay)
        }

        companion object {
            private const val DEFAULT_DELAY = 200L
        }

    }

    /**
     * Callback when a theme has been modified and the [Activity] has been recreated.
     */
    interface ThemeModifiedListener {

        /**
         * Called in [onResume][Activity.onResume] of an [Activity] when the theme has been modified.
         */
        fun onThemeModified()
    }

    @Keep
    enum class BaseTheme {
        LIGHT,
        DARK
    }

}