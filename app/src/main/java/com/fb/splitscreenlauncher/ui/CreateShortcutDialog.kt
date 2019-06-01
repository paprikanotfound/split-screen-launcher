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

package com.fb.splitscreenlauncher.ui

import android.app.Activity
import android.app.Dialog
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import com.afollestad.aesthetic.utils.tint
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.fb.splitscreenlauncher.R
import com.fb.splitscreenlauncher.ui.base.BaseActivity
import com.fb.splitscreenlauncher.ui.base.Parameters
import com.fb.splitscreenlauncher.ui.settings.SettingsActivity
import com.fb.splitscreenlauncher.ui.shortcut.ShortcutActivity
import com.fb.splitscreenlauncher.util.*
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import io.reactivex.Observable
import kotlinx.android.synthetic.main.dialog_new_shortcut.view.*


class CreateShortcutDialog : DialogFragment() {


    companion object {

        private const val TAG = "CREATE_SHORTCUT"

        fun show(context: AppCompatActivity, args: Bundle = Bundle()) = CreateShortcutDialog()
            .also { it.arguments = args }
            .show(context.supportFragmentManager, TAG)

    }


    private val finishActivityOnDismiss: Boolean
        get() = arguments?.getBoolean(Parameters.FINISH_DISMISS) == true

    private val requestPinShortcut: Boolean
        get() = arguments?.getBoolean(Parameters.REQUEST_PINNED_SHORTCUT, true) == true


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { context ->

            MaterialDialog(context).show {
                title(R.string.dialog_new_title)
                noAutoDismiss()

                // Custom view

                customView(R.layout.dialog_new_shortcut, scrollable = true)

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
                    .`as`(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(context, Lifecycle.Event.ON_DESTROY)))
                    .subscribe {

                        val infoFirst = it[0]
                        val infoSecond = it[1]

                        val labelFirst = infoFirst?.loadLabel(context.packageManager)
                        val labelSecond = infoSecond?.loadLabel(context.packageManager)

                        val iconFirst = infoFirst?.loadIcon(context.packageManager)
                            ?: resources
                                .getDrawable(R.drawable.ic_add_circle_black_24dp, null)
                                .tint(context.getThemeColor(android.R.attr.textColorSecondary))

                        val iconSecond = infoSecond?.loadIcon(context.packageManager)
                            ?: resources
                                .getDrawable(R.drawable.ic_add_circle_black_24dp, null)
                                .tint(context.getThemeColor(android.R.attr.textColorSecondary))


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
                            setImageBitmap(IconUtil.getIcon(IconUtil.STACKED, iconFirst, iconSecond))
                        }

                    }

                view.app_first.setOnClickListener {
                    SettingsActivity.launch(
                        context as BaseActivity, SettingsActivity.PAGE_APP_PICKER, 123) { _, data ->

                        val packageInfo = data?.getParcelableExtra<ActivityInfo>(Parameters.RESULT_APP_PICK)
                            ?: return@launch

                        if (packageInfo.packageName == second.value.packageName) {

                            context.toast(res = R.string.warning_same_app_shortcut)

                            return@launch
                        }

                        first.value = packageInfo

                    }
                }

                view.app_second.setOnClickListener {
                    SettingsActivity.launch(
                        context as BaseActivity, SettingsActivity.PAGE_APP_PICKER, 123) { _, data ->

                        val packageInfo = data?.getParcelableExtra<ActivityInfo>(Parameters.RESULT_APP_PICK)
                            ?: return@launch

                        if (packageInfo.packageName == first.value.packageName) {

                            context.toast(res = R.string.warning_same_app_shortcut)

                            return@launch
                        }

                        second.value = packageInfo

                    }
                }

                // Callbacks

                val dismissDialog: DialogCallback = {

                    disposable.dispose()

                    if (finishActivityOnDismiss) context.finish()

                    this@CreateShortcutDialog.dismiss()
                }

                @Suppress("DEPRECATION")
                neutralButton(R.string.dialog_new_switch_pos) {

                    val temp = first.value
                    first.value = second.value
                    second.value = temp

                }
                positiveButton(R.string.save) {
                    when {
                        first.value.packageName.isNullOrEmpty() || second.value.packageName.isNullOrEmpty() -> {

                            context.toast(res = R.string.dialog_new_save_min_apps)

                        }
                        else -> {

                            // Generate shortcut intent

                            val shortcutId = "${first.value.packageName}/${second.value.packageName}"

                            val labelFirst = first.value.loadLabel(context.packageManager)
                            val labelSecond = second.value.loadLabel(context.packageManager)

                            val label = when (val input = view.edit_name?.text?.trim()?.toString() ?: "") {
                                "" -> "$labelFirst, $labelSecond"
                                else -> input
                            }

                            val intent = Intent(context, ShortcutActivity::class.java).apply {
                                putExtra(Parameters.FIRST, context.packageManager.getLaunchIntentForPackage(first.value.packageName)?.toUri(0))
                                putExtra(Parameters.SECOND, context.packageManager.getLaunchIntentForPackage(second.value.packageName)?.toUri(0))
                                action = Intent.ACTION_MAIN
                            }

                            val pinShortcutInfo = ShortcutInfoCompat
                                .Builder(context, shortcutId)
                                .setShortLabel(label)
                                .setLongLabel(label)
                                .setIcon(IconCompat.createWithBitmap(view.but_icon?.drawable?.asBitmap()))
                                .setIntent(intent)
                                .build()

                            val pinnedShortcutCallbackIntent = ShortcutManagerCompat
                                .createShortcutResultIntent(context, pinShortcutInfo)

                            val successCallback = PendingIntent
                                .getBroadcast(context, 0, pinnedShortcutCallbackIntent, 0)


                            // request pin shortcut
                            if (requestPinShortcut && ShortcutManagerCompat.isRequestPinShortcutSupported(context))
                                ShortcutManagerCompat
                                    .requestPinShortcut(context, pinShortcutInfo, successCallback.intentSender)


                            // set result OK
                            context.setResult(Activity.RESULT_OK, pinnedShortcutCallbackIntent)


                            // dismiss dialog
                            dismissDialog(it)
                        }
                    }
                }
                negativeButton(R.string.cancel, click = dismissDialog)

            }

        } ?: throw IllegalStateException("")
    }


}
