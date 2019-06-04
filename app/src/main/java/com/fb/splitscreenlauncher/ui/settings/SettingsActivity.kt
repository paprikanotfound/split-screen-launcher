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

package com.fb.splitscreenlauncher.ui.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.fb.splitscreenlauncher.R
import com.fb.splitscreenlauncher.ui.base.BaseActivity
import com.fb.splitscreenlauncher.ui.base.Parameters
import com.fb.splitscreenlauncher.ui.base.ResultHandler
import kotlinx.android.synthetic.main.act_preferences.*


class SettingsActivity : BaseActivity() {


    companion object {

        const val PAGE_LICENSES = 2
        const val PAGE_APP_PICKER = 3


        fun launch(parent: Activity, page: Int) {

            val intent = Intent(parent, SettingsActivity::class.java)
                .putExtra(Parameters.PAGE_ID, page)

            parent.startActivity(intent)
        }

        fun launch(parent: BaseActivity, page: Int, requestCode: Int, onResult: ResultHandler) {

            val intent = Intent(parent, SettingsActivity::class.java).putExtra(Parameters.PAGE_ID, page)

            with (parent) {
                resultReceiver = requestCode to onResult
                startActivityForResult(intent, requestCode)
            }
        }

    }


    var showLoadingUI = false
        set(value) {
            field = value

            loading_indicator.visibility = if (field) View.VISIBLE else View.GONE
        }


    private val page: Int
        get() = intent?.extras?.getInt(Parameters.PAGE_ID, -1) ?: -1


    private val currentFragment: Fragment
        get() = when (page) {
            PAGE_LICENSES -> LicensesFragment.newInstance(intent?.extras)
            PAGE_APP_PICKER -> AppPickerFragment.newInstance(intent?.extras)
            else -> Fragment()
        }


    private val pageTitle: String
        get() = when (page) {
            PAGE_LICENSES -> getString(R.string.title_activity_licenses)
            PAGE_APP_PICKER -> getString(R.string.title_activity_app_picker)
            else -> ""
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.act_preferences)
        setSupportActionBar(toolbar)


        // Toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener { finish() }

        toolbar_title?.apply {
            text = pageTitle
            visibility = when {
                text.isNullOrEmpty() -> View.GONE
                else -> View.VISIBLE
            }
        }


        // Fragment

        if (savedInstanceState == null) {

            val fragment = currentFragment
            val oldFragment = supportFragmentManager.findFragmentById(R.id.container)

            if (!fragment::class.java.isInstance(oldFragment)) {
                // Only replace the Fragment if there was a change
                supportFragmentManager.commit {
                    replace(R.id.container, fragment)
                    setPrimaryNavigationFragment(fragment).apply {
                        if (oldFragment != null) {
                            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        }
                    }
                }
            }

        }

    }

}