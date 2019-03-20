package com.gm.theme.delegate

import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.drawerlayout.widget.DrawerLayout
import com.gm.theme.Theme

/**
 * Author     : Gowtham
 * Email      : goutham.gm11@gmail.com
 * Github     : https://github.com/goutham106
 * Created on : 12/03/19.
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
@TargetApi(Build.VERSION_CODES.KITKAT)
internal open class ThemeDelegateImplV19(
    private val activity: Activity,
    private val theme: Theme,
    themeResId: Int
) : ThemeDelegateImplBase(activity, theme, themeResId) {

    override fun onPostCreate(savedInstanceState: Bundle?) {
        setupSystemBarTinting()
        super.onPostCreate(savedInstanceState)
    }

    private fun setupSystemBarTinting() {
        if (theme.isThemeModified && Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            val content = activity.findViewById<View>(android.R.id.content)
            if (content is ViewGroup) {
                if (content.childCount == 1) {
                    val child = content.getChildAt(0)
                    if (child !is DrawerLayout) {
                        child.fitsSystemWindows = true
                        val id = activity.resources.getIdentifier("config_enableTranslucentDecor", "bool", "android")
                        if (id != 0) {
                            val enabled = activity.resources.getBoolean(id)
                            if (enabled) {
                                activity.window.setFlags(
                                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                                )
                                if (theme.shouldTintNavBar) {
                                    activity.window.setFlags(
                                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}