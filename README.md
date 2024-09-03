<h1 align="center">Firebase Android KTX</h1></br>

<p align="center">
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=21"><img alt="API" src="https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat"/></a>
  <a href="https://github.com/skydoves/firebase-android-ktx/actions/workflows/android.yml"><img alt="Build Status" 
  src="https://github.com/skydoves/firebase-android-ktx/actions/workflows/android.yml/badge.svg"/></a>
  <a href="https://github.com/skydoves"><img alt="Profile" src="https://skydoves.github.io/badges/skydoves.svg"/></a>
</p><br>

<p align="center">
 <img src="https://github.com/user-attachments/assets/87ed8c86-31e2-429e-a09e-34a559416880"/>
</p>

<p align="center">🔥 Kotlin & Compose-friendly Firebase extensions designed to help you focus on your business logic. </p>

## Download
[![Maven Central](https://img.shields.io/maven-central/v/com.github.skydoves/firebase-android-ktx.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.skydoves%22%20AND%20a:%22firebase-android-ktx%22)

### Version Catalog

If you're using Version Catalog, you can configure the dependency by adding it to your `libs.versions.toml` file as follows:

```toml
[versions]
#...
firebaseKtx = "0.1.0"

[libraries]
#...
firebase-database-ktx = { module = "com.github.skydoves:firebase-database-ktx", version.ref = "firebaseKtx" }
```

### Gradle
Add the dependency below to your **module**'s `build.gradle.kts` file:

```gradle
dependencies {
    implementation("com.github.skydoves:firebase-database-ktx:0.1.0")
    
    // if you're using Version Catalog
    implementation(libs.firebase.database.ktx)
}
```

## Firebase Realtime Database KTX

The [Firebase Realtime Database](https://firebase.google.com/docs/database) is primarily based on Java and callback listeners, making it less compatible with Coroutines and Jetpack Compose. Furthermore, since it returns snapshot values in non-JSON formats, handling objects and implementing custom serialization solutions can be challenging, as shown in the example below:

```kotlin
val listener = object : ValueEventListener {
  override fun onDataChange(snapshot: DataSnapshot) {
    val value = snapshot.child("timeline")
    // ..
  }

  override fun onCancelled(error: DatabaseError) {
    // ..
  }
}
database.addValueEventListener(listener)
```

The result (`value`) isn't in JSON format, so we can't directly serialize it into a target object like this:

```
{top={banner={size={width=0, height=300}, scaleType=crop, url=https://blog.icons8.com/wp-content/uploads/2020/02/city-illustration-graphic-art.jpg}, order=0}, bottom={list={layout=grid, itemSize={width=135, height=210}, items=[{scaleType=crop, title=Frozen ...
```

They provide their own serialization algorithm internally, but it's not customizable. Additionally, you need to attach specific annotations and initialize fields with default values, as shown in the example below:

```kotlin
@IgnoreExtraProperties
data class Post(
    var uid: String? = "",
    var author: String? = "",
    var title: String? = "",
) {
      @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "author" to author,
            "title" to title,
    }
}
```

### DatabaseReference.flow()

You can easily serialize snapshot values from the Realtime Database and observe them as a Flow by using the `DatabaseReference.flow()` extension, as shown in the example below:

```kotlin
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
}
```

Now, you can safely observe it in Jetpack Compose using `collectAsStateWithLifecycle`, as demonstrated in the code below:

```kotlin
val timelineUI by viewModel.timelineUi.collectAsStateWithLifecycle()
```

### DatabaseReference.flowSingle()

This functions similarly to `DatabaseReference.flow()`, but it only emits the value once, even if the value changes dynamically. It uses `addListenerForSingleValueEvent` instead of `addValueEventListener`. So, it's suitable for the one-shot flow.

### DatabaseReference.flowChild()

This observes all changes in the child nodes of the Realtime Database, including additions, modifications, deletions, movements, and cancellations. The flow emits `ChildState`, which encapsulates the state changes along with the snapshot value and any errors.

```kotlin
class MainViewModel : ViewModel() {
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
```

Now, you can safely observe it in Jetpack Compose using `collectAsStateWithLifecycle`, as demonstrated in the code below:

```kotlin
val childState by viewModel.childState.collectAsStateWithLifecycle()

when (childState) {
  is ChildState.ChildAdded -> ..
  is ChildState.ChildChanged -> ..
  is ChildState.ChildMoved -> ..
  is ChildState.ChildRemoved -> ..
  is ChildState.ChildCanceled -> ..
  else -> ..
}
```

## Find this repository useful? :heart:
Support it by joining __[stargazers](https://github.com/skydoves/firebase-android-ktx/stargazers)__ for this repository. :star: <br>
Also, __[follow me](https://github.com/skydoves)__ on GitHub for my next creations! 🤩

# License
```xml
Designed and developed by 2024 skydoves (Jaewoong Eum)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
