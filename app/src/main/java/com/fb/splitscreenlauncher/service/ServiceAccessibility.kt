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

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityWindowInfo
import androidx.lifecycle.Lifecycle
import com.fb.splitscreenlauncher.util.Parameters
import com.fb.splitscreenlauncher.util.lifecycle.LifecycleAccessibilityService
import com.fb.splitscreenlauncher.util.rx.withAutoDispose
import org.koin.android.ext.android.inject


class ServiceAccessibility : LifecycleAccessibilityService() {


    private val serviceController by inject<ServiceController>()


    override fun onCreate() {
        super.onCreate()

        serviceController.launchState.observable
            .distinctUntilChanged()
            .withAutoDispose(this, Lifecycle.Event.ON_DESTROY)
            .subscribe { state ->

                // Ignore Accessibility events when there is no shortcut is not being used
                serviceInfo?.apply {
                    feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
                    notificationTimeout = 10
                    eventTypes =  when {
                        state != ServiceController.STATE_PAUSED ->
                            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_WINDOWS_CHANGED
                        else -> 0
                    }
                    flags = when {
                        state != ServiceController.STATE_PAUSED ->
                            AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
                        else -> 0
                    }
                    serviceInfo = this
                }

            }

        serviceController
            .actionSplitScreen = { performGlobalAction(GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN) }

    }


    override fun onInterrupt() {}


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId).apply {

            val first = intent?.getStringExtra(Parameters.FIRST)?.let { Intent.parseUri(it, 0) }
            val second = intent?.getStringExtra(Parameters.SECOND)?.let { Intent.parseUri(it, 0) }

            if (first != null && second != null)
                serviceController.launch(first to second)

        }
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {

        serviceController.onEvent(
            event?.packageName?.toString() ?: "",
            windows.firstOrNull { it.type == AccessibilityWindowInfo.TYPE_SPLIT_SCREEN_DIVIDER } != null)

    }


}

