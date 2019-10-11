package org.jetbrains.kotlinconf

import com.mapbox.android.core.permissions.*
import org.jetbrains.kotlinconf.storage.*

actual class LocationManager actual constructor(private val context: ApplicationContext) {
    actual suspend fun requestPermission(): Boolean {
        if (PermissionsManager.areLocationPermissionsGranted(context.activity)) {
            return true
        }

        val handler = object : PermissionsListener {
            override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {}
            override fun onPermissionResult(granted: Boolean) {}
        }
        PermissionsManager(handler).requestLocationPermissions(context.activity)
        return true
    }
}