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

package com.gm.theme.prefs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import com.gm.theme.R
import com.gm.theme.Theme
import com.gm.theme.app.ThemeFragment

/**
 * Author     : Gowtham
 * Email      : goutham.gm11@gmail.com
 * Github     : https://github.com/goutham106
 * Created on : 12/03/19.
 */
open class GmThemePickerFragment : ThemeFragment(), AdapterView.OnItemClickListener {

    open val themesJsonAssetPath get() = "themes/gm_themes.json"

    private lateinit var gridView: GridView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.gm_theme_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gridView = view.findViewById(R.id.gridView)
        val themes = GmTheme.from(requireActivity().assets, themesJsonAssetPath)
        gridView.adapter = GmThemePickerAdapter(themes, theme)
        gridView.onItemClickListener = this
        scrollToCurrentTheme(themes)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val theme = (gridView.adapter as GmThemePickerAdapter).getItem(position)
        val themeName = theme.themeName
        Theme.log(TAG, "Clicked $themeName")
        theme.apply(this.theme).recreate(requireActivity(), smooth = true)
    }

    private fun scrollToCurrentTheme(themes: List<GmTheme>) {
        var selectedTheme = -1
        run {
            themes.forEachIndexed { index, theme ->
                if (theme.isMatchingColorScheme(this.theme)) {
                    selectedTheme = index
                    return@run
                }
            }
        }
        if (selectedTheme != -1) {
            gridView.post {
                if (selectedTheme < gridView.firstVisiblePosition || selectedTheme > gridView.lastVisiblePosition) {
                    gridView.setSelection(selectedTheme)
                }
            }
        }
    }

    companion object {
        private const val TAG = "ThemePickerFragment"

        fun newInstance() = GmThemePickerFragment()
    }

}