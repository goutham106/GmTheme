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

package com.gm.theme

import android.app.Application
import android.content.res.Resources

/**
 * Author     : Gowtham
 * Email      : goutham.gm11@gmail.com
 * Github     : https://github.com/goutham106
 * Created on : 12/03/19.
 */
open class ThemeApp : Application() {

    private val resources: ThemeResources by lazy {
        ThemeResources(super.getResources(), theme)
    }

    /**
     * The [theme][Theme] instance used to create the application's resources
     */
    open val theme: Theme by lazy { Theme.instance }

    override fun onCreate() {
        super.onCreate()
        Theme.init(this, super.getResources())
    }

    override fun getResources(): Resources {
        return if (Theme.isInitialized()) resources else super.getResources()
    }

}