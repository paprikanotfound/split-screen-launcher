package com.fb.splitscreenlauncher.util.theme

import androidx.fragment.app.FragmentActivity



data class ThemeMode(val id: Int, val name: Int)


interface ThemeHelper {


    fun setup(activity: FragmentActivity)


    fun setMode(mode: Int)


    fun curMode(): Int


    fun getModes(): MutableList<ThemeMode>

}