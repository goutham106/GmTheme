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