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

package com.fb.splitscreenlauncher

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityWindowInfo
import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import com.fb.splitscreenlauncher.ui.base.Parameters
import com.fb.splitscreenlauncher.util.LiveVar
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import kotlinx.coroutines.*


class ServiceAccessibility : AccessibilityServiceExt() {

    private var jobExpireLaunch: Job? = null
    private var launchState = LiveVar(0)
    private var intents : Pair<Intent, Intent>? = null


    override fun onCreate() {
        super.onCreate()

        launchState.observable
            .distinctUntilChanged()
            .`as`(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
            .subscribe { state ->

                serviceInfo = serviceInfo?.apply {
                    eventTypes =  when {
                        state > 0 -> AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_WINDOWS_CHANGED
                        else -> 0
                    }
                    feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
                    flags = when {
                        state > 0 -> AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
                        else -> 0
                    }
                    notificationTimeout = 200
                }

                 if (state == 0) {
                     jobExpireLaunch?.cancel()
                     intents = null
                 }

            }

    }


    override fun onInterrupt() {}


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId).apply {

            val first = intent?.getStringExtra(Parameters.FIRST)?.let { Intent.parseUri(it, 0) }
            val second = intent?.getStringExtra(Parameters.SECOND)?.let { Intent.parseUri(it, 0) }

            if (first != null && second != null) {

                intents = first to second
                launchState.value = 1

                PendingIntent
                    .getActivity(baseContext, 0, first, PendingIntent.FLAG_ONE_SHOT, Bundle()).send()


                // Expire launch after 4 seconds

                jobExpireLaunch = CoroutineScope(Dispatchers.Default).launch {
                    delay(4000)
                    launchState.value = 0
                }

            }

        }
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        intents?.also { shortcut ->
            when (launchState.value) {
                1 -> {

                    if (event?.packageName == shortcut.first.`package`) {

                        performGlobalAction(GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN)

                        launchState.value = 2

                    }
                }
                2 -> {

                    if (event?.packageName != shortcut.second.`package`
                        && windows.firstOrNull { it.type == AccessibilityWindowInfo.TYPE_SPLIT_SCREEN_DIVIDER } != null) {

                        PendingIntent
                            .getActivity(baseContext, 0, shortcut.second, PendingIntent.FLAG_ONE_SHOT, null)
                            .send()


                    } else if (event?.packageName == shortcut.second.`package`) {

                        launchState.value = 0

                    }
                }
            }
        }
    }


}


abstract class AccessibilityServiceExt: AccessibilityService(), LifecycleOwner {

    private val mDispatcher = ServiceLifecycleDispatcher(this)

    @CallSuper
    override fun onCreate() {
        mDispatcher.onServicePreSuperOnCreate()
        super.onCreate()
    }

    @CallSuper
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mDispatcher.onServicePreSuperOnStart()
        return super.onStartCommand(intent, flags, startId)
    }

    @CallSuper
    override fun onDestroy() {
        mDispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()
    }

    override fun getLifecycle(): Lifecycle {
        return mDispatcher.lifecycle
    }



}