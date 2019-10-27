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

package com.fb.splitscreenlauncher.ui.shortcut

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.fb.splitscreenlauncher.R
import com.fb.splitscreenlauncher.service.ServiceAccessibility
import com.fb.splitscreenlauncher.util.Parameters
import com.fb.splitscreenlauncher.util.SplitScreenModeUtil
import com.fb.splitscreenlauncher.util.misc.ActivityExt
import com.fb.splitscreenlauncher.util.misc.isAccessibilityEnabled
import com.fb.splitscreenlauncher.util.misc.ld
import com.fb.splitscreenlauncher.util.misc.toast


class ShortcutActivity: ActivityExt() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when {
            intent?.action == Intent.ACTION_CREATE_SHORTCUT -> {

                ShortcutDialog.show(this, Bundle().apply {
                    putBoolean(Parameters.REQUEST_PINNED_SHORTCUT, false)
                    putBoolean(Parameters.FINISH_PARENT_AFTER_DISMISS, true)
                })

            }
        }

    }

    override fun onResume() {
        super.onResume()

        when {
            intent?.action == Intent.ACTION_CREATE_SHORTCUT -> return
            isAccessibilityEnabled(ServiceAccessibility::class.java) -> {

                Intent(baseContext, ServiceAccessibility::class.java)
                    .apply { putExtras(this@ShortcutActivity.intent.extras ?: Bundle()) }
                    .also { startService(it) }

                finish()

            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {


                val first = intent?.getStringExtra(Parameters.FIRST)
                    ?.let { Intent.parseUri(it, 0) }
                    ?: return

                val second = intent?.getStringExtra(Parameters.SECOND)
                    ?.let { Intent.parseUri(it, 0) }
                    ?: return

                SplitScreenModeUtil().launchSplitScreenMode(this, first, second)

                finish()

            }
            else -> {

                MaterialDialog(this).show {
                    title(R.string.dialog_accessibility_title)
                    message(R.string.dialog_accessibility_msg)
                    onDismiss { finish() }
                    positiveButton(R.string.dialog_accessibility_confirm) {
                        try {

                            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))

                        } catch (ignored: Exception) {

                            toast(res = R.string.error_launch_accessibility)

                        }
                    }
                }

            }
        }

    }

}


