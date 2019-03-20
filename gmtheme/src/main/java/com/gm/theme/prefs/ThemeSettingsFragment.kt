package com.gm.theme.prefs

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.XmlRes
import androidx.preference.*
import androidx.recyclerview.widget.RecyclerView
import com.gm.theme.R
import com.gm.theme.Theme
import com.gm.theme.app.BaseThemeActivity
import com.gm.theme.tinting.SystemBarTint
import com.gm.theme.utils.ColorUtils
import com.jaredrummler.android.colorpicker.ColorPreferenceCompat

/**
 * Author     : Gowtham
 * Email      : goutham.gm11@gmail.com
 * Github     : https://github.com/goutham106
 * Created on : 12/03/19.
 */
open class ThemeSettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener,
    Preference.OnPreferenceClickListener {

    private lateinit var prefThemePicker: Preference
    private lateinit var prefColorPrimary: ColorPreferenceCompat
    private lateinit var prefColorAccent: ColorPreferenceCompat
    private lateinit var prefColorBackground: ColorPreferenceCompat
    private lateinit var prefColorNavBar: SwitchPreferenceCompat

    /**
     * The [Theme] instance used for styling.
     */
    open val theme: Theme get() = (activity as? BaseThemeActivity)?.theme ?: Theme.instance

    /**
     * Get the preferences resource to load into the preference hierarchy.
     *
     * The preferences should contain a [ColorPreferenceCompat] for "pref_color_primary",
     * "pref_color_accent" and "pref_color_background".
     *
     * It should also contain preferences for "pref_theme_picker" and "pref_color_navigation_bar".
     *
     * @return The XML resource id to inflate
     */
    @XmlRes
    open fun getPreferenceXmlResId(): Int = R.xml.pref_gm

    /**
     * Sets whether to reserve the space of all Preference views. If set to false, all padding will be removed.
     *
     * By default, if the action bar is displaying home as up then padding will be added to the preference.
     */
    open val iconSpaceReserved = false
//    get() = (activity as? AppCompatActivity)?.supportActionBar?.displayOptions?.and(ActionBar.DISPLAY_HOME_AS_UP) != 0

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(getPreferenceXmlResId(), rootKey)

        prefThemePicker = findPreference(PREF_THEME_PICKER)
        prefColorPrimary = findPreference(PREF_COLOR_PRIMARY)
        prefColorAccent = findPreference(PREF_COLOR_ACCENT)
        prefColorBackground = findPreference(PREF_COLOR_BACKGROUND)
        prefColorNavBar = findPreference(PREF_COLOR_NAV_BAR)

        prefColorPrimary.saveValue(theme.primary)
        prefColorAccent.saveValue(theme.accent)
        prefColorBackground.saveValue(theme.backgroundColor)

        prefThemePicker.onPreferenceClickListener = this
        prefColorPrimary.onPreferenceChangeListener = this
        prefColorAccent.onPreferenceChangeListener = this
        prefColorBackground.onPreferenceChangeListener = this
        prefColorNavBar.onPreferenceChangeListener = this

        setupNavBarPref()
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        return when (preference) {
            prefThemePicker -> {
                activity?.run {
                    if (this is GmThemePickerLauncher) {
                        launchThemePicker()
                    } else {
                        startActivity(Intent(this, GmThemePickerActivity::class.java))
                    }
                }
                true
            }
            else -> false
        }
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        fun editTheme(action: (editor: Theme.Editor) -> Unit) {
            theme.edit {
                action(this)
            }.recreate(requireActivity(), smooth = true)
        }

        when (preference) {
            prefColorPrimary -> editTheme { it.primary(newValue as Int) }
            prefColorAccent -> editTheme { it.accent(newValue as Int) }
            prefColorBackground -> editTheme { it.background(newValue as Int) }
            prefColorNavBar -> editTheme { it.shouldTintNavBar(newValue as Boolean) }
            else -> return false
        }

        return true
    }

    override fun onCreateAdapter(preferenceScreen: PreferenceScreen): RecyclerView.Adapter<*> {
        return object : PreferenceGroupAdapter(preferenceScreen) {
            @SuppressLint("RestrictedApi")
            override fun onBindViewHolder(holder: PreferenceViewHolder, position: Int) {
                super.onBindViewHolder(holder, position)
                if (iconSpaceReserved) return
                // See: https://stackoverflow.com/a/51568782/1048340
                val preference = getItem(position)
                if (preference is PreferenceCategory) {
                    setZeroPaddingToLayoutChildren(holder.itemView)
                } else {
                    holder.itemView.findViewById<View>(R.id.icon_frame)?.let { iconFrame ->
                        iconFrame.visibility = if (preference.icon == null) View.GONE else View.VISIBLE
                    }
                }
            }
        }
    }

    // See: https://stackoverflow.com/a/51568782/1048340
    private fun setZeroPaddingToLayoutChildren(view: View) {
        if (view !is ViewGroup) return
        for (i in 0 until view.childCount) {
            setZeroPaddingToLayoutChildren(view.getChildAt(i))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                view.setPaddingRelative(0, view.paddingTop, view.paddingEnd, view.paddingBottom)
            } else {
                view.setPadding(0, view.paddingTop, view.paddingRight, view.paddingBottom)
            }
        }
    }

    private fun setupNavBarPref() {
        ColorUtils.isDarkColor(theme.primary, 0.75).let { isDarkEnough ->
            prefColorNavBar.isEnabled = isDarkEnough || Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        }
        val isColored = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity?.window?.navigationBarColor == theme.primary
        } else false
        prefColorNavBar.isChecked = theme.shouldTintNavBar || isColored
        val sysBarConfig = SystemBarTint(requireActivity()).sysBarConfig
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT || !sysBarConfig.hasNavigationBar) {
            findPreference<PreferenceCategory>(PREF_CATEGORY).run {
                removePreference(prefColorNavBar)
            }
        }
    }

    private inline fun <reified T : Preference> findPreference(key: String): T = super.findPreference(key) as T

    companion object {
        private const val PREF_CATEGORY = "gm_preference_category"
        private const val PREF_THEME_PICKER = "pref_theme_picker"
        private const val PREF_COLOR_PRIMARY = "pref_color_primary"
        private const val PREF_COLOR_ACCENT = "pref_color_accent"
        private const val PREF_COLOR_BACKGROUND = "pref_color_background"
        private const val PREF_COLOR_NAV_BAR = "pref_color_navigation_bar"

        fun newInstance() = ThemeSettingsFragment()
    }

}

/**
 * Let the hosting activity implement this to launch a custom theme picker from preferences
 */
interface GmThemePickerLauncher {

    /**
     * Launch a theme picker for Theme
     */
    fun launchThemePicker()
}