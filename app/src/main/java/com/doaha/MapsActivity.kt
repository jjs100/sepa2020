package com.doaha

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.doaha.model.enum.MapSource
import com.doaha.model.map.kml.CustomKmlContainer
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.google.maps.android.data.kml.KmlLayer
import com.google.maps.android.data.kml.KmlPolygon
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var mapFrag: SupportMapFragment? = null
    private lateinit var mLocationRequest: LocationRequest
    var mLastLocation: Location? = null
    internal var mCurrLocationMarker: Marker? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    lateinit var liveKmlFileString: String
    private final var TAG: String = MapsActivity.javaClass.simpleName
    private lateinit var tasmaniaSoutheast: CustomKmlContainer
    private lateinit var riverineSpencer: CustomKmlContainer
    private lateinit var eyreGulf: CustomKmlContainer
    private lateinit var northeastRainforest: CustomKmlContainer
    private lateinit var eastWestCapeTorresStrait: CustomKmlContainer
    private lateinit var desert: CustomKmlContainer
    private lateinit var southwestNorthwest: CustomKmlContainer
    private lateinit var kimberleyFitzmaurice: CustomKmlContainer
    private lateinit var northArnhem: CustomKmlContainer

    //set header text as mapHeaderText var value
    private lateinit var mapHeaderTextView: TextView

    //set acknowledgement text as mapAckText var value
    private lateinit var mapAckTextView: TextView

    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                val location = locationList.last()
                Log.i("MapsActivity", "Location: " + location.latitude + " " + location.longitude)
                mLastLocation = location
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker?.remove()
                }

                // move map camera
                val userLocation = LatLng(location.latitude, location.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10.0F))

                //Finish entire list
                    verifyLocationToNation(
                        userLocation
                    )

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // set up app view
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        supportActionBar?.title = "Map Location Activity"

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // map ui element
        mapFrag = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFrag?.getMapAsync(this)
    }

    public override fun onPause() {
        super.onPause()

        //stop location updates when Activity is no longer active
        mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // sets map variable
        mMap = googleMap
        // sets map type to HYBRID, i.e. satellite view with road overlay
        //mMap.mapType = GoogleMap.MAP_TYPE_HYBRID

        // applies custom map style json
        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this,
                    R.raw.map_style_standard
                )
            )

            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }

        // set kml layer
        val layer = loadMapFile(MapSource.LOCAL)
        // add layer overlay to map
        layer.addLayerToMap()
        // create super zones
        createSuperZoneObjects(layer)

        //Set map settings
        with(mMap.uiSettings) {
            //Enable RHS zoom controls for debug
            this.isZoomControlsEnabled = true
            //Enable gesture zoom controls
            this.isZoomGesturesEnabled = true
        }

        // pings user location
        mLocationRequest = LocationRequest()
        // In Milliseconds || 30 secs
        mLocationRequest.interval = 30000
        mLocationRequest.fastestInterval = 30000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        // check/request app permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //Location Permission already granted
                mFusedLocationClient?.requestLocationUpdates(
                    mLocationRequest,
                    mLocationCallback,
                    Looper.myLooper()
                )
                mMap.isMyLocationEnabled = true
            } else {
                //Request Location Permission
                checkLocationPermission()
            }
        } else {
            mFusedLocationClient?.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback,
                Looper.myLooper()
            )
            mMap.isMyLocationEnabled = true
        }

        // this listen will be changed to send the user
        // to the Nation info activity
        layer.setOnFeatureClickListener {
            val intent = Intent(this, MainListActivity::class.java)
            val locName = it.getProperty("name")
            intent.putExtra("name", locName)
            val t = Toast.makeText(this@MapsActivity, "this is $locName", Toast.LENGTH_SHORT)
            t.show()
            startActivity(intent)
        }

        mapHeaderTextView = findViewById<TextView>(R.id.textViewMapHeader)
        mapAckTextView = findViewById<TextView>(R.id.textViewMapAck)
        //if header location is clicked, acknowledgement TextView appears/disappears
        mapHeaderTextView.setOnClickListener {
            if (mapAckTextView.visibility == View.GONE) {
                mapAckTextView.visibility = View.VISIBLE
            } else {
                mapAckTextView.visibility = View.GONE
            }

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
                        mMap.isMyLocationEnabled = true
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

    fun loadMapFile(mapSource: MapSource): KmlLayer {
        if (mapSource == MapSource.ONLINE) {
            var fileString: String = ""
            val inputStream: InputStream =
                applicationContext.resources.openRawResource(R.raw.online)
            val linesOfFileIterator: Iterator<String> =
                inputStream.bufferedReader().lineSequence().iterator()
            while (linesOfFileIterator.hasNext()) {
                fileString += linesOfFileIterator.next()
            }
            val liveKmlUrl: String =
                fileString.substringAfter("<href><![CDATA[").substringBefore("]]></href>")
            val inputStreamReader = URL(liveKmlUrl).openConnection() as HttpURLConnection
            val thread = Thread(Runnable {
                liveKmlFileString =
                    inputStreamReader.inputStream.bufferedReader().use(BufferedReader::readText)
            })

            thread.start()
            thread.join()
            return KmlLayer(
                mMap,
                ByteArrayInputStream(liveKmlFileString.toByteArray(Charsets.UTF_8)),
                applicationContext
            )
        }
        return KmlLayer(mMap, R.raw.proto, applicationContext)
    }

    private fun createSuperZoneObjects(kmlLayer: KmlLayer) {
        for (kmlContainer in kmlLayer.containers) {
            for (aLayer in kmlContainer.containers) {
                when (aLayer.getProperty("name")) {
                    "Tasmania/Southeast" -> tasmaniaSoutheast = CustomKmlContainer(aLayer)
                    "Riverine/Spencer" -> riverineSpencer = CustomKmlContainer(aLayer)
                    "Eyre/Gulf" -> eyreGulf = CustomKmlContainer(aLayer)
                    "Northeast/Rainforest" -> northeastRainforest = CustomKmlContainer(aLayer)
                    "East Cape/West Cape/Torres Strait" -> eastWestCapeTorresStrait = CustomKmlContainer(aLayer)
                    "Desert" -> desert = CustomKmlContainer(aLayer)
                    "Southwest/Northwest" -> southwestNorthwest = CustomKmlContainer(aLayer)
                    "Kimberley/Fitzmaurice" -> kimberleyFitzmaurice = CustomKmlContainer(aLayer)
                    "North/Arnhem" -> northArnhem = CustomKmlContainer(aLayer)
                }
            }
        }
    }

    private fun verifyLocationToNation(
        userLocation: LatLng
    ) {
        if(!this::tasmaniaSoutheast.isInitialized){
            return
        }
        var listOfLayers: List<CustomKmlContainer> = listOf<CustomKmlContainer>(
            tasmaniaSoutheast,
            riverineSpencer,
            eyreGulf,
            northeastRainforest,
            eastWestCapeTorresStrait,
            desert,
            southwestNorthwest,
            kimberleyFitzmaurice,
            northArnhem
        )
        for (aLayer in listOfLayers) {
            if (!PolyUtil.containsLocation(userLocation, aLayer.aSuperRegion, true)) {
                continue
            }

            for (aPlaceMarker in aLayer.kmlContainer.placemarks) {
                if (aPlaceMarker is KmlPolygon) {
                    if (!PolyUtil.containsLocation(
                            userLocation,
                            aPlaceMarker.outerBoundaryCoordinates,
                            true
                        )
                    ) {
                        continue
                    }

                    // Toast is just for testing purposes
                    Toast.makeText(
                        this@MapsActivity,
                        "You are in " + aPlaceMarker.getProperty("name"),
                        Toast.LENGTH_LONG
                    ).show()
                    mapHeaderTextView.text = aPlaceMarker.getProperty("name")
                    mapAckTextView.text = "[Acknowledgement of traditional owners here]"

                } else {
                    continue
                }
            }
        }
    }

    companion object {
        const val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }
}