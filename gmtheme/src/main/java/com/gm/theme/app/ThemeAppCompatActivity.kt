package com.gm.theme.app

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.gm.theme.ThemeResources
import com.gm.theme.delegate.ThemeDelegate

/**
 * Author     : Gowtham
 * Email      : goutham.gm11@gmail.com
 * Github     : https://github.com/goutham106
 * Created on : 12/03/19.
 */
abstract class ThemeAppCompatActivity : AppCompatActivity(), BaseThemeActivity {

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
        super.onCreate(savedInstanceState)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        delegate.onPostCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        delegate.onStart()
    }

    override fun onResume() {
        super.onResume()
        delegate.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        delegate.onCreateOptionsMenu(menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun getResources(): Resources = resources

}