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

import android.app.Application
import android.content.pm.ActivityInfo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fb.splitscreenlauncher.ui.shortcut.dialog.ShortcutDialogViewModel
import org.hamcrest.CoreMatchers.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.junit.Assert.assertThat


const val PKG_TOP = "com.pkg1"
const val PKG_BOTTOM = "com.pkg2"

@RunWith(AndroidJUnit4::class)
class ShortcutDialogViewModelTest {


    private lateinit var viewModel: ShortcutDialogViewModel


    @Before fun setup() {

        viewModel = ShortcutDialogViewModel(Mockito.mock(Application::class.java))

    }

    @Test fun saveDiffAppsReturnsTrue() {

        assertThat(viewModel.updateApp(ShortcutDialogViewModel.KEY_TOP,
            ActivityInfo().apply { packageName = PKG_TOP }), `is`(equalTo(-1)))

        assertThat(viewModel.updateApp(ShortcutDialogViewModel.KEY_BOTTOM,
            ActivityInfo().apply { packageName = PKG_BOTTOM }), `is`(equalTo(-1)))

    }

    @Test fun saveSameAppsReturnsFalse() {

        viewModel.updateApp(ShortcutDialogViewModel.KEY_TOP, ActivityInfo().apply { packageName = PKG_TOP })

        val result = viewModel
            .updateApp(ShortcutDialogViewModel.KEY_BOTTOM, ActivityInfo().apply { packageName = PKG_TOP })

        assertThat(result, not(equalTo(-1)))

    }

    @Test fun swapPositions() {

        viewModel.updateApp(ShortcutDialogViewModel.KEY_TOP, ActivityInfo().apply { packageName = PKG_TOP })
        viewModel.updateApp(ShortcutDialogViewModel.KEY_BOTTOM, ActivityInfo().apply { packageName = PKG_BOTTOM })

        viewModel.swapItems()

        assertThat(viewModel.topActivityInfo?.packageName, `is`(equalTo(PKG_BOTTOM)))
        assertThat(viewModel.bottomActivityInfo?.packageName, `is`(equalTo(PKG_TOP)))

    }

    @Test fun saveEmptyReturnsFalse() {

        assertThat(viewModel.save(), not(equalTo<Any>(-1)))

    }



}