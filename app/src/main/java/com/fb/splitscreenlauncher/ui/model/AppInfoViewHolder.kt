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

package com.fb.splitscreenlauncher.ui.model

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.afollestad.recyclical.ViewHolder
import com.fb.splitscreenlauncher.R


class AppInfoViewHolder (itemView: View): ViewHolder(itemView) {
    val name: AppCompatTextView = itemView.findViewById(R.id.text_name)
    val icon: AppCompatImageView = itemView.findViewById(R.id.img_icon)
}