import com.android.build.api.dsl.ApplicationDefaultConfig
import com.android.build.api.dsl.LibraryDefaultConfig
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

fun LibraryDefaultConfig.populateBuildConfigFields(){
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
    buildConfigField("String", "SPEECHIFY_VOICE_ID", "\"lisa\"")
    buildConfigField("String", "LLM_MODEL", "\"google/gemini-2.0-flash-001\"")
}


plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "tekin.luetfi.heart.of.jessamine.common"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 26

        populateBuildConfigFields()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    }
    hilt {
        enableAggregatingTask = false
    }
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
        freeCompilerArgs.add("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


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

    //Data Persistency
    implementation(libs.datastore.preferences)

    //Media
    api(libs.androidx.media3.exoplayer)
    api(libs.androidx.media3.session)


    //Dependency Injection - Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    ksp(libs.hilt.ext.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    api(project(":app:map"))
}