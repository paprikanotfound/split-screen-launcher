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

package com.fb.splitscreenlauncher.ui.shortcut.dialog


import android.app.Application
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.databinding.Bindable
import androidx.lifecycle.*
import com.fb.splitscreenlauncher.R
import com.fb.splitscreenlauncher.ui.base.Event
import com.fb.splitscreenlauncher.ui.base.Parameters
import com.fb.splitscreenlauncher.ui.shortcut.ShortcutActivity
import com.fb.splitscreenlauncher.util.*


class ShortcutDialogViewModel(val app: Application) : AndroidViewModel(app) {


    companion object {

        const val KEY_TOP = 1
        const val KEY_BOTTOM = 2

    }


    private var iconStyle: Int = 0


    private val iconDefault: Drawable?
        get() = app.resources.getDrawable(R.drawable.ic_add_circle_black_24dp, null)

    private val _topInfo: MutableLiveData<ActivityInfo?> = MutableLiveData<ActivityInfo?>().also { it.value = null }
    private val _bottomInfo: MutableLiveData<ActivityInfo?> = MutableLiveData<ActivityInfo?>().also { it.value = null }

    val topActivityInfo: ActivityInfo?
        get() = _topInfo.value

    val bottomActivityInfo: ActivityInfo?
        get() = _bottomInfo.value

    val appPickerEvents = MutableLiveData<Event<Int>>()

    val topIcon: LiveData<Drawable?> = Transformations.map(_topInfo) {
        it?.loadIcon(app.packageManager) ?: iconDefault
    }
    val topLabel: LiveData<String> = Transformations.map(_topInfo) {
        it?.loadLabel(app.packageManager)?.toString() ?: app.getString(R.string.dialog_new_top_app)
    }

    val bottomIcon: LiveData<Drawable> = Transformations.map(_bottomInfo) {
        it?.loadIcon(app.packageManager) ?: iconDefault
    }
    val bottomLabel: LiveData<String> = Transformations.map(_bottomInfo) {
        it?.loadLabel(app.packageManager)?.toString() ?: app.getString(R.string.dialog_new_bottom_app)
    }

    val resultIcon: MutableLiveData<Drawable?> = MediatorLiveData<Drawable?>().apply {
        addSource(topIcon) {
            value = BitmapDrawable(app.resources, IconUtil.getIcon(iconStyle, it, bottomIcon.value))
        }
        addSource(bottomIcon) {
            value = BitmapDrawable(app.resources, IconUtil.getIcon(iconStyle, topIcon.value, it))
        }
    }
    val resultLabel: LiveData<String?> =  MediatorLiveData<String>().apply {
        addSource(topLabel) { value = "$it, ${bottomLabel.value}" }
        addSource(bottomLabel) { value = "${topLabel.value}, $it" }
    }

    val labelHint: LiveData<String> = Transformations.map(resultLabel) {
        if (_topInfo.value != null && _bottomInfo.value != null) it
        else app.getString(R.string.dialog_new_label_hint)
    }

    private val customLabel: MutableLiveData<String> = MutableLiveData()


    fun selectTopApp() { appPickerEvents.value = Event(KEY_TOP) }

    fun selectBottomApp() { appPickerEvents.value = Event(KEY_BOTTOM) }

    fun updateApp(itemKey: Int, info: ActivityInfo): Int {
        return when {
            itemKey == KEY_TOP && info.packageName == _bottomInfo.value?.packageName
                    || itemKey == KEY_BOTTOM && info.packageName == _topInfo.value?.packageName -> {

                R.string.warning_same_app_shortcut

            }
            itemKey == KEY_TOP -> {

                _topInfo.value = info

                -1
            }
            else -> {

                _bottomInfo.value = info

                -1
            }
        }
    }

    fun swapItems() {

        val temp = _topInfo.value
        _topInfo.value = _bottomInfo.value
        _bottomInfo.value = temp

    }

    fun save(): Any {

        val first = _topInfo.value
        val second = _bottomInfo.value

        return when {
            first == null || second == null -> {

                R.string.dialog_new_save_min_apps

            }
            else -> {

                val shortcutId = "${first.packageName}/${second.packageName}"

                val label =
                    (if (customLabel.value.isNullOrEmpty()) resultLabel.value else customLabel.value) ?: ""

                val intent = Intent(app, ShortcutActivity::class.java).apply {
                    putExtra(Parameters.FIRST, app.packageManager.getLaunchIntentForPackage(first.packageName)?.toUri(0))
                    putExtra(Parameters.SECOND, app.packageManager.getLaunchIntentForPackage(second.packageName)?.toUri(0))
                    action = Intent.ACTION_MAIN
                }

                val pinShortcutInfo = ShortcutInfoCompat
                    .Builder(app, shortcutId)
                    .setShortLabel(label)
                    .setLongLabel(label)
                    .setIcon(IconCompat.createWithBitmap(resultIcon.value?.asBitmap()))
                    .setIntent(intent)
                    .build()

                pinShortcutInfo
            }
        }

    }

    fun switchIconStyle() {}

    @Bindable fun getCustomUserLabel(): String {
        return customLabel.value ?: ""
    }

    fun setCustomUserLabel(value: String) {
        if (customLabel.value != value) {
            customLabel.value = value
        }
    }

}