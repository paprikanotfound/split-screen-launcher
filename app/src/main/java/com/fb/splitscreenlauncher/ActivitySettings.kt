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

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.afollestad.aesthetic.AestheticActivity
import com.afollestad.inlineactivityresult.internal.OnResult
import com.afollestad.inlineactivityresult.startActivityForResult
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import kotlinx.android.synthetic.main.layout_toolbar.toolbar
import kotlinx.android.synthetic.main.layout_toolbar.toolbar_title
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class Parameters {

    companion object {

        const val PAGE_ID = "page"

        const val RESULT_APP_PICK = "result_app_pick"

        const val FIRST = "first"
        const val SECOND = "second"
    }

}


class ActivitySettings : AestheticActivity() {


    companion object {

        const val PAGE_LICENSES = 2
        const val PAGE_APP_PICKER = 3


        fun launch(parent: AppCompatActivity, page: Int) {

            val intent = Intent(parent, ActivitySettings::class.java)
                .putExtra(Parameters.PAGE_ID, page)

            parent.startActivity(intent)
        }


        fun launch(parent: AppCompatActivity, page: Int, onResult: OnResult) {

            val intent = Intent(parent, ActivitySettings::class.java)
                .putExtra(Parameters.PAGE_ID, page)

            parent.startActivityForResult(intent, 100, onResult)

        }

    }


    var showLoadingUI = false
        set(value) {
            field = value


            // Update UI

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
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)


        // Toolbar

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {

                finish()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    // Fragments

    class LicensesFragment : PreferenceFragmentCompat() {


        companion object {

            fun newInstance(args: Bundle? = null): Fragment {
                return LicensesFragment().apply {
                    arguments = args
                }
            }

        }


        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

            setPreferencesFromResource(R.xml.preferences_empty, rootKey)

            CoroutineScope(Dispatchers.Main).launch {

                val titles = requireContext().resources.getStringArray(R.array.library_titles)
                val urls = requireContext().resources.getStringArray(R.array.library_urls)
                val licences = requireContext().resources.getStringArray(R.array.library_licenses)

                titles.forEachIndexed { index, _ ->

                    val pref = Preference(context).apply {
                        layoutResource = R.layout.layout_preference_material_small_icon // Make icons a bit smaller
                        title = titles[index]
                        summary = licences[index]
                        isIconSpaceReserved = false
                        onPreferenceClickListener = Preference.OnPreferenceClickListener {

                            requireContext().launchUrl(urls[index])

                            true
                        }
                    }

                    preferenceScreen.addPreference(pref)
                }

            }

        }

    }


    class AppPickerFragment : PreferenceFragmentCompat() {


        companion object {

            fun newInstance(args: Bundle? = null): Fragment {
                return AppPickerFragment().apply {
                    arguments = args
                }
            }

        }


        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

            setPreferencesFromResource(R.xml.preferences_empty, rootKey)

            CoroutineScope(Dispatchers.Main).launch {

                (activity as? ActivitySettings)?.showLoadingUI = true

                requireContext().packageManager.let { pm ->

                    val mainIntent = Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER)
                    val apps = pm.queryIntentActivities(mainIntent, 0)
                    Collections.sort(apps, ResolveInfo.DisplayNameComparator(pm))

                    apps

                }.forEach { info ->

                    val pref = Preference(context).apply {
                        layoutResource = R.layout.layout_preference_material_small_icon // Make icons a bit smaller
                        title = info.loadLabel(requireContext().packageManager).toString()
                        summary = info.activityInfo?.packageName
                        icon = info.loadIcon(requireContext().packageManager)
                        onPreferenceClickListener = Preference.OnPreferenceClickListener {

                            requireActivity().apply {

                                setResult(RESULT_OK, Intent()
                                    .putExtra(Parameters.RESULT_APP_PICK, info.activityInfo))

                                finish()
                            }

                            true
                        }
                    }

                    preferenceScreen.addPreference(pref)

                }

                (activity as? ActivitySettings)?.showLoadingUI = false

            }

        }

    }


}