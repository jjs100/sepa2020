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
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.maps.android.PolyUtil
import com.google.maps.android.data.kml.KmlContainer
import com.google.maps.android.data.kml.KmlLayer
import com.google.maps.android.data.kml.KmlPolygon
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    //data storage to pass name without intents
    object nation {
        @JvmStatic var name = ""
    }

    private lateinit var mMap: GoogleMap
    private var mapFrag: SupportMapFragment? = null
    private lateinit var mLocationRequest: LocationRequest
    var mLastLocation: Location? = null
    internal var mCurrLocationMarker: Marker? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    lateinit var liveKmlFileString: String
    private var TAG: String = MapsActivity::class.java.simpleName


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

                // adding KML layer to map
                val layer = loadMapFile(MapSource.LOCAL)
                layer.addLayerToMap()

                // get time for calculating runtime
                val timeStart = Calendar.getInstance().time
                println("Start of code: $timeStart")

                val kmlContainerList: MutableIterable<KmlContainer>? = layer.containers
                val aSuperPolygon: MutableList<LatLng> = mutableListOf()
                if (kmlContainerList != null) {
                    for (aKmlContainer in kmlContainerList) {
                        for (eachContainer in aKmlContainer.containers)
                        {
                            for (eachPlacemark in eachContainer.placemarks)
                            {
                                if (eachPlacemark.geometry is KmlPolygon)
                                {
                                    //When a Polygon
                                    val aPolygon : KmlPolygon = eachPlacemark.geometry as KmlPolygon

                                    //make a super polygon to reduce computation time for user location
                                    aSuperPolygon.addAll(aPolygon.outerBoundaryCoordinates)
                                    if (PolyUtil.containsLocation(userLocation, aSuperPolygon, true))
                                    {
                                        //When a user is within a greater polygon
                                        println("in the super polygon")

                                        if (PolyUtil.containsLocation(userLocation, aPolygon.outerBoundaryCoordinates, true))
                                        {
                                            val locName = eachPlacemark.getProperty("name")
                                            // toast is just for testing purposes
                                            val t = Toast.makeText(this@MapsActivity,"You are in $locName", Toast.LENGTH_LONG)
                                            t.show()
                                        }


                                        //Map header
                                        //create val reference to xml textView
                                        val mapHeaderTextView: TextView = findViewById<TextView>(R.id.textViewMapHeader)
                                        if (PolyUtil.containsLocation(userLocation, aPolygon.outerBoundaryCoordinates, true))
                                        {
                                            //assign
                                            val mapHeaderText : String = eachPlacemark.getProperty("name")
                                            //set header text as mapHeaderText var value
                                            mapHeaderTextView.text = mapHeaderText
                                        }

                                        //EXTENSION - If user touched the current location header, shows the acknowledgement of country
                                        //needs to pull acknowledgement from database
                                        val mapAckText : String = "[Acknowledgement of traditional owners here]"

                                        //set acknowledgement text as mapAckText var value
                                        val mapAckTextView: TextView = findViewById<TextView>(R.id.textViewMapAck)
                                        mapAckTextView.text = mapAckText

                                        //if header location is clicked, acknowledgement TextView appears/disappears
                                        mapHeaderTextView.setOnClickListener {
                                            if(mapAckTextView.visibility == View.GONE){
                                                mapAckTextView.visibility = View.VISIBLE
                                            }
                                            else{
                                                mapAckTextView.visibility = View.GONE
                                            }

                                        }

                                        //Searchbar implementation hinging on XML searchView
                                        //val mapSearchView: SearchView = findViewById<SearchView>(R.id.searchViewMap)
                                        //mapSearchView.setSearchableInfo([info])





                                    }
                                }
                            }
                        }
                    }
                }
                // we add and remove the layer from the map
                // this is because the layer needs to be added to the map in this function
                // so that the data within the layer can be used.
                // the removal is so that new polygons don't continuously get drawn whenever
                // the location is retrieved
                layer.removeLayerFromMap()

                // get time for calculating runtime
                val timeEnd = Calendar.getInstance().time
                println("End of code: $timeEnd")
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

        // Initialize the AutocompleteSupportFragment and Places
        Places.initialize(applicationContext, getString(R.string.google_maps_auto_complete_key))
        //val pC: PlacesClient = Places.createClient(applicationContext)

        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.NAME, Place.Field.LAT_LNG))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val newLocation = place.latLng
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 10.0F))
            }

            override fun onError(p0: Status) {
                Log.i(TAG, "An error occurred: $p0")
            }
        })
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
            val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style_standard))

            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }

        // set kml layer
        val layer = KmlLayer(mMap, R.raw.proto, applicationContext)
        // add layer overlay to map
        layer.addLayerToMap()


        //Set map settings
        with(mMap.uiSettings){
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

        // this listen will be changed to send the user
        // to the Nation info activity
	    layer.setOnFeatureClickListener {
            val intent = Intent(this, MainListActivity::class.java)
            val locName = it.getProperty("name")
            nation.name = locName
            val t = Toast.makeText(this@MapsActivity,"this is $locName", Toast.LENGTH_SHORT)
            t.show()
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
        if(mapSource == MapSource.ONLINE){
            var fileString:String = ""
            val inputStream: InputStream = applicationContext.resources.openRawResource(R.raw.online)
            val linesOfFileIterator:Iterator<String> = inputStream.bufferedReader().lineSequence().iterator()
            while(linesOfFileIterator.hasNext()){
                fileString += linesOfFileIterator.next()
            }
            val liveKmlUrl:String = fileString.substringAfter("<href><![CDATA[").substringBefore("]]></href>")
            val inputStreamReader = URL(liveKmlUrl).openConnection() as HttpURLConnection
            val thread = Thread(Runnable {
                liveKmlFileString = inputStreamReader.inputStream.bufferedReader().use(BufferedReader::readText)
            })

            thread.start()
            thread.join()
            return KmlLayer(mMap, ByteArrayInputStream(liveKmlFileString.toByteArray(Charsets.UTF_8)), applicationContext)
        }
        return KmlLayer(mMap, R.raw.proto, applicationContext)
    }

    companion object {
        const val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }
}
