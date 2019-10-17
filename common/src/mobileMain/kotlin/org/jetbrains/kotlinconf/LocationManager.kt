package org.jetbrains.kotlinconf

import org.jetbrains.kotlinconf.storage.*

expect class LocationManager(context: ApplicationContext) {
    fun requestPermission(): Unit

    fun isEnabled(): Boolean
}