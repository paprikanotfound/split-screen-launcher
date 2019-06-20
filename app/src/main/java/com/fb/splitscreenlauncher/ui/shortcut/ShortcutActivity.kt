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
import android.os.Bundle
import android.provider.Settings
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.fb.splitscreenlauncher.R
import com.fb.splitscreenlauncher.ServiceAccessibility
import com.fb.splitscreenlauncher.ui.base.BaseActivity
import com.fb.splitscreenlauncher.ui.base.Parameters
import com.fb.splitscreenlauncher.util.isAccessibilityEnabled
import com.fb.splitscreenlauncher.util.toast


class ShortcutActivity: BaseActivity() {


    override val useAesthetic: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when {
            intent?.action == Intent.ACTION_CREATE_SHORTCUT -> {

                ShortcutDialog.show(this,
                    Bundle().apply {
                        putBoolean(Parameters.REQUEST_PINNED_SHORTCUT, false)
                        putBoolean(Parameters.FINISH_PARENT_AFTER_DISMISS, true)
                    })

            }
            !isAccessibilityEnabled() -> {

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
            else -> {

                Intent(baseContext, ServiceAccessibility::class.java)
                    .apply { putExtras(this@ShortcutActivity.intent.extras ?: Bundle()) }
                    .also { startService(it) }

                finish()

            }
        }

    }

}


