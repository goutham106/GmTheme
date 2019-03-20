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

import android.content.res.Resources
import android.os.Build
import android.util.TypedValue
import com.gm.theme.utils.Reflection

/**
 * Author     : Gowtham
 * Email      : goutham.gm11@gmail.com
 * Github     : https://github.com/goutham106
 * Created on : 12/03/19.
 */

internal fun Resources.getKey(id: Int, resolveRefs: Boolean = true) = getValue(id, resolveRefs).let {
    it.assetCookie.toLong() shl 32 or it.data.toLong()
}

internal fun Resources.getValue(id: Int, resolveRefs: Boolean = true) = TypedValue().also { value ->
    (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Reflection.getFieldValue<Any?>(this, "mResourcesImpl")
    } else this)?.run {
        Reflection.invoke<Any?>(this, "getValue",
            arrayOf(Int::class.java, TypedValue::class.java, Boolean::class.java),
            id, value, resolveRefs)
    }
}