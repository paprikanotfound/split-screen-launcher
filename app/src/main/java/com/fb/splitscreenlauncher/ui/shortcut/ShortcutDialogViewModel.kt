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

package com.fb.splitscreenlauncher.ui.shortcut


import android.app.Application
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.databinding.Bindable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.afollestad.recyclical.datasource.DataSource
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.fb.splitscreenlauncher.R
import com.fb.splitscreenlauncher.ui.base.Parameters
import com.fb.splitscreenlauncher.ui.model.AppInfo
import com.fb.splitscreenlauncher.util.IconUtil
import com.fb.splitscreenlauncher.util.asBitmap


class ShortcutDialogViewModel(val app: Application) : AndroidViewModel(app) {


    companion object {

        const val KEY_TOP = 1
        const val KEY_BOTTOM = 2

    }


    private var iconStyle: Int = 0


    val dataSource: DataSource<AppInfo> = dataSourceTypedOf(AppInfo(), AppInfo())

    val resultIcon = MutableLiveData<Drawable?>()
    val resultLabel = MutableLiveData<String?>()
    val labelHint = MutableLiveData<String>()

    private val customLabel: MutableLiveData<String> = MutableLiveData()


    fun swapItems() {

        val temp = dataSource[0].activityInfo
        dataSource[0].activityInfo = dataSource[1].activityInfo
        dataSource[1].activityInfo = temp
        dataSource.invalidateAll()

    }

    fun save(): Any {

        val first = dataSource[0].activityInfo
        val second = dataSource[1].activityInfo

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

    fun invalidatePreview() {

        val topInfo = dataSource[0].activityInfo
        val bottomInfo = dataSource[1].activityInfo

        resultIcon.value = BitmapDrawable(app.resources, IconUtil.getIcon(iconStyle,
            topInfo?.loadIcon(app.packageManager),
            bottomInfo?.loadIcon(app.packageManager)))

        resultLabel.value =
            "${topInfo?.loadLabel(app.packageManager)}, ${bottomInfo?.loadLabel(app.packageManager)}"

        labelHint.value =
            if (topInfo != null && bottomInfo != null) resultLabel.value
            else app.getString(R.string.dialog_new_label_hint)

    }

    fun switchIconStyle() { }


    @Bindable fun getCustomUserLabel(): String {
        return customLabel.value ?: ""
    }

    fun setCustomUserLabel(value: String) {
        if (customLabel.value != value) {
            customLabel.value = value
        }
    }

}