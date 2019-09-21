package com.example.mapapruebaudemy

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private val coarseLocationPermission = android.Manifest.permission.ACCESS_COARSE_LOCATION
    private val fineLocationPermission = android.Manifest.permission.ACCESS_FINE_LOCATION
    private val QUERY_PERMISSION_CODE = 100

    var fusedLocationProviderClient : FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private fun isLocationPermissionValidated(): Boolean {
        val isFineLocationPermission = ActivityCompat.checkSelfPermission(this,fineLocationPermission) == PackageManager.PERMISSION_GRANTED
        val isCoarseLocationPermision = ActivityCompat.checkSelfPermission(this, coarseLocationPermission) == PackageManager.PERMISSION_GRANTED
        return isFineLocationPermission && isCoarseLocationPermision
    }
    private fun askForLocation(){
        val rational = ActivityCompat.shouldShowRequestPermissionRationale(this, fineLocationPermission)

        if (rational){
            askForLocationPermission()
        }else{
            askForLocationPermission()
        }
    }

    private fun askForLocationPermission(){
        requestPermissions(arrayOf(coarseLocationPermission,fineLocationPermission),QUERY_PERMISSION_CODE)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            QUERY_PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults.get(0) == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(
                        this,
                        "No diste permiso para acceder a la ubicacion",
                        Toast.LENGTH_LONG
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if(isLocationPermissionValidated()){
            askForLocation()
        }else{
            askForLocationPermission()
        }
    }

}
