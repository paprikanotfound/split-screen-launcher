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

package com.fb.splitscreenlauncher.ui.main

import android.os.Bundle
import android.text.Html
import androidx.databinding.DataBindingUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.fb.splitscreenlauncher.R
import com.fb.splitscreenlauncher.databinding.MainActivityBinding
import com.fb.splitscreenlauncher.ui.settings.SettingsActivity
import com.fb.splitscreenlauncher.ui.shortcut.ShortcutDialog
import com.fb.splitscreenlauncher.util.BuildUtils
import com.fb.splitscreenlauncher.util.misc.ActivityExt
import com.fb.splitscreenlauncher.util.misc.launchUrl
import com.fb.splitscreenlauncher.util.misc.versionName
import com.fb.splitscreenlauncher.util.theme.ThemeHelper
import kotlinx.android.synthetic.main.main_activity.*
import org.koin.android.ext.android.inject


class MainActivity: ActivityExt() {


    private companion object {

        private const val PRIVACY_URL = "https://franciscobarroso.me/privacy"

    }


    private val themeHelper by inject<ThemeHelper>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        themeHelper.setup(this)


        DataBindingUtil
            .setContentView<MainActivityBinding>(this, R.layout.main_activity)
            .apply {
                lifecycleOwner = this@MainActivity
                parent = this@MainActivity
            }


        toolbar.apply {
            inflateMenu(R.menu.menu_main)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_app_theme -> launchThemeDialog()
                    R.id.action_about -> launchAboutDialog()
                    R.id.action_licenses -> SettingsActivity.launch(this@MainActivity, SettingsActivity.PAGE_LICENSES)
                }
                true
            }
        }


        text_msg.text = getString(when {
            BuildUtils.AT_LEAST_9 -> R.string.msg_start_indicator_28
            else -> R.string.msg_start_indicator
        })

    }


    private fun launchAboutDialog() {

        MaterialDialog(this@MainActivity).show {
            title(R.string.app_name)
            message(text = getString(R.string.dialog_about_body, versionName)) {
                html()
                lineSpacing(1.2f)
            }
            negativeButton(R.string.privacy_policy) { launchUrl(PRIVACY_URL) }
            positiveButton(R.string.close)
        }

    }


    private fun launchThemeDialog() {
        themeHelper.getModes().let { modes ->

            MaterialDialog(this@MainActivity).show {
                title(R.string.app_name)
                positiveButton(R.string.cancel)
                listItemsSingleChoice(
                    initialSelection = modes.indexOfFirst { it.id == themeHelper.curMode() },
                    waitForPositiveButton = false,
                    items = modes.map { getString(it.name) }
                ) { dialog, index, _ ->

                    dialog.dismiss()

                    themeHelper.setMode(modes[index].id)

                }
            }

        }
    }


    fun onTapNewShortcut() = ShortcutDialog.show(this@MainActivity)


}
