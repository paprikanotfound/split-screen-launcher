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
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.fb.splitscreenlauncher.R
import com.fb.splitscreenlauncher.ui.base.BaseActivity
import com.fb.splitscreenlauncher.ui.base.Parameters
import com.fb.splitscreenlauncher.ui.settings.SettingsActivity
import com.fb.splitscreenlauncher.databinding.DialogShortcutBinding
import com.fb.splitscreenlauncher.ui.base.ResultHandler
import com.fb.splitscreenlauncher.ui.model.AppInfo
import com.fb.splitscreenlauncher.ui.model.AppInfoViewHolder
import com.fb.splitscreenlauncher.util.ld
import com.fb.splitscreenlauncher.util.toast
import kotlinx.android.synthetic.main.dialog_shortcut.view.*


class ShortcutDialog : DialogFragment() {


    companion object {

        private const val TAG = "SHORTCUT"


        fun show(context: AppCompatActivity, args: Bundle = Bundle())
                = ShortcutDialog().also {
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

        val model = ViewModelProviders.of(this)
            .get(ShortcutDialogViewModel::class.java)

        val binding = DataBindingUtil
            .inflate<DialogShortcutBinding>(LayoutInflater.from(context), R.layout.dialog_shortcut, null, false)
            .apply {
                lifecycleOwner = this@ShortcutDialog
                viewModel = model
            }

        binding.root.recycler_view.setup {
            withDataSource(model.dataSource)
            withItem<AppInfo, AppInfoViewHolder>(R.layout.item_app) {
                onBind(::AppInfoViewHolder) { index, item ->

                    name.text = item.activityInfo
                        ?.loadLabel(context.packageManager)?.toString()
                        ?: getString(when (index) {
                            0 -> R.string.dialog_new_top_app
                            else -> R.string.dialog_new_bottom_app
                        })

                    icon.setImageDrawable(
                        item.activityInfo?.loadIcon(context.packageManager) ?:
                        context.getDrawable(R.drawable.ic_add_circle_black_24dp))

                }
                onClick { index ->
                    SettingsActivity.launch(activity as BaseActivity, SettingsActivity.PAGE_APP_PICKER, 123,
                        ResultHandler { _, resultCode, data ->

                            val info = data?.getParcelableExtra<ActivityInfo>(Parameters.RESULT_APP_PICK)
                            val indexDuplicate = if (index == 0) 1 else 0

                            when {
                                info != null && info.packageName == model.dataSource[indexDuplicate].activityInfo?.packageName -> {

                                    context.toast(res = R.string.warning_same_app_shortcut)

                                }
                                resultCode == Activity.RESULT_OK && info != null -> {

                                    model.dataSource[index].activityInfo = info
                                    model.dataSource.invalidateAt(index)
                                    model.invalidatePreview()
                                }
                            }
                        })
                }
            }
        }

        @Suppress("DEPRECATION")
        return MaterialDialog(context).show {
            title(R.string.dialog_new_title)
            noAutoDismiss()
            customView(view = binding.root, scrollable = true)
            neutralButton(R.string.dialog_new_switch_pos) { model.swapItems() }
            positiveButton(R.string.save) {
                when (val result = model.save()) {
                    is Int -> context.toast(res = result)
                    is ShortcutInfoCompat -> {

                        val resultIntent = ShortcutManagerCompat.createShortcutResultIntent(context, result)

                        // request pin shortcut
                        if (requestPinShortcut && ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {

                            val successCallback =
                                PendingIntent.getBroadcast(context, 0, resultIntent, 0)

                            ShortcutManagerCompat.requestPinShortcut(context, result, successCallback.intentSender)

                        }

                        // set result
                        requireActivity().setResult(Activity.RESULT_OK, resultIntent)

                        // dismiss dialog
                        dismiss()
                    }
                }
            }
            negativeButton(R.string.cancel) { dismiss() }
        }
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        if (finishActivityOnDismiss) activity?.finish()

    }


}
