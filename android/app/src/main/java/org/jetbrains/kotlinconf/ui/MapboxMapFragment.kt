package org.jetbrains.kotlinconf.ui

import android.graphics.*
import android.os.*
import android.widget.*
import com.mapbox.android.core.permissions.*
import com.mapbox.mapboxsdk.camera.*
import com.mapbox.mapboxsdk.geometry.*
import com.mapbox.mapboxsdk.location.modes.*
import com.mapbox.mapboxsdk.maps.*
import com.mapbox.mapboxsdk.style.layers.*
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.sources.*
import org.jetbrains.anko.support.v4.*
import org.jetbrains.kotlinconf.*


/**
 * Fragment class that handles the display of a Mapbox map via the Mapbox Maps SDK for Android.
 * This class extends the Maps SDK's SupportMapFragment and implements the Mapbox Core Library's
 * PermissionsListener interface.
 *
 * More info at https://www.mapbox.com/android-docs/maps/overview and https://docs.mapbox.com/android/core/overview/
 */
class MapboxMapFragment : SupportMapFragment(), PermissionsListener {
    private lateinit var permissionsManager: PermissionsManager
    private var map: MapboxMap? = null
    private val location = LatLng(55.6377951, 12.5787219)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragment = activity
                ?.supportFragmentManager
                ?.findFragmentByTag(TAG) as SupportMapFragment

        fragment.getMapAsync { mapboxMap ->
            // Set the map style. If you want to set a custom map style, replace
            // Style.MAPBOX_STREETS with Style.Builder().fromUrl("customStyleUrl")
            mapboxMap.setStyle(Style.MAPBOX_STREETS) { style ->
                // Map is set up and the style has loaded. Now we can add data
                // and/or make other map adjustments.
                map = mapboxMap

                // Move the map camera to the site of the KotlinConf location
                val mapTarget = LatLng(location.latitude, location.longitude)
                mapboxMap.cameraPosition = CameraPosition.Builder()
                        .target(mapTarget)
                        .zoom(16.0)
                        .build()

                addIndoorLayer(style)
                enableLocationComponent(style)
            }

            mapboxMap.addOnMapClickListener { point ->
                val screenPoint = mapboxMap.projection.toScreenLocation(point)
                val features = mapboxMap.queryRenderedFeatures(screenPoint, "indoor-fill")
                if (features.isEmpty()) return@addOnMapClickListener true
                val selectedFeature = features[0]

                val title = selectedFeature.getStringProperty("name")
                Toast.makeText(context, "You selected $title", Toast.LENGTH_SHORT).show()

                true
            }
        }
    }

    private fun addIndoorLayer(style: Style) {
        val indoorId = "indoor-map"

        val indoor = GeoJsonSource(indoorId, loadStringAsset("indoor.geojson"))
        style.addSource(indoor)

        val indoorFill = FillLayer("indoor-fill", indoorId).withProperties(
                fillColor(Color.parseColor("#10eeee"))
        )

        style.addLayer(indoorFill)

        val indoorLines = LineLayer("indoor-line", indoorId).withProperties(
                lineColor("{stroke}"),
                lineWidth(0.5f)
        )

        style.addLayer(indoorLines)

        val textLayer = SymbolLayer("indoor-map-text", indoorId)
                .withProperties(textField("{name}"))

        style.addLayer(textLayer)
    }

    private fun loadStringAsset(name: String): String =
            context!!.assets.open(name).bufferedReader().readText()

    /**
     * Enable the Maps SDK's LocationComponent to display the device's location the map
     */
    @SuppressWarnings("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {

        // Check whether the location permission has been granted
        if (PermissionsManager.areLocationPermissionsGranted(context)) {

            // Activate and set the preferences for the Maps SDK's LocationComponent
            map?.locationComponent?.apply {
                activateLocationComponent(activity!!.applicationContext, loadedMapStyle)
                isLocationComponentEnabled = true
                cameraMode = CameraMode.NONE
                renderMode = RenderMode.NORMAL
            }

        } else {
            // Use the Mapbox Core Library to request location permissions
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(activity)
        }
    }

    /**
     * Handle the codes associated with requesting the device's location
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Provide an explanation if/when needed to explain why the app is requesting the
     * location permission
     */
    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        toast(R.string.user_location_permission_not_granted)
    }

    /**
     * Handle the results of the location permission request
     */
    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            // Now that user has granted location permissions, once again
            // start the process of showing the device location icon
            enableLocationComponent(map!!.style!!)
        } else {
            toast(R.string.user_location_permission_not_granted)
        }
    }

    companion object {
        const val TAG = "MapboxMap"
    }
}