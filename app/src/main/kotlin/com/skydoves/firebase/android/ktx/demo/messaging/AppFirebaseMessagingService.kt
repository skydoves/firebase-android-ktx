package com.skydoves.firebase.android.ktx.demo.messaging

import android.util.Log
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.RemoteMessage
import com.skydoves.firebase.android.ktx.demo.App.Companion.APP_LOG_TAG
import com.skydoves.firebase.messaging.lifecycle.ktx.LifecycleAwareFirebaseMessagingService
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class AppFirebaseMessagingService : LifecycleAwareFirebaseMessagingService() {
  private var job: Job? by Delegates.observable(null) { _, oldValue, newValue ->
    Log.d(APP_LOG_TAG, "FCMService#job oldValue: $oldValue, newValue: $newValue")
  }

  init {
    Log.d(APP_LOG_TAG, "AppFCMService#init init lifecycle: $lifecycle")
    lifecycle.addObserver(LifecycleEventObserver { _, event ->
      Log.d(APP_LOG_TAG, "FCMService#lifecycleevent: $event")
    })
  }

  override fun onNewToken(token: String) {
    super.onNewToken(token)
    job = lifecycleScope.launch {
      // send the token to the server
      Log.d(APP_LOG_TAG, "FCMService#onNewToken onNewToken: $token")
    }
  }

  override fun onMessageReceived(message: RemoteMessage) {
    super.onMessageReceived(message)
    Log.d(APP_LOG_TAG, "FCMService#onMessageRec onMessageReceived: ${message.data}")
  }

  override fun onDestroy() {
    super.onDestroy()
    Log.d(APP_LOG_TAG, "FCMService#onDestroy onDestroy")
  }
}