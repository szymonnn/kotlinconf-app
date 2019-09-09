package org.jetbrains.kotlinconf

import android.app.Application

class KotlinConfApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        ConferenceService.errors.watch {
            println("Error: $it")
        }
    }
}