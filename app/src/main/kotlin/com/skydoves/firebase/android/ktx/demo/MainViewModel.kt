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
@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package com.skydoves.firebase.android.ktx.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.skydoves.firebase.android.ktx.demo.model.TimelineUi
import com.skydoves.firebase.database.ktx.flow
import com.skydoves.firebase.database.ktx.flowChild
import com.skydoves.firebase.database.ktx.flowSingle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.json.Json

class MainViewModel : ViewModel() {

  private val database = Firebase.database(BuildConfig.REALTIME_DATABASE_URL).reference

  private val json = Json {
    isLenient = true
    ignoreUnknownKeys = true
  }

  val timelineUi = database.flow<TimelineUi>(
    path = { dataSnapshot ->
      dataSnapshot.child("timeline")
    },
    decodeProvider = { jsonString ->
      json.decodeFromString(jsonString)
    },
  ).flatMapLatest { result ->
    if (result.isSuccess) {
      flowOf(result.getOrNull())
    } else {
      throw RuntimeException("parsing error!")
    }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = null,
  )

  val timelineUiOneShot = database.flowSingle<TimelineUi>(
    path = { dataSnapshot ->
      dataSnapshot.child("timeline")
    },
    decodeProvider = { jsonString ->
      json.decodeFromString(jsonString)
    },
  ).flatMapLatest { result ->
    if (result.isSuccess) {
      flowOf(result.getOrNull())
    } else {
      throw RuntimeException("parsing error!")
    }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = null,
  )

  val childState = database.flowChild<TimelineUi>(
    path = { dataSnapshot ->
      dataSnapshot
    },
    decodeProvider = { jsonString ->
      json.decodeFromString(jsonString)
    },
  ).stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = null,
  )
}
