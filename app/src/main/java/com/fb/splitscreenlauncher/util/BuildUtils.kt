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

package com.fb.splitscreenlauncher.util

import android.annotation.SuppressLint
import android.os.Build


@SuppressLint("ObsoleteSdkInt")
object BuildUtils {

    val IS_10 = Build.VERSION.SDK_INT == Build.VERSION_CODES.Q
    val IS_9 = Build.VERSION.SDK_INT == Build.VERSION_CODES.P

    val AT_LEAST_10 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    val AT_LEAST_9 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
    val AT_LEAST_8_1 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1
    val AT_LEAST_8 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    val AT_LEAST_7_1 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1
    val AT_LEAST_7 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    val AT_LEAST_6 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    val AT_LEAST_5_1 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1
    val AT_LEAST_5_0 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

}