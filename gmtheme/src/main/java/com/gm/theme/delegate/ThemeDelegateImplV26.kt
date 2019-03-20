package com.gm.theme.delegate

import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.gm.theme.Theme
import com.gm.theme.tinting.SystemBarTint
import com.gm.theme.utils.ColorUtils

/**
 * Author     : Gowtham
 * Email      : goutham.gm11@gmail.com
 * Github     : https://github.com/goutham106
 * Created on : 12/03/19.
 */
@RequiresApi(Build.VERSION_CODES.O)
@TargetApi(Build.VERSION_CODES.O)
internal open class ThemeDelegateImplV26(
    private val activity: Activity,
    theme: Theme,
    themeResId: Int
) : ThemeDelegateImplV24(activity, theme, themeResId) {

    override fun tintNavigationBar(color: Int, tinter: SystemBarTint) {
        super.tintNavigationBar(color, tinter)
        if (!ColorUtils.isDarkColor(color)) {
            activity.window.decorView.run {
                systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
        }
    }

    companion object {
        private const val TAG = "ThemeDelegateImplV26"
    }

}