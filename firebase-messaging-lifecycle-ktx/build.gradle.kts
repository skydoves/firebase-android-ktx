import com.skydoves.firebase.android.ktx.Configuration

plugins {
  id(libs.plugins.android.library.get().pluginId)
  id(libs.plugins.kotlin.android.get().pluginId)
  id(libs.plugins.nexus.plugin.get().pluginId)
}

apply(from = "${rootDir}/scripts/publish-module.gradle.kts")

mavenPublishing {
  val artifactId = "firebase-messaging-lifecycle-ktx"
  coordinates(
    Configuration.artifactGroup,
    artifactId,
    rootProject.extra.get("libVersion").toString()
  )

  pom {
    name.set(artifactId)
    description.set("Firebase extensions Kotlin & Compose firendly that helps you to focus on your business logic.")
  }
}

android {
  compileSdk = Configuration.compileSdk
  namespace = "com.skydoves.firebase.messaging.lifecycle.ktx"
  defaultConfig {
    minSdk = Configuration.minSdk
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  kotlinOptions {
    jvmTarget = "1.8"
  }
}

kotlin {
  compilerOptions {
    freeCompilerArgs.addAll(
      "-Xexplicit-api=strict",
    )
  }
}

dependencies {
  implementation(platform(libs.firebase.bom))
  api(libs.firebase.messaging)

  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.androidx.lifecycle.runtime)
}
