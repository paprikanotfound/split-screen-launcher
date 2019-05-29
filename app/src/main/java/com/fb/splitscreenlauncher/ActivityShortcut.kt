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

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.Lifecycle
import com.afollestad.aesthetic.utils.tint
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import io.reactivex.Observable
import kotlinx.android.synthetic.main.dialog_create_shortcut.view.*
import java.lang.Exception


class ActivityShortcut : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when {
            intent?.action == Intent.ACTION_CREATE_SHORTCUT -> {

                showCreateShortcutDialog(true, false) {

                    setResult(Activity.RESULT_OK, it)

                    finish()

                }

            }
            !isAccessibilityEnabled() -> {

                MaterialDialog(this).show {
                    title(R.string.dialog_accessibility_title)
                    message(R.string.dialog_accessibility_msg)
                    onDismiss { finish() }
                    positiveButton(R.string.dialog_accessibility_confirm) {
                        try {

                            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))

                        } catch (ignored: Exception) {}
                    }
                }

            }
            else -> {

                startService(Intent(baseContext, ServiceAccessibility::class.java).also {
                    it.putExtras(this@ActivityShortcut.intent.extras ?: Bundle())
                })

                finish()

            }
        }

    }

}


fun AppCompatActivity.showCreateShortcutDialog(finishOnDismiss: Boolean = false,
                                               requestPinShortut: Boolean = true,
                                               onResult: ((intent: Intent) -> Unit)? = null) {

    MaterialDialog(this).show {
        title(R.string.dialog_new_title)
        noAutoDismiss()


        // Custom view

        customView(R.layout.dialog_create_shortcut)

        val view = getCustomView()

        val first = LiveVar(ActivityInfo())
        val second = LiveVar(ActivityInfo())

        val disposable = Observable
            .combineLatest(listOf(first.observable, second.observable)) { infos ->
                listOf(
                    infos[0]?.let { if ( it is ActivityInfo && !it.packageName.isNullOrEmpty()) it else null },
                    infos[1]?.let { if ( it is ActivityInfo && !it.packageName.isNullOrEmpty()) it else null }
                )
            }
            .subscribe {

                val infoFirst = it[0]
                val infoSecond = it[1]

                val labelFirst = infoFirst?.loadLabel(this@showCreateShortcutDialog.packageManager)
                val labelSecond = infoSecond?.loadLabel(this@showCreateShortcutDialog.packageManager)

                val iconFirst = infoFirst?.loadIcon(this@showCreateShortcutDialog.packageManager)
                    ?: resources
                        .getDrawable(R.drawable.ic_add_circle_black_24dp)
                        .tint(getThemeColor(android.R.attr.textColorSecondary))

                val iconSecond = infoSecond?.loadIcon(this@showCreateShortcutDialog.packageManager)
                    ?: resources
                        .getDrawable(R.drawable.ic_add_circle_black_24dp)
                        .tint(getThemeColor(android.R.attr.textColorSecondary))


                // first app

                view.app_first.findViewById<TextView>(android.R.id.title)
                    .text = labelFirst ?: getString(R.string.dialog_new_top_app)

                view.app_first.findViewById<AppCompatImageView>(android.R.id.icon)
                    .setImageDrawable(iconFirst)


                // second app

                view.app_second.findViewById<TextView>(android.R.id.title)
                    .text = labelSecond ?: getString(R.string.dialog_new_bottom_app)

                view.app_second.findViewById<AppCompatImageView>(android.R.id.icon)
                    .setImageDrawable(iconSecond)


                // label name

                view.edit_name.hint = when {
                    infoFirst == null || infoSecond == null -> getString(R.string.dialog_new_label_hint)
                    else -> "$labelFirst, $labelSecond"
                }


                // icon

                view.but_icon.apply {
                    setOnClickListener {  } // TODO: Switch icon styles
                    setImageBitmap(IconHelper.getIcon(IconHelper.STACKED, iconFirst, iconSecond))
                }

            }

        view.app_first.setOnClickListener {
            ActivitySettings.launch(this@showCreateShortcutDialog, ActivitySettings.PAGE_APP_PICKER) { _, data ->

                val packageInfo = data
                    .getParcelableExtra<ActivityInfo>(Parameters.RESULT_APP_PICK) ?: return@launch

                if (packageInfo.packageName == second.value.packageName) {

                    toast(res = R.string.warning_same_app_shortcut)

                    return@launch
                }

                first.value = packageInfo

            }
        }

        view.app_second.setOnClickListener {
            ActivitySettings.launch(this@showCreateShortcutDialog, ActivitySettings.PAGE_APP_PICKER) { _, data ->

                val packageInfo = data
                    .getParcelableExtra<ActivityInfo>(Parameters.RESULT_APP_PICK) ?: return@launch

                if (packageInfo.packageName == first.value.packageName) {

                    toast(res = R.string.warning_same_app_shortcut)

                    return@launch
                }

                second.value = packageInfo

            }
        }


        // Actions

        onDismiss {

            disposable.dispose()

            if (finishOnDismiss) finish()

        }
        neutralButton(R.string.dialog_new_switch_pos) {

            val temp = first.value
            first.value = second.value
            second.value = temp

        }
        positiveButton(R.string.save) {
            when {
                first.value.packageName.isNullOrEmpty() || second.value.packageName.isNullOrEmpty() -> {

                    toast(res = R.string.dialog_new_save_min_apps)

                }
                else -> {

                    dismiss()


                    // Generate shortcut intent

                    val shortcutId = "${first.value.packageName}/${second.value.packageName}"

                    val labelFirst = first.value.loadLabel(this@showCreateShortcutDialog.packageManager)
                    val labelSecond = second.value.loadLabel(this@showCreateShortcutDialog.packageManager)

                    val label = when (val input = view.edit_name?.text?.trim()?.toString() ?: "") {
                        "" -> "$labelFirst, $labelSecond"
                        else -> input
                    }

                    val intent = Intent(context, ActivityShortcut::class.java).apply {
                        putExtra(Parameters.FIRST, packageManager.getLaunchIntentForPackage(first.value.packageName)?.toUri(0))
                        putExtra(Parameters.SECOND, packageManager.getLaunchIntentForPackage(second.value.packageName)?.toUri(0))
                        action = Intent.ACTION_MAIN
                    }

                    val pinShortcutInfo = ShortcutInfoCompat
                        .Builder(this@showCreateShortcutDialog, shortcutId)
                        .setShortLabel(label)
                        .setLongLabel(label)
                        .setIcon(IconCompat.createWithBitmap(view.but_icon?.drawable?.asBitmap()))
                        .setIntent(intent)
                        .build()

                    val pinnedShortcutCallbackIntent = ShortcutManagerCompat
                        .createShortcutResultIntent(this@showCreateShortcutDialog, pinShortcutInfo)

                    val successCallback = PendingIntent
                        .getBroadcast(this@showCreateShortcutDialog, 0, pinnedShortcutCallbackIntent, 0)


                    // request pin shortcut

                    if (requestPinShortut
                        && ShortcutManagerCompat.isRequestPinShortcutSupported(this@showCreateShortcutDialog))
                        ShortcutManagerCompat.requestPinShortcut(this@showCreateShortcutDialog, pinShortcutInfo, successCallback.intentSender)


                    // return result intent

                    onResult?.invoke(pinnedShortcutCallbackIntent)

                }
            }
        }
        negativeButton(R.string.cancel) { dismiss() }
    }

}