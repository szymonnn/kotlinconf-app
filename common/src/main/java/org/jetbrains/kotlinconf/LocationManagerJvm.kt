package org.jetbrains.kotlinconf

import com.mapbox.android.core.permissions.*
import org.jetbrains.kotlinconf.storage.*

actual class LocationManager actual constructor(private val context: ApplicationContext) {
    actual fun requestPermission() {
        if (PermissionsManager.areLocationPermissionsGranted(context.activity)) {
            return
        }

        val handler = object : PermissionsListener {
            override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {}
            override fun onPermissionResult(granted: Boolean) {}
        }
        PermissionsManager(handler).requestLocationPermissions(context.activity)
    }

    actual fun isEnabled(): Boolean = PermissionsManager.areLocationPermissionsGranted(context.activity)
}