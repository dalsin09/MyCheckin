import java.util.Properties
// Carregar o secrets.properties
val secretProperties = Properties()
file("../secrets.properties").inputStream().use { secretProperties.load(it) }
plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.mateus.oliveira.mycheckin"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mateus.oliveira.mycheckin"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["MAPS_API_KEYS"] = secretProperties["MAPS_API_KEYS"]?:"";
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}