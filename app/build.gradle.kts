import com.android.build.api.dsl.ApplicationDefaultConfig
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import java.util.Properties
import kotlin.apply

fun Project.readSecret(
    key: String,
    default: String = "YOUR_OPEN_AI_KEY"
): String {
    // 1) local.properties at project root
    val localFile = File(rootDir, "local.properties")
    if (localFile.exists()) {
        Properties().apply {
            localFile.inputStream().use { load(it) }
            getProperty(key)?.let { return it }
        }
    }
    // 2) Environment variable
    System.getenv(key)?.let { return it }
    // 3) gradle.properties or -Pkey=...
    (findProperty(key) as String?)?.let { return it }
    // 4) Fallback
    return default
}

val openRouterAiKey = project.readSecret("OPENROUTERAI_API_KEY", "YOUR_OPENROUTERAI_API_KEY")
val speechifyKey = project.readSecret("SPEECHIFY_API_KEY", "YOUR_SPEECHIFY_API_KEY")

fun ApplicationDefaultConfig.populateBuildConfigFields(){
    /**
     * Endpoints
     */
    buildConfigField("String", "OPENROUTERAI_API", "\"https://openrouter.ai/api/v1/\"")
    buildConfigField("String", "SPEECHIFY_API", "\"https://api.sws.speechify.com/\"")
    buildConfigField("String", "WIKIPEDIA_API", "\"https://en.wikipedia.org/\"")

    /**
     * API Keys
     */
    buildConfigField("String", "OPENROUTERAI_API_KEY", "\"$openRouterAiKey\"")
    buildConfigField("String", "SPEECHIFY_API_KEY", "\"$speechifyKey\"")

    /**
     * Other
     */
    buildConfigField("String", "OPEN_ROUTER_APP_NAME", "\"Jessamine\"")
    buildConfigField("String", "APP_USER_AGENT", "\"Jessamine/1.0 (https://github.com/LutfiTekin/Jessamine) OkHttp/4.x\"")
}


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "tekin.luetfi.heart.of.jessamine"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "tekin.luetfi.heart.of.jessamine"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        populateBuildConfigFields()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true
    }
    hilt {
        enableAggregatingTask = false
    }
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
    }
}

dependencies {
    // AndroidX Core & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Jetpack Compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui.foundation)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material3.adaptive)
    implementation(libs.androidx.material3.adaptive.navigation.suite)
    implementation(libs.androidx.material3.icons.extended)
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.compose.ui.text.google.fonts)


    //Networking
    implementation(libs.retrofit)
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.moshi.adapter)
    implementation(libs.material)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.logging.interceptor)

    //Local Data
    implementation(libs.androidx.room)
    ksp(libs.androidx.room.compiler)

    // Dependency Injection - Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    ksp(libs.hilt.ext.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    //Media
    implementation(libs.androidx.media3.exoplayer)


    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Compose testing BOM
    androidTestImplementation(libs.androidx.ui.test.junit4)       // Compose UI tests

    // Debugging - Compose UI Tooling
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(project(":app:map"))
}