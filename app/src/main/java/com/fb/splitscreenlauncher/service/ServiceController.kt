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

package com.fb.splitscreenlauncher.service

import android.app.Application
import android.content.Intent
import androidx.annotation.VisibleForTesting
import com.fb.splitscreenlauncher.util.rx.LiveVar
import kotlinx.coroutines.*


interface ServiceController {


    companion object {

        const val STATE_PAUSED = 0
        const val STATE_TRIGGER_SPLITSCREEN = 1
        const val STATE_LAUNCH_BOTTOM_APP = 2

    }


    var actionSplitScreen: () -> Unit
    val launchState: LiveVar<Int>


    fun onEvent(pkgName: String, isSplitScreenUIVisible: Boolean)


    fun launch(shortcutIntents: Pair<Intent, Intent>)


}


class ServiceControllerImpl(val app: Application): ServiceController {

    override var actionSplitScreen: () -> Unit = {  }
    override val launchState: LiveVar<Int> =  LiveVar(ServiceController.STATE_PAUSED)

    private var intents : Pair<Intent, Intent> = Intent() to Intent()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val job = Job()
    private val scope = CoroutineScope(job + Dispatchers.Default)


    override fun onEvent(pkgName: String, isSplitScreenUIVisible: Boolean) {
        when (launchState.value) {
            ServiceController.STATE_TRIGGER_SPLITSCREEN -> {

                if (pkgName == intents.first.`package`) {

                    actionSplitScreen()

                    launchState.value = ServiceController.STATE_LAUNCH_BOTTOM_APP

                }
            }
            ServiceController.STATE_LAUNCH_BOTTOM_APP -> {

                val eventIsFromSecondActivity = pkgName == intents.second.`package`

                when {
                    !eventIsFromSecondActivity && isSplitScreenUIVisible -> {

                        app.startActivity(intents.second)

//                        // Launch second app
//                        PendingIntent
//                            .getActivity(app, 0, intents.second, PendingIntent.FLAG_ONE_SHOT, null)
//                            .send()

                    }
                    isSplitScreenUIVisible -> {

                        launchState.value = ServiceController.STATE_PAUSED

                        job.cancel()

                    }
                }

            }
        }
    }


    override fun launch(shortcutIntents: Pair<Intent, Intent>) {

        if (launchState.value == ServiceController.STATE_PAUSED) {

            intents = shortcutIntents


            // Set next step to trigger split-screen mode
            launchState.value = ServiceController.STATE_TRIGGER_SPLITSCREEN


            // Launch first app
            app.startActivity(shortcutIntents.first)

//            PendingIntent
//                .getActivity(app, 0, intents.first, PendingIntent.FLAG_ONE_SHOT, Bundle())
//                .sendSafe()


            // Cancel shortcut event after timeout
            scope.launch {
                delay(4000L)
                launchState.value = ServiceController.STATE_PAUSED
            }

        }

    }


}