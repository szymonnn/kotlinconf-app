package org.jetbrains.kotlinconf

import org.jetbrains.kotlinconf.storage.*
import platform.CoreLocation.*

actual class LocationManager actual constructor(context: ApplicationContext) {
    actual fun requestPermission(): Unit = Unit

    actual fun isEnabled(): Boolean {
        val status: CLAuthorizationStatus = CLLocationManager.authorizationStatus()
        return status in setOf(
            kCLAuthorizationStatusAuthorized,
            kCLAuthorizationStatusAuthorizedAlways,
            kCLAuthorizationStatusAuthorizedWhenInUse
        )
    }
}