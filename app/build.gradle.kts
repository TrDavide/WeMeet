import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.temptationjavaisland.wemeet"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.temptationjavaisland.wemeet"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resValue("string", "ticketmaster_key", gradleLocalProperties(rootDir, providers).getProperty("ticketmaster_key"));
        resValue("bool", "debug_mode", gradleLocalProperties(rootDir, providers).getProperty("debug_mode"));
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation (libs.material.v1110)
    implementation (libs.commons.validator)
    implementation (libs.cardview)
    implementation(libs.room.common.jvm)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.material.v1120)
    implementation (libs.gson)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    implementation(libs.retrofit)
    implementation (libs.converter.gson.v300)
    implementation (libs.play.services.maps)
    implementation (libs.play.services.location)
    implementation (libs.play.services.location)
    implementation (libs.retrofit.v290)
    implementation (libs.converter.gson.v290)
    implementation (libs.glide)
    implementation (libs.glide)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.credentials.v130)
    implementation(libs.credentials.play.services.auth.v130)
    implementation(libs.googleid)
    implementation(libs.firebase.database)
    implementation (libs.play.services.auth)
    implementation (libs.google.services)


}