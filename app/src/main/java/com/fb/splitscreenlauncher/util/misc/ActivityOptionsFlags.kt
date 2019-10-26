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


object ActivityOptionsFlags {

    /** The primary container driving the screen to be in split-screen mode. */
    val WINDOWING_MODE_SPLIT_SCREEN_PRIMARY = 3

    /**
     * The containers adjacent to the {@link #WINDOWING_MODE_SPLIT_SCREEN_PRIMARY} container in
     * split-screen mode.
     * NOTE: Containers launched with the windowing mode with APIs like
     * {@link ActivityOptions#setLaunchWindowingMode(int)} will be launched in
     * {@link #WINDOWING_MODE_FULLSCREEN} if the display isn't currently in split-screen windowing
     * mode
     * @see #WINDOWING_MODE_FULLSCREEN_OR_SPLIT_SCREEN_SECONDARY
     */
    val WINDOWING_MODE_SPLIT_SCREEN_SECONDARY = 4

    /**
     * Parameter to {@link android.app.IActivityManager#setTaskWindowingModeSplitScreenPrimary}
     * which specifies the position of the created docked stack at the top half of the screen if
     * in portrait mode or at the left half of the screen if in landscape mode.
     * @hide
     */
    val SPLIT_SCREEN_CREATE_MODE_TOP_OR_LEFT = 0

    /**
     * Parameter to {@link android.app.IActivityManager#setTaskWindowingModeSplitScreenPrimary}
     * which
     * specifies the position of the created docked stack at the bottom half of the screen if
     * in portrait mode or at the right half of the screen if in landscape mode.
     * @hide
     */
    val SPLIT_SCREEN_CREATE_MODE_BOTTOM_OR_RIGHT = 1

    /**
     * The windowing mode the activity should be launched into.
     * @hide
     */
    val KEY_LAUNCH_WINDOWING_MODE = "android.activity.windowingMode"

    /**
     * Where the split-screen-primary stack should be positioned.
     * @hide
     */
    val KEY_SPLIT_SCREEN_CREATE_MODE = "android:activity.splitScreenCreateMode"

}