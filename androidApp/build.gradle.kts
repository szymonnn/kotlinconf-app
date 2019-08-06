val kotlin_version: String by project
val anko_version: String by project
val coroutines_version: String by project
val ktor_version: String by project
val sticky_headers: String by project
val glide_version: String by project

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlinx-serialization")
    id("kotlin-kapt")
}

android {
    compileSdkVersion(28)
    defaultConfig {
        applicationId = "com.jetbrains.kotlinconf"
        minSdkVersion(16)
        targetSdkVersion(28)
        multiDexEnabled = true
        versionCode = 10
        versionName = "1.0.9"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions("server")

    productFlavors {
        val local by creating {
            buildConfigField("String", "API_URL", "\"https://api.kotlinconf.com\"")
            setDimension("server")
        }
        val production by creating {
            buildConfigField("String", "API_URL", "\"https://api.kotlinconf.com\"")
            setDimension("server")
        }
    }

    packagingOptions {
        exclude("META-INF/*.kotlin_module")
    }

}

dependencies {
    implementation(project(":common"))
    implementation("com.android.support:multidex:1.0.3")
    implementation("com.android.support:appcompat-v7:28.0.0")
    implementation("com.android.support:support-v4:28.0.0")
    implementation("com.android.support:recyclerview-v7:28.0.0")
    implementation("com.android.support:design:28.0.0")
    implementation("com.android.support.constraint:constraint-layout:1.1.3")
    kapt("android.arch.lifecycle:compiler:2.0.0")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version")
    implementation("org.jetbrains.anko:anko-appcompat-v7-commons:$anko_version")
    implementation("org.jetbrains.anko:anko-appcompat-v7:$anko_version")
    implementation("org.jetbrains.anko:anko-sdk25:$anko_version")
    implementation("org.jetbrains.anko:anko-recyclerview-v7:$anko_version")
    implementation("org.jetbrains.anko:anko-commons:$anko_version")
    implementation("org.jetbrains.anko:anko-design:$anko_version")
    implementation("org.jetbrains.anko:anko-coroutines:$anko_version")
    implementation("net.opacapp:multiline-collapsingtoolbar:27.1.1")
    implementation("com.github.bumptech.glide:glide:$glide_version")
    implementation("com.brandongogetap:stickyheaders:$sticky_headers")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version")
    implementation("io.ktor:ktor-client-android:$ktor_version")
    implementation("io.ktor:ktor-client-serialization-jvm:$ktor_version")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlin_version")
    testImplementation("junit:junit:4.12")
}