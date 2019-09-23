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

import android.content.pm.ActivityInfo
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.fb.splitscreenlauncher.ui.settings.model.AppInfo
import com.fb.splitscreenlauncher.ui.shortcut.ShortcutDialogRepoImpl
import com.fb.splitscreenlauncher.ui.shortcut.ShortcutDialogViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class ShortcutDialogTest {


    private lateinit var shortcutDialogVM: ShortcutDialogViewModel

    private val app = mockk<App>(relaxed = true)

    private val infoOne = mockk<ActivityInfo> {
        packageName = "pkg1"
        every { loadLabel(mockk()) } returns "name1"
    }
    private val infoTwo = mockk<ActivityInfo> {
        packageName = "pkg2"
        every { loadLabel(mockk()) } returns "name2"
    }
    private val repo = spyk(ShortcutDialogRepoImpl(app)) {
        every { getIcon() } answers { mockk() }
        every { getLabel() } answers { "" }
    }



    @Rule
    @JvmField
    var liveDataRule = InstantTaskExecutorRule()


    @Before fun before() {

        shortcutDialogVM = ShortcutDialogViewModel(app, repo)

    }


    @After fun after() { unmockkAll() }


    @Test fun `Validate at least two apps are selected`() {

        repo.dataSource.set(listOf(AppInfo(), AppInfo(infoTwo)))

        assertEquals(false, shortcutDialogVM.save())

    }


    @Test fun `Validate apps switch position`() {

        repo.dataSource.set(listOf(AppInfo(infoOne), AppInfo(infoTwo)))


        with(shortcutDialogVM) {

            swapItems()

        }

        assertEquals(infoTwo.packageName, repo.dataSource[0].activityInfo?.packageName)
        assertEquals(infoOne.packageName, repo.dataSource[1].activityInfo?.packageName)

    }


    @Test fun `Validate selected apps are different`() {

        repo.dataSource.set(listOf(AppInfo(), AppInfo()))


        val resultFirst: Boolean
        val resultSecond: Boolean

        with (shortcutDialogVM) {

            resultFirst = setApp(0, infoOne)

            resultSecond = setApp(1, infoOne)

        }


        assertEquals(true, resultFirst)
        assertEquals(false, resultSecond)

    }


}