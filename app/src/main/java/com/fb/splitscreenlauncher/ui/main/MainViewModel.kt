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

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.afollestad.aesthetic.Aesthetic
import com.fb.splitscreenlauncher.R
import com.fb.splitscreenlauncher.ui.base.Event


class MainViewModel : ViewModel() {

    val tapEvents = MutableLiveData<Event<Int>>()

    val themeIsDark
        get() = Aesthetic.get().isDark

    fun onTapMainFab() { tapEvents.value = Event(R.id.fab) }

}