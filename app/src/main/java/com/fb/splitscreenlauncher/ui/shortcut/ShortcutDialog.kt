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

import android.app.Activity
import android.app.Dialog
import android.app.PendingIntent
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.observe
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.fb.splitscreenlauncher.R
import com.fb.splitscreenlauncher.databinding.ShortcutDialogBinding
import com.fb.splitscreenlauncher.ui.settings.SettingsActivity
import com.fb.splitscreenlauncher.ui.settings.model.AppInfo
import com.fb.splitscreenlauncher.ui.settings.model.AppInfoViewHolder
import com.fb.splitscreenlauncher.util.Parameters
import com.fb.splitscreenlauncher.util.misc.ActivityExt
import com.fb.splitscreenlauncher.util.misc.toast
import kotlinx.android.synthetic.main.shortcut_dialog.view.*
import org.koin.android.viewmodel.ext.android.viewModel


class ShortcutDialog : DialogFragment() {


    companion object {

        private const val TAG = "SHORTCUT"


        fun show(context: AppCompatActivity,
                 args: Bundle = Bundle()) = ShortcutDialog().also {
            it.arguments = args
            it.show(context.supportFragmentManager, TAG)
        }

    }


    private val finishActivityOnDismiss: Boolean
        get() = arguments?.getBoolean(Parameters.FINISH_PARENT_AFTER_DISMISS) == true

    private val requestPinShortcut: Boolean
        get() = arguments?.getBoolean(Parameters.REQUEST_PINNED_SHORTCUT, true) == true


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val context = activity ?: throw IllegalStateException("ShortcutDialog")

        val model: ShortcutDialogViewModel by viewModel()

        with (model) {

            navigateBack.observe(this@ShortcutDialog) { shortcutInfo ->

                shortcutInfo?.let {

                    val result = ShortcutManagerCompat.createShortcutResultIntent(context, shortcutInfo)

                    // Request shortcut pinning
                    if (requestPinShortcut && ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {

                        val successCallback =
                            PendingIntent.getBroadcast(context, 0, result, 0)

                        ShortcutManagerCompat
                            .requestPinShortcut(context, shortcutInfo, successCallback.intentSender)

                    }

                    // Set result
                    activity?.setResult(Activity.RESULT_OK, result)
                }


                dismiss()

            }

            toasts.observe(this@ShortcutDialog) { context.toast(res = it) }

        }


        // View binding
        val binding = DataBindingUtil
            .inflate<ShortcutDialogBinding>(LayoutInflater.from(context), R.layout.shortcut_dialog, null, false)
            .apply {
                lifecycleOwner = this@ShortcutDialog
                viewModel = model
            }

        binding.root.recycler_view.setup {
            withDataSource(model.repo.dataSource)
            withItem<AppInfo, AppInfoViewHolder>(R.layout.shortcut_dialog_item_app) {
                onBind(::AppInfoViewHolder) { index, item ->

                    name.text = item.activityInfo
                        ?.loadLabel(context.packageManager)?.toString()
                        ?: getString(when (index) {
                            0 -> R.string.dialog_new_top_app
                            else -> R.string.dialog_new_bottom_app
                        })

                    icon.setImageDrawable(
                        item.activityInfo?.loadIcon(context.packageManager) ?:
                        context.getDrawable(R.drawable.ic_add_outline_24px))

                }
                onClick { index ->
                    (context as? ActivityExt)?.apply {

                        SettingsActivity.launch(this, SettingsActivity.PAGE_APP_PICKER, 123) { resultCode, data ->

                            val info =
                                data?.getParcelableExtra<ActivityInfo>(Parameters.RESULT_APP_PICK)

                            if (resultCode == Activity.RESULT_OK && info != null)
                                model.setApp(index, info)

                        }

                    }
                }
            }
        }


        // Create dialog
        return MaterialDialog(context).show {
            noAutoDismiss()
            customView(view = binding.root, scrollable = false, noVerticalPadding = true)
            @Suppress("DEPRECATION")
            neutralButton(R.string.dialog_new_switch_pos) { model.swapItems() }
            negativeButton(R.string.cancel) { dismiss() }
            positiveButton(R.string.save) { model.save() }
        }
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)


        if (finishActivityOnDismiss) activity?.finish()


    }


}
