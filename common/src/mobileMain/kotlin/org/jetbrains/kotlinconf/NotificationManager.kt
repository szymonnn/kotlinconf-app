package org.jetbrains.kotlinconf

import org.jetbrains.kotlinconf.storage.*

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect class NotificationManager(context: ApplicationContext) {
    suspend fun isEnabled(): Boolean

    suspend fun requestPermission(): Boolean

    suspend fun schedule(sessionData: SessionData): String?

    fun cancel(sessionData: SessionData)
}


expect class LocationManager(context: ApplicationContext) {
    suspend fun requestPermission(): Boolean
}