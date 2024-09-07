package com.skydoves.firebase.android.ktx.demo

import android.app.Application
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class App : Application() {
  private val lifecycleOwner: LifecycleOwner
    get() = ProcessLifecycleOwner.get()

  override fun onCreate() {
    super.onCreate()
    lifecycleOwner.lifecycleScope.launch {
      lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
        try {
          val token = FirebaseMessaging.getInstance().token.await()
          Log.d(APP_LOG_TAG, "APP#token FirebaseMessaging token: $token")
        } catch (e: CancellationException) {
          throw e
        } catch (e: Exception) {
          Log.e(APP_LOG_TAG, "APP#exception Error: ${e.message}")
        }
      }
    }
  }

  companion object {
    const val APP_LOG_TAG = "DemoApp"
  }
}