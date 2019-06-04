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

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.fb.splitscreenlauncher.R
import com.fb.splitscreenlauncher.util.launchUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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