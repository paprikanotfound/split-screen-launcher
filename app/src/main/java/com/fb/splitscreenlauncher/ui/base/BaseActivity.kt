/*
 * Copyright (c) Francisco Barroso
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.fb.splitscreenlauncher.ui.base

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.aesthetic.Aesthetic
import com.fb.splitscreenlauncher.util.Theme


class ResultHandler(val callback: (requestCode: Int, resultCode: Int, data: Intent?) -> Unit) {

    fun invoke(requestCode: Int, resultCode: Int, data: Intent?) = callback.invoke(requestCode, resultCode, data)

}


abstract class BaseActivity: AppCompatActivity() {

    var resultReceiver: Pair<Int, ResultHandler>? = null

    open val useAesthetic: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        if (useAesthetic) {

            Aesthetic.attach(this)

            if (Aesthetic.isFirstTime) Theme.set(Theme.THEME_LIGHT)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (useAesthetic) Aesthetic.resume(this)
    }

    override fun onPause() {
        if (useAesthetic) Aesthetic.pause(this)
        super.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, result: Intent?) {
        super.onActivityResult(requestCode, resultCode, result)

        if (resultReceiver?.first == requestCode) {
            resultReceiver?.second?.callback?.invoke(requestCode, resultCode, result)
            resultReceiver = null
        }

    }

}