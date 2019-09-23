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

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import kotlin.math.max


fun Drawable.asBitmap(): Bitmap {
    return when (this) {
        is BitmapDrawable -> bitmap
        else -> {

            val width = max(if (bounds.isEmpty) intrinsicWidth else bounds.width(), 1)
            val height = max(if (bounds.isEmpty) intrinsicHeight else bounds.height(), 1)

            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also {
                val canvas = Canvas(it)
                setBounds(0, 0, canvas.width, canvas.height)
                draw(canvas)
            }
        }
    }
}


