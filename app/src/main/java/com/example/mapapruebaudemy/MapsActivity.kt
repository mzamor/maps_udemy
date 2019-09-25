package com.example.mapapruebaudemy

import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnSuccessListener
import javax.security.auth.callback.Callback

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener , GoogleMap.OnMarkerDragListener{
    private lateinit var mMap: GoogleMap
    private val coarseLocationPermission = android.Manifest.permission.ACCESS_COARSE_LOCATION
    private val fineLocationPermission = android.Manifest.permission.ACCESS_FINE_LOCATION
    private val QUERY_PERMISSION_CODE = 100
    private var fusedLocationProviderClient : FusedLocationProviderClient? = null
    private var locationRequest : LocationRequest? = null
    private var callback:LocationCallback?=null
    private var markGolden:Marker? = null
    private var markPisa:Marker? = null
    private  var markPiramide:Marker? = null
    private var markersList : ArrayList<Marker>? = null
    private var myPosition : LatLng? = null

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
                        myPosition = LatLng(location.latitude, location.longitude)
                        mMap.addMarker(MarkerOptions().position(myPosition!!).title("Home"))
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
        staticMarkers()
        createListeners()
        prepareMarkers()
        drawCircleArea()
    }

    private fun createListeners(){
        mMap.setOnMarkerClickListener(this)
        mMap.setOnMarkerDragListener(this)
    }
    private fun staticMarkers(){
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
    }

    private fun drawCircleArea(){
            var coordinates = CircleOptions().center(LatLng(43.7229, 10.3965)).radius(2000.0).strokeColor(Color.BLACK).strokeWidth(10f)
            mMap.addCircle(coordinates)
    }

    private fun prepareMarkers() {
        markersList = ArrayList()
        mMap.setOnMapLongClickListener {
            location:LatLng? ->
            markersList?.add(mMap.addMarker(MarkerOptions().position(location!!).title("location with longClick")))
            markersList?.last()!!.isDraggable = true
      //      val coords = LatLng(markersList?.last()!!.position.latitude,markersList?.last()!!.position.longitude)
      //      val origin = "origin=" + myPosition?.latitude.toString() + "," + myPosition?.longitude.toString() + "&"
      //      val destination = "destination=" + coords.latitude.toString() + "," + coords.longitude.toString()  +"&"
      //      val parameters = origin + destination + "sensor=false&mode=driving"
      //      loadURL("http://maps.googleapis.com/maps/api/directions/json?" + parameters)
        }
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

    override fun onMarkerDragEnd(marker: Marker?) {
    }

    override fun onMarkerDragStart(marker: Marker?) {
    }

    override fun onMarkerDrag(marker: Marker?) {
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

    private fun loadURL(url:String){
        val queue = Volley.newRequestQueue(this)
        val query = StringRequest(Request.Method.GET,url, Response.Listener<String>
        {
            response ->
            Log.d("HTTP",response)
        },Response.ErrorListener {})
        queue.add(query)
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
