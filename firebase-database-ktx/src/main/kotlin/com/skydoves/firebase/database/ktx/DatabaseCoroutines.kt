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
@file:Suppress("UNCHECKED_CAST")

package com.skydoves.firebase.database.ktx

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import org.json.JSONObject

/**
 * Returns a flow that emits a target object [T] from the specified [path] within the [DatabaseReference].
 *
 * The flow emits a [Result], encapsulating both the snapshot value and any potential errors.
 */
fun <T : Any> DatabaseReference.flow(
  path: (DataSnapshot) -> DataSnapshot,
  decodeProvider: (String) -> T,
) = callbackFlow {
  val listener = object : ValueEventListener {
    override fun onDataChange(snapshot: DataSnapshot) {
      val data = path.invoke(snapshot).serializedValue(decodeProvider)
      trySend(Result.success(data))
    }

    override fun onCancelled(error: DatabaseError) {
      trySend(Result.failure(error.toException()))
    }
  }
  addValueEventListener(listener)

  awaitClose { removeEventListener(listener) }
}

/**
 * Returns a flow that emits only once a target object [T] from the specified [path] within the [DatabaseReference].
 *
 * The flow emits a [Result], encapsulating both the snapshot value and any potential errors.
 */
fun <T : Any> DatabaseReference.flowSingle(
  path: (DataSnapshot) -> DataSnapshot,
  decodeProvider: (String) -> T,
) = callbackFlow {
  val listener = object : ValueEventListener {
    override fun onDataChange(snapshot: DataSnapshot) {
      val data = path.invoke(snapshot).serializedValue(decodeProvider)
      trySend(Result.success(data))
    }

    override fun onCancelled(error: DatabaseError) {
      trySend(Result.failure(error.toException()))
    }
  }
  addListenerForSingleValueEvent(listener)

  awaitClose { removeEventListener(listener) }
}

/**
 * Returns a flow that emits a target object [T] based on changes to the child nodes
 * at the specified [path] within the [DatabaseReference].
 *
 * The flow emits [ChildState], which encapsulates all state changes, including the snapshot value and any errors.
 */
fun <T : Any> DatabaseReference.flowChild(
  path: (DataSnapshot) -> DataSnapshot,
  decodeProvider: (String) -> T,
) = callbackFlow {
  val listener = object : ChildEventListener {
    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
      val data = path.invoke(snapshot).serializedValue(decodeProvider)
      trySend(
        ChildState.ChildAdded(value = data, previousChildName = previousChildName),
      )
    }

    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
      val data = path.invoke(snapshot).serializedValue(decodeProvider)
      trySend(
        ChildState.ChildChanged(value = data, previousChildName = previousChildName),
      )
    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
      val data = path.invoke(snapshot).serializedValue(decodeProvider)
      trySend(
        ChildState.ChildRemoved(value = data),
      )
    }

    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
      val data = path.invoke(snapshot).serializedValue(decodeProvider)
      trySend(
        ChildState.ChildMoved(value = data, previousChildName = previousChildName),
      )
    }

    override fun onCancelled(error: DatabaseError) {
      trySend(
        ChildState.ChildCanceled(error = error),
      )
    }
  }

  addChildEventListener(listener)

  awaitClose { removeEventListener(listener) }
}

private fun <T> DataSnapshot.serializedValue(
  decodeProvider: (String) -> T,
): T? {
  val map: Map<String, Any> = value as? Map<String, Any> ?: return null
  val jsonString = JSONObject(map).toString().replace("\\/", "/")
  val data = decodeProvider.invoke(jsonString)
  return data
}
