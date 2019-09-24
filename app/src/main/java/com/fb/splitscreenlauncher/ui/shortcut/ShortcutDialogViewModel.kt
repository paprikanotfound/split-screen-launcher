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
import android.graphics.drawable.Drawable
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fb.splitscreenlauncher.R
import com.fb.splitscreenlauncher.util.Parameters
import com.fb.splitscreenlauncher.util.misc.asBitmap
import com.fb.splitscreenlauncher.util.rx.ViewModelEvent


class ShortcutDialogViewModel(val app: Application,
                              val repo: ShortcutDialogRepo) : ViewModel() {


    val toasts = ViewModelEvent<Int>()
    val navigateBack = ViewModelEvent<ShortcutInfoCompat>()

    val resultIcon = MutableLiveData<Drawable?>()
    val labelHint = MutableLiveData<String>()

    private val customLabel: MutableLiveData<String> = MutableLiveData()


    init {

        invalidatePreview()

    }


    private fun invalidatePreview() {

        resultIcon.value = repo.getIcon()

        labelHint.value = repo.getLabel()

    }


    fun swapItems() {

        val temp = repo.dataSource[0].activityInfo
        repo.setApp(0, repo.dataSource[1].activityInfo)
        repo.setApp(1, temp)

        invalidatePreview()
    }


    fun save(): Boolean {

        val first = repo.dataSource[0].activityInfo
        val second = repo.dataSource[1].activityInfo

        return when {
            first == null || second == null -> {

                toasts.setValue(R.string.dialog_new_save_min_apps)

                false
            }
            else -> {

                val shortcutId = "${first.packageName}/${second.packageName}"

                val label =
                    (if (customLabel.value.isNullOrEmpty()) repo.getLabel() else customLabel.value) ?: ""

                val intent = Intent(app, ShortcutActivity::class.java).apply {
                    putExtra(Parameters.FIRST, app.packageManager.getLaunchIntentForPackage(first.packageName)?.toUri(0))
                    putExtra(Parameters.SECOND, app.packageManager.getLaunchIntentForPackage(second.packageName)?.toUri(0))
                    action = Intent.ACTION_MAIN
                }

                val shortcutInfo = ShortcutInfoCompat
                    .Builder(app, shortcutId)
                    .setShortLabel(label)
                    .setLongLabel(label)
                    .setIcon(IconCompat.createWithBitmap(resultIcon.value?.asBitmap()))
                    .setIntent(intent)
                    .build()


                // dismiss dialog & return result
                navigateBack.setValue(shortcutInfo)


                true
            }
        }

    }


    fun setApp(index: Int, info: ActivityInfo): Boolean {
        return when {
            info.packageName == repo.dataSource[1 - index].activityInfo?.packageName -> {

                toasts.setValue(R.string.warning_same_app_shortcut)

                false
            }
            else -> {

                repo.setApp(index, info)

                invalidatePreview()

                true
            }
        }
    }


    fun switchIconStyle() { }


    fun getCustomUserLabel(): String = customLabel.value ?: ""


    fun setCustomUserLabel(value: String) { customLabel.value = value }


}