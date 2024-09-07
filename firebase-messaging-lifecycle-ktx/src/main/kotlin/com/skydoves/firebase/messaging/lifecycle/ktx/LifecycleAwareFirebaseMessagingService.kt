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
package com.skydoves.firebase.messaging.lifecycle.ktx

import android.annotation.SuppressLint
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.messaging.FirebaseMessagingService

/**
 * LifecycleAwareFirebaseMessagingService is a lifecycle-aware version of [FirebaseMessagingService],
 * designed to manage tasks in alignment with the service's [lifecycle].
 *
 * For instance, you can send a token to your backend in the [onNewToken] method using the
 * `lifecycleOwner.lifecycleScope.launch` function. This ensures the coroutine scope is automatically canceled
 * when the service lifecycle changes, preventing any unintended background tasks from continuing to run.
 */
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public open class LifecycleAwareFirebaseMessagingService :
  FirebaseMessagingService(), LifecycleOwner {
  private val dispatcher = ServiceLifecycleDispatcher(this@LifecycleAwareFirebaseMessagingService)

  override val lifecycle: Lifecycle
    get() = dispatcher.lifecycle

  override fun onCreate() {
    dispatcher.onServicePreSuperOnCreate()
    super.onCreate()
  }

  @Deprecated("Deprecated in Java")
  @Suppress("DEPRECATION")
  override fun onStart(intent: Intent?, startId: Int) {
    dispatcher.onServicePreSuperOnStart()
    super.onStart(intent, startId)
  }

  override fun onRebind(intent: Intent?) {
    dispatcher.onServicePreSuperOnBind()
    super.onRebind(intent)
  }

  override fun onDestroy() {
    dispatcher.onServicePreSuperOnDestroy()
    super.onDestroy()
  }
}
