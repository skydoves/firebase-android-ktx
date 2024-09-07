package com.skydoves.firebase.messaging.lifecycle.ktx

import android.annotation.SuppressLint
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.messaging.FirebaseMessagingService

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public open class LifecycleAwareFirebaseMessagingService : FirebaseMessagingService(),
  LifecycleOwner {
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
