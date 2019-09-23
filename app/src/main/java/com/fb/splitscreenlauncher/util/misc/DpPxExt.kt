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
import kotlin.math.max


/** System density */
val density: Float
    get() = max(Resources.getSystem().displayMetrics.density, 1f)


/** Convert Int dpi value to pixel */
val Int.toPx: Int
    get() = (this * density).toInt()


/** Convert Double dpi value to pixel */
val Double.toPx: Double
    get() = (this * density).toInt().toDouble()


/** Convert Float dpi value to pixel */
val Float.toPx: Float
    get() = (this * density).toInt().toFloat()


/** Convert Int pixel to dpi */
val Int.toDp: Int
    get() = (this / density).toInt()


/** Convert Double pixel to dpi */
val Double.toDp: Double
    get() = (this / density).toInt().toDouble()


/** Convert Float pixel to dpi */
val Float.toDp: Float
    get() = (this / density).toInt().toFloat()