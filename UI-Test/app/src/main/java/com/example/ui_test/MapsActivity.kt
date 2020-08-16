package com.example.ui_test

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.google.maps.android.data.kml.KmlContainer
import com.google.maps.android.data.kml.KmlLayer
import com.google.maps.android.data.kml.KmlPolygon


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var mapFrag: SupportMapFragment? = null
    lateinit var mLocationRequest: LocationRequest
    var mLastLocation: Location? = null
    internal var mCurrLocationMarker: Marker? = null
    internal var mFusedLocationClient: FusedLocationProviderClient? = null

    internal var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                val location = locationList.last()
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude())
                mLastLocation = location
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker?.remove()
                }

                //move map camera
                val userLocation = LatLng(location.latitude, location.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 11.0F))

                // LEAVING THIS CODE HERE FOR REFERENCE
//                // Adding a zone
//                var coord1 = LatLng(-37.792749, 145.226018) // top left
//                var coord2 = LatLng(-37.793805, 145.235371) // top right
//                var coord3 = LatLng(-37.796675, 145.235060) // bottom right
//                var coord4 = LatLng(-37.795770, 145.225506) // bottom left
//                val NRingwoodPoints = listOf(coord1, coord2, coord3, coord4) // putting coords into a list. list is for the containsLocation() call
//                // saving coords and lines to variable. can't be done with list because the add() needs in format LatLng not List<LatLng>
//                val ringwoodBlock = PolygonOptions().add(coord1, coord2, coord3, coord4).strokeColor(Color.GREEN)
//                mMap.addPolygon(ringwoodBlock) // drawing onto map
//
//                var coord5 = LatLng(-37.789027, 145.226554) // top left
//                var coord6 = LatLng(-37.790083, 145.235969) // top right
//                var coord7 = LatLng(-37.793805, 145.235371) // bottom right
//                var coord8 = LatLng(-37.792749, 145.226018) // bottom left
//                var coord9 = LatLng(-37.788688, 145.228673) // top middle
//                val NRingwoodPoints2 = listOf(coord5, coord6, coord7, coord8, coord9)
//                val ringwoodBlock2 = PolygonOptions().add(coord5, coord9, coord6, coord7, coord8).strokeColor(Color.RED)
//                mMap.addPolygon((ringwoodBlock2))

//                if (PolyUtil.containsLocation(userLocation, NRingwoodPoints, true)) // if user in polygon
//                {
//                    // TRUE: Tell user they are in zone
//                    val t = Toast.makeText(this@MapsActivity, "You are in GREEN ZONE", Toast.LENGTH_LONG)
//                    t.show()
//                }
//                else if (PolyUtil.containsLocation(userLocation, NRingwoodPoints2, true))
//                {
//                    val t = Toast.makeText(this@MapsActivity, "You are in RED ZONE", Toast.LENGTH_LONG)
//                    t.show()
//                }

                val layer = KmlLayer(mMap, R.raw.proto, applicationContext)
                layer.addLayerToMap()

                var kmlContainerList: MutableIterable<KmlContainer>? = layer.containers

                var aSuperPolygon: MutableList<LatLng> = mutableListOf()
                if (kmlContainerList != null) {
                    for (aKmlContainer in kmlContainerList) {
                        for (kmlContainerList in aKmlContainer.containers)
                        {
                            for (eachMapElement in kmlContainerList.placemarks)
                            {
                                if (eachMapElement.geometry is KmlPolygon)
                                {
                                    //When a Polygon
                                    var aPolygon : KmlPolygon = eachMapElement.geometry as KmlPolygon

                                    //make a super polygon to reduce computation time for user location
                                    aSuperPolygon.addAll(aPolygon.outerBoundaryCoordinates)
                                    if (PolyUtil.containsLocation(userLocation, aSuperPolygon, true))
                                    {
                                        //When a user is within a greater polygon
                                        println("in the super polygon")

                                        if (PolyUtil.containsLocation(userLocation, aPolygon.outerBoundaryCoordinates, true))
                                        {
                                            val t = Toast.makeText(this@MapsActivity, "You are in the northern melbourne zone", Toast.LENGTH_LONG)
                                            t.show()
                                        }

                                        //Map header
                                        //Need to implement grabbing the region name for this string for header
                                        var mapHeaderText : String = "FillerText(until region can be pulled)"

                                        //set header text as mapHeaderText var value
                                        val mapHeaderTextView: TextView = findViewById<TextView>(R.id.textViewMapHeader)
                                        mapHeaderTextView.text = mapHeaderText

                                        /*just for ref - can change stuff onclick
                                        mapHeaderTextView.setOnClickListener {
                                            mapHeaderTextView.text = "onClick text"
                                        }*/

                                        var mapAckText : String = "[Acknowledgement of traditional owners here]"

                                        //set acknowledgement text as mapAckText var value
                                        val mapAckTextView: TextView = findViewById<TextView>(R.id.textViewMapAck)
                                        mapAckTextView.text = mapAckText
                                    }


                                }
                            }
                        }
                    }
                }


            }

        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        supportActionBar?.title = "Map Location Activity"

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mapFrag = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFrag?.getMapAsync(this)
    }

    public override fun onPause() {
        super.onPause()

        //stop location updates when Activity is no longer active
        mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        //mMap.mapType = GoogleMap.MAP_TYPE_HYBRID

        val layer = KmlLayer(mMap, R.raw.proto, applicationContext)
        layer.addLayerToMap()

        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 120000 // In Milliseconds || two minute interval
        mLocationRequest.fastestInterval = 120000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //Location Permission already granted
                mFusedLocationClient?.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
                mMap.isMyLocationEnabled = true
            } else {
                //Request Location Permission
                checkLocationPermission()
            }
        } else {
            mFusedLocationClient?.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
            mMap.isMyLocationEnabled = true
        }

        layer.setOnFeatureClickListener {
            val intent = Intent(this, MainListActivity::class.java)
            intent.putExtra("name", it.getProperty("name"))
            startActivity(intent)
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(
                            this@MapsActivity,
                            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST_LOCATION
                        )
                    }
                    .create()
                    .show()


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {

                        mFusedLocationClient?.requestLocationUpdates(
                            mLocationRequest,
                            mLocationCallback,
                            Looper.myLooper()
                        )
                        mMap.setMyLocationEnabled(true)
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }// other 'case' lines to check for other
        // permissions this app might request
    }

    companion object {
        val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }
}
