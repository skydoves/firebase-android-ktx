/*
 * Designed and developed by 2024 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.skydoves.firebase.android.ktx.demo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

class MainActivity : ComponentActivity() {

  private val viewModel: MainViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      val timelineUI by viewModel.timelineUi.collectAsStateWithLifecycle()
      val timelineUIOneShot by viewModel.timelineUiOneShot.collectAsStateWithLifecycle()
      val childState by viewModel.childState.collectAsStateWithLifecycle()

      Text(text = timelineUI.toString())

      LaunchedEffect(timelineUI) {
        Log.e("Test", "timelineUI: $timelineUI")
      }

      LaunchedEffect(timelineUIOneShot) {
        Log.e("Test", "timelineUIOneShot: $timelineUIOneShot")
      }

      LaunchedEffect(childState) {
        Log.e("Test", "childState: $childState")
      }
    }
  }
}
