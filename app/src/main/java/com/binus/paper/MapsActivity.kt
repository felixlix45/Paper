package com.binus.paper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.binus.paper.model.DataLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        val data = intent.getParcelableExtra(latLong) as DataLocation
        latitude = data.latitude
        longitude = data.longitude
    }

    companion object {
        const val latLong = "EXTRA_LAT_LONG"
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val binus = LatLng(latitude, longitude)
        mMap.addMarker(MarkerOptions().position(binus).title("Marker in BINUS"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(binus, 20F))
        mMap.setMaxZoomPreference(50F)
        mMap.setMinZoomPreference(5F)
//        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.))


        val anchor = LatLngBounds(
            LatLng(-6.201789, 106.781723),
            LatLng(-6.201778, 106.781861)
        )

        val overlayMap2 = GroundOverlayOptions()
            .image(BitmapDescriptorFactory.fromResource(R.drawable.map))
            .anchor(0F, 1F)
            .position(LatLng(-6.201973, 106.781782), 11F, 21F)
            .bearing(-5F)

        this.mMap.addGroundOverlay(overlayMap2)

    }
}