package com.gm.theme.app

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.preference.PreferenceActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatDelegate
import com.gm.theme.ThemeResources
import com.gm.theme.delegate.BaseAppCompatDelegate
import com.gm.theme.delegate.ThemeDelegate

/**
 * Author     : Gowtham
 * Email      : goutham.gm11@gmail.com
 * Github     : https://github.com/goutham106
 * Created on : 12/03/19.
 */
abstract class ThemePreferenceActivity : PreferenceActivity(),
    BaseAppCompatDelegate, BaseThemeActivity {

    private val appCompatDelegate: AppCompatDelegate by lazy {
        AppCompatDelegate.create(this, null)
    }

    private val delegate: ThemeDelegate by lazy {
        ThemeDelegate.create(this, theme, getThemeResId())
    }

    private val resources: ThemeResources by lazy {
        ThemeResources(super.getResources(), theme)
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(delegate.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        delegate.onCreate(savedInstanceState)
        appCompatDelegate.installViewFactory()
        appCompatDelegate.onCreate(savedInstanceState)
        super.onCreate(savedInstanceState)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        appCompatDelegate.onPostCreate(savedInstanceState)
        delegate.onPostCreate(savedInstanceState)
    }

    public override fun onStart() {
        super.onStart()
        delegate.onStart()
    }

    override fun onResume() {
        super.onResume()
        delegate.onResume()
    }

    override fun onPostResume() {
        super.onPostResume()
        appCompatDelegate.onPostResume()
    }

    override fun onStop() {
        super.onStop()
        appCompatDelegate.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        appCompatDelegate.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        appCompatDelegate.onConfigurationChanged(newConfig)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        delegate.onCreateOptionsMenu(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun invalidateOptionsMenu() {
        appCompatDelegate.invalidateOptionsMenu()
    }

    override fun onTitleChanged(title: CharSequence, color: Int) {
        super.onTitleChanged(title, color)
        appCompatDelegate.setTitle(title)
    }

    override fun setContentView(@LayoutRes layoutResID: Int) {
        appCompatDelegate.setContentView(layoutResID)
    }

    override fun setContentView(view: View) {
        appCompatDelegate.setContentView(view)
    }

    override fun setContentView(view: View, params: ViewGroup.LayoutParams) {
        appCompatDelegate.setContentView(view, params)
    }

    override fun addContentView(view: View, params: ViewGroup.LayoutParams) {
        appCompatDelegate.addContentView(view, params)
    }

    override fun getSupportActionBar(): ActionBar? = appCompatDelegate.supportActionBar

    override fun getMenuInflater(): MenuInflater = appCompatDelegate.menuInflater

    override fun getResources(): Resources = resources

    override fun getDelegate(): AppCompatDelegate = appCompatDelegate

}