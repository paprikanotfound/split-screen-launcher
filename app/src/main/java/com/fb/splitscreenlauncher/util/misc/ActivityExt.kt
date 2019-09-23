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

package com.fb.splitscreenlauncher.util.misc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity


abstract class ActivityExt : AppCompatActivity() {


    var resultReceiver: ((resultCode: Int, data: Intent?) -> Any?)? = null


    override fun onActivityResult(requestCode: Int, resultCode: Int, result: Intent?) {
        super.onActivityResult(requestCode, resultCode, result)

        resultReceiver?.invoke(resultCode, result)
        resultReceiver = null

    }


    fun startActivityForResult(intent: Intent,
                               requestCode: Int,
                               callback: ((resultCode: Int, data: Intent?) -> Any?)) {

        resultReceiver = callback

        startActivityForResult(intent, requestCode)
    }


    override fun onDestroy() {
        super.onDestroy()

        resultReceiver = null
    }

}