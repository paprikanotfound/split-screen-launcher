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

package com.fb.splitscreenlauncher.util.misc

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.provider.Settings
import android.util.TypedValue
import android.webkit.URLUtil
import android.widget.Toast
import androidx.annotation.AttrRes


/** Show toast with specified Context */
fun Context.toast(text: String? = null, res: Int = 0, length: Int = Toast.LENGTH_SHORT) {

    Toast.makeText(this, text ?: getString(res), length).show()

}


/**
 * Launch WebPage with specified URL
 *
 * @param url URL to open
 *
 * @return false if device has no apps that can receive the intent
 */
fun Context.launchUrl(url: String? = null, res: Int = 0): Boolean {

    return Intent(Intent.ACTION_VIEW, Uri.parse(URLUtil.guessUrl(url ?: getString(res)))).let {

        (it.resolveActivity(packageManager) != null).apply {

            if (this) startActivity(it)

        }
    }
}


/** Return version name for specified Context */
val Context.versionName: String
    get() = appPackageInfo?.versionName ?: ""


val Context.appPackageInfo: PackageInfo?
    get() = packageManager.getPackageInfo(packageName, 0) ?: null


/**
 *
 * Check if specified service is enabled in Android's system settings
 * Does NOT ensure that the service is actually running
 *
 * @param serviceClass Class of the Accessibility service to verify
 *
 * @return true if system settings contain the specified service name
 * in the enabled services table value
 *
 */
fun Context.isAccessibilityEnabled(serviceClass: Class<*>): Boolean {
    return try {

        Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES).let {

            it.contains("$packageName/${serviceClass.name}") || it.contains("$packageName/${serviceClass.simpleName}")

        }

    } catch (ignored: Exception) {

        false

    }
}


/**
 *
 * Get color value for specified theme attribute
 *
 * @param attr resource id of Attribute
 *
 * @return Int value of the color
 */
fun Context.getThemeColor(@AttrRes attr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.data
}

