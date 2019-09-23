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

import android.content.Intent
import com.fb.splitscreenlauncher.di.moduleServiceTest
import com.fb.splitscreenlauncher.service.ServiceController
import com.fb.splitscreenlauncher.service.ServiceControllerImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock


class ServiceControllerTest: KoinTest {


    private val serviceController by inject<ServiceControllerImpl>()
    private val app = mockk<App>(relaxed = true)


    private fun genShortcut(): Pair<Intent, Intent> {

        val intent = mockk<Intent> {
            every { getPackage() } answers { "first" }
        }


        val intentSecond = mockk<Intent> {
            every { getPackage() } answers { "second" }
        }

        return intent to intentSecond
    }


    @Before fun before() {
        startKoin {
            androidContext(app)
            modules(moduleServiceTest)
        }
    }


    @After fun after() {

        unmockkAll()

        stopKoin()
    }


    @Test fun `Validate state after launching first app`() {


        val shortcut = genShortcut()


        with (serviceController) {

            launch(shortcut)

        }


        verify(exactly = 1) { app.startActivity(shortcut.first) }

        assertEquals(ServiceController.STATE_TRIGGER_SPLITSCREEN, serviceController.launchState.value)

    }


    @Test fun `Validate state after toggling split-screen mode`() {

        val shortcut = genShortcut()


        with (serviceController) {

            launch(shortcut)

            onEvent(shortcut.first.`package` ?: "", true)

        }


        verify(exactly = 1) { app.startActivity(shortcut.first) }

        assertEquals(ServiceController.STATE_LAUNCH_BOTTOM_APP, serviceController.launchState.value)

    }


    @Test fun `Validate launch is expired after x seconds`() {

        val shortcut = genShortcut()


        with (serviceController) {

            launch(shortcut)

            onEvent("", true)

            runBlocking { job.children.forEach { it.join() } }

        }


        verify(exactly = 1) { app.startActivity(shortcut.first) }

        assertEquals(ServiceController.STATE_PAUSED, serviceController.launchState.value)

    }


    @Test fun `Validate state after launching second app`() {

        val shortcut = genShortcut()


        with (serviceController) {

            launch(shortcut)

            onEvent(shortcut.first.`package` ?: "", true)

            onEvent("", true)

            onEvent(shortcut.second.`package` ?: "", true)

        }


        verify(exactly = 1) { app.startActivity(shortcut.first) }

        verify(exactly = 1) { app.startActivity(shortcut.second) }

        assertEquals(ServiceController.STATE_PAUSED, serviceController.launchState.value)


    }


}