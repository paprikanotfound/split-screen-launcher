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

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable


/**
 * Scale bitmap mataining ration to specific maximum size
 *
 * @param maxWidthAndHeight Maximum width or height to scale
 * @return scaled Bitmap
 */
fun Bitmap.scale(maxWidthAndHeight: Int): Bitmap {

    val newWidth: Int
    val newHeight: Int

    if (this.width >= this.height) {

        val ratio:Float = this.width.toFloat() / this.height.toFloat()
        newWidth = maxWidthAndHeight
        newHeight = Math.max(Math.round(maxWidthAndHeight / ratio), 1)

    } else {

        val ratio:Float = this.height.toFloat() / this.width.toFloat()
        newWidth = Math.round(maxWidthAndHeight / ratio)
        newHeight = Math.max(maxWidthAndHeight, 1)
    }

    return Bitmap.createScaledBitmap(this, newWidth, newHeight, false)
}


/** Bitmap to drawable */
fun Bitmap.asDrawable(res: Resources): BitmapDrawable = BitmapDrawable(res,this)

