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
import android.content.pm.ActivityInfo
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.afollestad.recyclical.datasource.DataSource
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.fb.splitscreenlauncher.R
import com.fb.splitscreenlauncher.ui.settings.model.AppInfo
import com.fb.splitscreenlauncher.util.icon.IconUtil


interface ShortcutDialogRepo {


    val dataSource: DataSource<AppInfo>


    fun getIcon(): Drawable


    fun getLabel(): String


    fun setApp(index: Int, info: ActivityInfo?)


    fun clear()

}


class ShortcutDialogRepoImpl(val app: Application): ShortcutDialogRepo {


    private var iconStyle: Int = 0


    override val dataSource by lazy { dataSourceTypedOf(AppInfo(), AppInfo()) }


    override fun getIcon(): Drawable {

        val topInfo = dataSource[0].activityInfo
        val bottomInfo = dataSource[1].activityInfo

        return BitmapDrawable(app.resources,
            IconUtil.getIcon(iconStyle,
                topInfo?.loadIcon(app.packageManager),
                bottomInfo?.loadIcon(app.packageManager)))

    }


    override fun getLabel(): String {

        val topInfo = dataSource[0].activityInfo
        val bottomInfo = dataSource[1].activityInfo

        return when {
            topInfo == null || bottomInfo == null -> app.getString(R.string.dialog_new_label_hint)
            else -> "${topInfo.loadLabel(app.packageManager)}, ${bottomInfo.loadLabel(app.packageManager)}"
        }
    }


    override fun setApp(index: Int, info: ActivityInfo?) {

        dataSource[index].activityInfo = info

        dataSource.invalidateAt(index)

    }


    override fun clear() {

        dataSource[0].activityInfo = null
        dataSource[1].activityInfo = null

        dataSource.invalidateAll()

    }


}