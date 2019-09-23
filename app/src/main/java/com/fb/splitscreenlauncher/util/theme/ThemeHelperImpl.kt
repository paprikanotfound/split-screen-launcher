package com.fb.splitscreenlauncher.util.theme

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentActivity
import com.afollestad.rxkprefs.Pref
import com.fb.splitscreenlauncher.R
import com.fb.splitscreenlauncher.util.BuildUtils
import com.fb.splitscreenlauncher.util.misc.ld
import com.fb.splitscreenlauncher.util.prefs.PrefNames.PREF_THEME
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named


class ThemeHelperImpl(val app: Application): ThemeHelper {


    private val appTheme by app.inject<Pref<Int>>(named(PREF_THEME))


    init {

        AppCompatDelegate.setDefaultNightMode(curMode())

    }


    override fun setup(activity: FragmentActivity) {
        with (activity) {

            setTheme(R.style.AppTheme)

        }
    }


    override fun setMode(mode: Int) {

        appTheme.set(mode)

        AppCompatDelegate.setDefaultNightMode(mode)

    }


    override fun curMode(): Int = appTheme.get()


    override fun getModes(): MutableList<ThemeMode> {
        return when {
            BuildUtils.AT_LEAST_9 -> mutableListOf(
                ThemeMode(AppCompatDelegate.MODE_NIGHT_NO, R.string.theme_light),
                ThemeMode(AppCompatDelegate.MODE_NIGHT_YES, R.string.theme_dark),
                ThemeMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, R.string.theme_system)
            )
            else -> mutableListOf(
                ThemeMode(AppCompatDelegate.MODE_NIGHT_NO, R.string.theme_light),
                ThemeMode(AppCompatDelegate.MODE_NIGHT_YES, R.string.theme_dark)
            )
        }
    }


}