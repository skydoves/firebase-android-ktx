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
import com.skydoves.firebase.android.ktx.Configuration

plugins {
  id(libs.plugins.android.application.get().pluginId)
  id(libs.plugins.kotlin.android.get().pluginId)
  id(libs.plugins.compose.compiler.get().pluginId)
  id(libs.plugins.google.secrets.get().pluginId)
  id(libs.plugins.kotlin.serialization.get().pluginId)
}

android {
  compileSdk = Configuration.compileSdk
  namespace = "com.skydoves.firebase.android.ktx.demo"
  defaultConfig {
    applicationId = "com.skydoves.firebase.android.ktx.demo"
    minSdk = Configuration.minSdk
    targetSdk = Configuration.targetSdk
    versionCode = Configuration.versionCode
    versionName = Configuration.versionName
  }

  buildFeatures {
    compose = true
    buildConfig = true
  }

  lint {
    abortOnError = false
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  kotlinOptions {
    jvmTarget = "17"
  }
}

secrets {
  propertiesFileName = "secrets.properties"
  defaultPropertiesFileName = "secrets.defaults.properties"
}

dependencies {
  // modules
  implementation(project(":firebase-database-ktx"))

  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.foundation)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.lifecycle.runtimeCompose)
  implementation(libs.androidx.lifecycle.viewModelCompose)
  debugImplementation(libs.androidx.compose.ui.tooling)

  implementation(libs.kotlinx.serialization.json)
}

if (file("google-services.json").exists()) {
  apply(plugin = libs.plugins.gms.googleServices.get().pluginId)
}
