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

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.fb.splitscreenlauncher.R
import com.fb.splitscreenlauncher.ui.base.Parameters
import com.fb.splitscreenlauncher.util.asBitmap
import com.fb.splitscreenlauncher.util.asDrawable
import com.fb.splitscreenlauncher.util.dpiToPx
import com.fb.splitscreenlauncher.util.scale
import kotlinx.coroutines.*
import java.util.*


class AppPickerFragment : PreferenceFragmentCompat() {


    companion object {

        fun newInstance(args: Bundle? = null): Fragment {
            return AppPickerFragment().apply {
                arguments = args
            }
        }

    }


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        CoroutineScope(Dispatchers.Main).launch {

            setPreferencesFromResource(R.xml.preferences_empty, rootKey)

            (activity as? SettingsActivity)?.showLoadingUI = true

            withContext(Dispatchers.Default) { getInstalledAppsSorted() }.forEach { info ->

                Preference(context).apply {
                    title = info.loadLabel(requireContext().packageManager).toString()
                    summary = info.activityInfo?.packageName
                    icon = info.loadIcon(requireContext().packageManager)
                        ?.asBitmap()
                        ?.scale(40.dpiToPx)
                        ?.asDrawable(context.resources)
                    onPreferenceClickListener = Preference.OnPreferenceClickListener {

                        with (requireActivity()) {

                            setResult(AppCompatActivity.RESULT_OK,
                                Intent().putExtra(Parameters.RESULT_APP_PICK, info.activityInfo))

                            finish()
                        }

                        true
                    }
                    preferenceScreen.addPreference(this)
                }

            }

            (activity as? SettingsActivity)?.showLoadingUI = false

        }

    }


    private fun getInstalledAppsSorted(): MutableList<ResolveInfo> {

        val pm = requireContext().packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER)
        val apps: MutableList<ResolveInfo> = pm.queryIntentActivities(mainIntent, 0)

        Collections.sort(apps, ResolveInfo.DisplayNameComparator(pm))

        return apps
    }


}