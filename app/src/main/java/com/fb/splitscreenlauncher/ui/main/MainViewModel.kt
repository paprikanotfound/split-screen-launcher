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

import android.app.Application
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.databinding.Bindable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.fb.splitscreenlauncher.R
import com.fb.splitscreenlauncher.ui.base.Event
import com.fb.splitscreenlauncher.util.ld


data class ShortcutModel(val intentTop: Intent, val intentBottom: Intent, val icon: Drawable)


class MainViewModel(app: Application) : AndroidViewModel(app) {

    val tapActions = MutableLiveData<Event<Int>>()

    fun onTapMainFab() { tapActions.value = Event(R.id.fab) }

}