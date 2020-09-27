package com.doaha

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.doaha.application.DoAHAApplication
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.PolyUtil
import com.google.maps.android.data.kml.KmlContainer
import com.google.maps.android.data.kml.KmlLayer
import com.google.maps.android.data.kml.KmlPolygon
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


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
    private lateinit var liveKmlFileString: String
    private var TAG: String = MapsActivity::class.java.simpleName
    private var channelID = "Notification_Channel"
    private val notificationID = 101


    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        @SuppressLint("SetTextI18n")
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
                // adding KML layer to map
                val layer = loadMapFile(MapSource.LOCAL)
                layer.addLayerToMap()
                // Update Current Location Header
                val camPos = mMap.cameraPosition.target
                val mapHeaderTextView: TextView = findViewById(R.id.textViewMapHeader)
                val checkedCamPos = currentRegion(camPos, layer)
                if (checkedCamPos != null) {
                    mapHeaderTextView.text = checkedCamPos
                }

                val checkedUserLocation = currentRegion(userLocation, layer)
                if (checkedUserLocation != null) {
                    val mapHeaderText : String = checkedUserLocation
                    //pull acknowledgement from database
                    val mapAckTextView: TextView = findViewById(R.id.textViewMapAck)
                    val docRef = FirebaseFirestore.getInstance().collection(
                        "zones"
                    ).document(mapHeaderText)

                    GlobalScope.launch(Dispatchers.Main) {
                        delay(1000L)
                        val region = docRef.get().await()
                        if (region.getString("Acknowledgements") != "") {
                            mapAckTextView.text = "Acknowledgments: " + region.getString(
                                "Acknowledgements"
                            )
                        } else {
                            mapAckTextView.text = getString(R.string.ack_unavailable)
                        }
                    }
                    //if header location is clicked, acknowledgement TextView appears/disappears
                    mapHeaderTextView.setOnClickListener {
                        if(mapAckTextView.visibility == View.GONE){
                            mapAckTextView.visibility = View.VISIBLE
                        }
                        else{
                            mapAckTextView.visibility = View.GONE
                        }
                    }

                }
                //we need to add and remove the layer for use in this function so polygons don't get drawn continuously
                layer.removeLayerFromMap()
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
        createNotificationChannel()

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
                mMap.addMarker(
                    newLocation?.let {
                        MarkerOptions()
                            .position(it)
                    }
                )
            }
            override fun onError(p0: Status) {
                Log.i(TAG, "An error occurred: $p0")
            }
        })

        //allow user to hide tooltip
        val toolTip : LinearLayout = findViewById(R.id.toolTip)
        toolTip.setOnClickListener {
            if(toolTip.visibility == View.VISIBLE) {
                toolTip.visibility = View.GONE
            }
        }
    }

    public override fun onPause() {
        super.onPause()
        //Stop location updates when Activity is no longer active
        //mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // sets map variable
        mMap = googleMap
        // applies custom map style json
        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.map_style_standard
                )
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
        // Set map bounds to australia and show aus map
        val australiaBounds = LatLngBounds(
            LatLng(-47.1, 110.4), LatLng(-8.6, 156.4)
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(australiaBounds, 0))

        // set kml layer and map settings
        val layer = KmlLayer(mMap, R.raw.proto, applicationContext)
        layer.addLayerToMap()
        with(mMap.uiSettings){
            //Enable RHS zoom controls for debug
            this.isZoomControlsEnabled = true
            //Enable gesture zoom controls
            this.isZoomGesturesEnabled = true
        }

        // pings user location In Milliseconds || 30 secs
	    mLocationRequest = LocationRequest()
        // In Milliseconds || 30 secs
        mLocationRequest.interval = 10000
        mLocationRequest.fastestInterval = 10000
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
        // sends user to nation information page
	    layer.setOnFeatureClickListener {
            val intent = Intent(this, MainListActivity::class.java)
            val locName = it.getProperty("name")
            nation.name = locName
            val t = Toast.makeText(this@MapsActivity, "this is $locName", Toast.LENGTH_SHORT)
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* After the user sees the explanation, try again to request the permission.
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
                    // Location permissions enabled.
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
                    // disables location functionality
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    fun loadMapFile(mapSource: MapSource): KmlLayer {
        if(mapSource == MapSource.ONLINE){
            var fileString = ""
            val inputStream: InputStream = applicationContext.resources.openRawResource(R.raw.online)
            val linesOfFileIterator:Iterator<String> = inputStream.bufferedReader().lineSequence().iterator()
            while(linesOfFileIterator.hasNext()){
                fileString += linesOfFileIterator.next()
            }
            val liveKmlUrl:String = fileString.substringAfter("<href><![CDATA[").substringBefore("]]></href>")
            val inputStreamReader = URL(liveKmlUrl).openConnection() as HttpURLConnection
            val thread = Thread(Runnable {
                liveKmlFileString = inputStreamReader.inputStream.bufferedReader().use(
                    BufferedReader::readText
                )
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

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(locName: String) {
        if ((this.application as DoAHAApplication).getIsNotificationEnabled(
                getSharedPreferences(
                    getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE
                )
            )
        ) {
            val intent = Intent(this, MapsActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
            // set notification content
            val builder = NotificationCompat.Builder(this, channelID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Test Notification")
                .setContentText("You are in $locName")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(this)) {
                notify(notificationID, builder.build())
            }
        }
    }

    private fun currentRegion(location: LatLng, layer: KmlLayer): String? {
        val kmlContainerList: MutableIterable<KmlContainer>? = layer.containers
        val aSuperPolygon: MutableList<LatLng> = mutableListOf()
        if (kmlContainerList != null) {
            for (aKmlContainer in kmlContainerList) {
                for (eachContainer in aKmlContainer.containers) {
                    for (eachPlacemark in eachContainer.placemarks) {
                        if (eachPlacemark.geometry is KmlPolygon) {
                            //When a Polygon
                            val aPolygon : KmlPolygon = eachPlacemark.geometry as KmlPolygon
                            //make a super polygon to reduce computation time for user location
                            aSuperPolygon.addAll(aPolygon.outerBoundaryCoordinates)
                            if (PolyUtil.containsLocation(location, aSuperPolygon, true)) {
                                if (PolyUtil.containsLocation(location, aPolygon.outerBoundaryCoordinates, true)) {
                                    return eachPlacemark.getProperty("name")
                                }
                            }
                        }
                    }
                }
            }
        }
        return null
    }

    companion object {
        const val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }
}
