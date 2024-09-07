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

import android.os.Handler
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

internal class ServiceLifecycleDispatcher(provider: LifecycleOwner) {
  private val registry: LifecycleRegistry = LifecycleRegistry(provider)
  private val handler: Handler
  private var lastDispatchRunnable: DispatchRunnable? = null

  init {
    @Suppress("DEPRECATION")
    handler = Handler()
  }

  private fun postDispatchRunnable(event: Lifecycle.Event) {
    lastDispatchRunnable?.run()
    lastDispatchRunnable = DispatchRunnable(registry, event)
    handler.postAtFrontOfQueue(lastDispatchRunnable!!)
  }

  /**
   * Must be a first call in [Service.onCreate] method, even before super.onCreate call.
   */
  fun onServicePreSuperOnCreate() {
    postDispatchRunnable(Lifecycle.Event.ON_CREATE)
  }

  /**
   * Must be a first call in [Service.onBind] method, even before super.onBind
   * call.
   */
  fun onServicePreSuperOnBind() {
    postDispatchRunnable(Lifecycle.Event.ON_START)
  }

  /**
   * Must be a first call in [Service.onStart] or
   * [Service.onStartCommand] methods, even before
   * a corresponding super call.
   */
  fun onServicePreSuperOnStart() {
    postDispatchRunnable(Lifecycle.Event.ON_START)
  }

  /**
   * Must be a first call in [Service.onDestroy] method, even before super.OnDestroy
   * call.
   */
  fun onServicePreSuperOnDestroy() {
    postDispatchRunnable(Lifecycle.Event.ON_STOP)
    postDispatchRunnable(Lifecycle.Event.ON_DESTROY)
  }

  /**
   * [Lifecycle] for the given [LifecycleOwner]
   */
  val lifecycle: Lifecycle
    get() = registry

  internal class DispatchRunnable(
    private val registry: LifecycleRegistry,
    val event: Lifecycle.Event,
  ) : Runnable {
    private var wasExecuted = false

    override fun run() {
      if (!wasExecuted) {
        registry.handleLifecycleEvent(event)
        wasExecuted = true
      }
    }
  }
}
