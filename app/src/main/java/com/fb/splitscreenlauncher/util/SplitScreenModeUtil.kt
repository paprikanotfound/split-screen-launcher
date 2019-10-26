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

import android.content.Intent
import android.os.Handler
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.fb.splitscreenlauncher.util.misc.ActivityOptionsFlags
import com.fb.splitscreenlauncher.util.misc.isCallable


/**
 * Utility class to launch two supported apps in Split-screen mode without
 * using the Accessibility API
 */
class SplitScreenModeUtil {

    @RequiresApi(28)
    fun launchSplitScreenMode(parent: FragmentActivity, intentTop: Intent, intentBottom: Intent) {
        with(parent) {

            lifecycle.addObserver(object : LifecycleObserver {

                @Suppress("unused")
                @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
                fun onStop() {


                    // Attempt to reset the previous window mode by starting the home activity
                    // after activity is loaded
                    Intent(Intent.ACTION_MAIN)
                        .apply {

                            addCategory(Intent.CATEGORY_HOME)

                            flags = Intent.FLAG_ACTIVITY_NO_ANIMATION or
                                    Intent.FLAG_ACTIVITY_NEW_TASK or
                                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED

                        }
                        .let {

                            if (it.isCallable(baseContext)) startActivity(it)

                        }


                    // Lastly we try to start both activities in split screen mode
                    Handler().postDelayed({

                        intentTop.apply {
                            addCategory(Intent.CATEGORY_LAUNCHER)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                        }

                        intentBottom.apply {
                            addCategory(Intent.CATEGORY_LAUNCHER)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                        }

                        val options = ActivityOptionsCompat.makeBasic().toBundle()?.apply {
                            putInt(
                                ActivityOptionsFlags.KEY_LAUNCH_WINDOWING_MODE,
                                ActivityOptionsFlags.WINDOWING_MODE_SPLIT_SCREEN_PRIMARY
                            )
                            putInt(
                                ActivityOptionsFlags.KEY_SPLIT_SCREEN_CREATE_MODE,
                                ActivityOptionsFlags.SPLIT_SCREEN_CREATE_MODE_TOP_OR_LEFT
                            )
                        }

                        startActivities(listOf(intentBottom, intentTop).toTypedArray(), options)

                        finish()

                    }, 500L)

                }

            })


            // Launch the top activity to attempt to reset the current window mode.
            // Without this step when activity is already open, it will be launched in a
            // fullscreen window
            intentTop
                .apply {

                    addCategory(Intent.CATEGORY_LAUNCHER)

                    flags = Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or
                            Intent.FLAG_ACTIVITY_NO_HISTORY or
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_NO_ANIMATION or
                            Intent.FLAG_ACTIVITY_TASK_ON_HOME
                }
                .let {

                    if (it.isCallable(baseContext)) startActivity(it)

                }


        }
    }

}
