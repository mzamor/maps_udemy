package com.example.mapapruebaudemy

import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener
import javax.security.auth.callback.Callback

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var mMap: GoogleMap
    private val coarseLocationPermission = android.Manifest.permission.ACCESS_COARSE_LOCATION
    private val fineLocationPermission = android.Manifest.permission.ACCESS_FINE_LOCATION
    private val QUERY_PERMISSION_CODE = 100
    var fusedLocationProviderClient : FusedLocationProviderClient? = null
    var locationRequest : LocationRequest? = null
    var callback:LocationCallback?=null
    var markGolden:Marker? = null
    var markPisa:Marker? = null
    var markPiramide:Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        initLocationRequest()
        callback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                if(mMap!=null){
                    mMap.isMyLocationEnabled = true
                    mMap.uiSettings.isMyLocationButtonEnabled =true
                    for(location in locationResult?.locations!!){
                        Toast.makeText(applicationContext,location.latitude.toString() + " , " + location.longitude.toString(), Toast.LENGTH_SHORT).show()
                        val myPosition = LatLng(location.latitude, location.longitude)
                        mMap.addMarker(MarkerOptions().position(myPosition).title("Home"))
                      //  mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition))
                    }
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if(isLocationPermissionValidated()){
            getLocation()
        }else{
            askForLocationPermission()
        }
        val TORRE_PISA = LatLng(43.7229, 10.3965)
        val GOLDEN_STATE = LatLng(37.8199, -122.478)
        val PIRAMIDES = LatLng(29.9772, 31.1324)
        markGolden = mMap.addMarker(MarkerOptions().position(GOLDEN_STATE)
            .title("Golden Gate").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))
        markGolden?.tag = 0
        markPisa = mMap.addMarker(MarkerOptions().position(TORRE_PISA).title("Torre de Pisa")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))
        markPisa?.tag = 0
        markPiramide= mMap.addMarker(MarkerOptions().position(PIRAMIDES).title("Piramides de Egipto")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)))
        markPiramide?.tag = 0
        mMap.setOnMarkerClickListener(this)
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        var clickNumber = marker?.tag as? Int
        if(clickNumber!=null){
            clickNumber++
            marker?.tag = clickNumber
            Toast.makeText(applicationContext,"cantidad de clicks ${clickNumber}",Toast.LENGTH_LONG).show()
        }
        return false
    }

    private fun initLocationRequest(){
        locationRequest = LocationRequest()
        locationRequest?.interval = 10000
        locationRequest?.fastestInterval = 5000
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun isLocationPermissionValidated(): Boolean {
        val isFineLocationPermission = ActivityCompat.checkSelfPermission(this,fineLocationPermission) == PackageManager.PERMISSION_GRANTED
        val isCoarseLocationPermision = ActivityCompat.checkSelfPermission(this, coarseLocationPermission) == PackageManager.PERMISSION_GRANTED
        return isFineLocationPermission && isCoarseLocationPermision
    }

    private fun askForLocationPermission(){
        val rational = ActivityCompat.shouldShowRequestPermissionRationale(this, fineLocationPermission)
        if (rational){
            askForQueryPermission()
        }else{
            askForQueryPermission()
        }
    }

    private fun askForQueryPermission(){
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
                if (grantResults.size > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    getLocation()
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

    private fun getLocation() {
        /*fusedLocationProviderClient?.lastLocation?.addOnSuccessListener(this, object: OnSuccessListener<Location>{
            override fun onSuccess(location: Location?) {
                Toast.makeText(applicationContext, location?.latitude.toString() + " - " + location?.longitude.toString(),Toast.LENGTH_LONG).show()
            }
       })*/
        fusedLocationProviderClient?.requestLocationUpdates(locationRequest,callback,null)
    }


    private fun stopUpdateLocation(){
        fusedLocationProviderClient?.removeLocationUpdates(callback)
    }
    override fun onStart() {
        super.onStart()
        if(isLocationPermissionValidated()){
            getLocation()
        }else{
            askForLocationPermission()
        }
    }

    override fun onPause() {
        super.onPause()
        stopUpdateLocation()
    }
}
