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

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.afollestad.aesthetic.Aesthetic
import com.afollestad.aesthetic.AestheticActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_toolbar.*


class ActivityMain : AestheticActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Aesthetic.isFirstTime) Theme.set(Theme.THEME_LIGHT)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { showCreateShortcutDialog() }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_main, menu)

        Aesthetic.get().isDark
            .take(1)
            .`as`(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
            .subscribe { menu.findItem(R.id.action_dark_theme)?.isChecked = it }

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_dark_theme -> {

                item.isChecked = !item.isChecked

                Theme.set(if (item.isChecked) Theme.THEME_DARK else Theme.THEME_LIGHT)

                true
            }
            R.id.action_licenses -> {

                ActivitySettings.launch(this, ActivitySettings.PAGE_LICENSES)

                true
            }
            R.id.action_about -> {

                MaterialDialog(this).show {
                    title(
                        text = "${getString(R.string.app_name)} ${this@ActivityMain.versionName()}"
                    )
                    message(R.string.dialog_about_body) {
                        html()
                        lineSpacing(1.2f)
                    }
                    positiveButton(R.string.close)
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}
