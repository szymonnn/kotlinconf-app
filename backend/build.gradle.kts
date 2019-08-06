import com.github.jengelman.gradle.plugins.shadow.tasks.*
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.tasks.*

val kotlin_version: String by project
val ktor_version: String by project
val squash_version: String by project

plugins {
    kotlin("jvm")
    id("kotlinx-serialization")

    id("application")
    id("com.github.johnrengelman.shadow")
}

application {
    mainClassName = "org.jetbrains.kotlinconf.backend.ServerKt"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(project(":common"))

    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-auth:$ktor_version")
    implementation("io.ktor:ktor-websockets:$ktor_version")
    implementation("io.ktor:ktor-client-apache:$ktor_version")
    implementation("io.ktor:ktor-client-serialization-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")

    implementation("org.jetbrains.squash:squash:$squash_version")
    implementation("org.jetbrains.squash:squash-h2:$squash_version")
    implementation("com.github.salomonbrys.kotson:kotson:2.5.0")
    implementation(group = "com.zaxxer", name = "HikariCP", version = "2.7.2")

    implementation("ch.qos.logback:logback-classic:1.2.3")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlin_version")
    testImplementation(group = "junit", name = "junit", version = "4.12")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

sourceSets {
    val main by getting {
        java.srcDir("src")
        withConvention(KotlinSourceSet::class) {
            kotlin.srcDir("src")
        }
        resources.srcDir("resources")
    }

    val test by getting {
        java.srcDirs("test")
        withConvention(KotlinSourceSet::class) {
            kotlin.srcDir("test")
        }
    }
}

tasks.withType<ShadowJar> {
    archiveBaseName.value("backend")
    archiveClassifier.value(null)
    archiveVersion.value(null)
}
