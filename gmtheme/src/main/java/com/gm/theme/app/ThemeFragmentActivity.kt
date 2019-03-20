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

package com.gm.theme.app

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.Menu
import androidx.fragment.app.FragmentActivity
import com.gm.theme.ThemeResources
import com.gm.theme.delegate.ThemeDelegate

/**
 * Author     : Gowtham
 * Email      : goutham.gm11@gmail.com
 * Github     : https://github.com/goutham106
 * Created on : 12/03/19.
 */
abstract class ThemeFragmentActivity : FragmentActivity(), BaseThemeActivity {

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