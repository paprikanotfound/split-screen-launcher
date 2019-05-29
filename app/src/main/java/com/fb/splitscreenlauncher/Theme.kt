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

import android.graphics.Color
import com.afollestad.aesthetic.Aesthetic
import com.afollestad.aesthetic.AutoSwitchMode
import com.afollestad.aesthetic.BottomNavBgMode
import com.afollestad.aesthetic.BottomNavIconTextMode


class Theme {

    companion object {

        const val THEME_LIGHT = 0
        const val THEME_DARK = 1
        const val THEME_BLACK = 2

        fun set(themeType: Int) {
            Aesthetic.config {
                when (themeType) {
                    THEME_BLACK -> {

                        activityTheme(R.style.AppTheme_Black)

                        isDark(true)

                        lightStatusBarMode(AutoSwitchMode.AUTO)
                        lightNavigationBarMode(AutoSwitchMode.AUTO)

                        textColorPrimary(res = R.color.textNightColorPrimary)
                        textColorSecondary(res = R.color.textNightColorSecondary)

                        colorWindowBackground(Color.BLACK)
                        colorPrimary(Color.BLACK)
                        colorPrimaryDark(Color.BLACK)
                        colorAccent(res = R.color.colorNightAccent)

                        colorStatusBar(Color.BLACK)
                        colorNavigationBar(Color.BLACK)

                        bottomNavigationBackgroundMode(BottomNavBgMode.ACCENT)
                        bottomNavigationIconTextMode(BottomNavIconTextMode.NONE)

                        snackbarBackgroundColorDefault()
                        snackbarTextColorDefault()


                    }
                    THEME_DARK -> {

                        activityTheme(R.style.AppTheme_Dark)

                        isDark(true)

                        lightStatusBarMode(AutoSwitchMode.AUTO)
                        lightNavigationBarMode(AutoSwitchMode.AUTO)

                        textColorPrimary(res = R.color.textNightColorPrimary)
                        textColorSecondary(res = R.color.textNightColorSecondary)

                        colorWindowBackground(res = R.color.colorNightBackground)
                        colorPrimary(res = R.color.colorNightPrimary)
                        colorPrimaryDark(R.color.colorNightPrimaryDark)
                        colorAccent(res = R.color.colorNightAccent)

                        colorStatusBar(res = R.color.colorNightBackground)
                        colorNavigationBar(res = R.color.colorNightBackgroundDark)

                        bottomNavigationBackgroundMode(BottomNavBgMode.ACCENT)
                        bottomNavigationIconTextMode(BottomNavIconTextMode.NONE)

                        snackbarBackgroundColorDefault()
                        snackbarTextColorDefault()

                    }
                    THEME_LIGHT -> {

                        activityTheme(R.style.AppTheme)

                        isDark(false)

                        lightStatusBarMode(AutoSwitchMode.AUTO)
                        lightNavigationBarMode(AutoSwitchMode.AUTO)

                        textColorPrimary(res = R.color.textColorPrimary)
                        textColorSecondary(res = R.color.textColorSecondary)

                        colorWindowBackground(res = R.color.colorBackground)
                        colorPrimary(res = R.color.colorPrimary)
                        colorPrimaryDark(R.color.colorPrimaryDark)
                        colorAccent(res = R.color.colorAccent)

                        colorStatusBar(res = R.color.colorBackground)
                        colorNavigationBar(res = R.color.colorBackgroundDark)

                        bottomNavigationBackgroundMode(BottomNavBgMode.ACCENT)
                        bottomNavigationIconTextMode(BottomNavIconTextMode.NONE)

                        snackbarBackgroundColorDefault()
                        snackbarTextColorDefault()

                    }
                }
            }
        }

    }

}