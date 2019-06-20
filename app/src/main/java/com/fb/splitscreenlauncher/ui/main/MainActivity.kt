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
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog
import com.fb.splitscreenlauncher.R
import com.fb.splitscreenlauncher.databinding.ActMainBinding
import com.fb.splitscreenlauncher.ui.base.BaseActivity
import com.fb.splitscreenlauncher.ui.settings.SettingsActivity
import com.fb.splitscreenlauncher.ui.shortcut.ShortcutDialog
import com.fb.splitscreenlauncher.util.Theme
import com.fb.splitscreenlauncher.util.openUrl
import com.fb.splitscreenlauncher.util.versionName
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import kotlinx.android.synthetic.main.act_main.*


class MainActivity: BaseActivity() {

    private companion object {

        private const val PRIVACY_URL = "https://franciscobarroso.me/privacy"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val model = ViewModelProviders.of(this)
            .get(MainViewModel::class.java)
            .apply {

                tapEvents.observe(this@MainActivity, androidx.lifecycle.Observer { event ->
                    event.handle { id ->
                        when (id) {
                            R.id.fab -> ShortcutDialog.show(this@MainActivity)
                        }
                    }
                })

                themeIsDark
                    .`as`(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this@MainActivity)))
                    .subscribe { isDark -> toolbar?.menu?.findItem(R.id.action_dark_theme)?.isChecked = isDark }

            }

        DataBindingUtil
            .setContentView<ActMainBinding>(this, R.layout.act_main)
            .apply {
                lifecycleOwner = this@MainActivity
                viewModel = model
            }

        toolbar.apply {
            inflateMenu(R.menu.menu_main)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_dark_theme -> {

                        Theme.set(if (!item.isChecked) Theme.THEME_DARK else Theme.THEME_LIGHT)

                    }
                    R.id.action_licenses -> {

                        SettingsActivity.launch(this@MainActivity, SettingsActivity.PAGE_LICENSES)

                    }
                    R.id.action_about -> {

                        MaterialDialog(this@MainActivity).show {
                            title(R.string.app_name)
                            message(text = Html.fromHtml(getString(R.string.dialog_about_body, this@MainActivity.versionName()),0)) {
                                html()
                                lineSpacing(1.2f)
                            }
                            negativeButton(R.string.privacy_policy) {
                                openUrl(PRIVACY_URL)
                            }
                            positiveButton(R.string.close)
                        }

                    }
                }
                true
            }
        }

    }

}
