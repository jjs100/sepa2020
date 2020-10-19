package com.doaha

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.HandlerThread
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.doaha.application.DoAHAApplication
import com.doaha.model.enum.MapSource
import com.doaha.model.enum.MapStyle
import com.doaha.model.generator.GeoJSONClassGenerator
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.PolyUtil
import com.google.maps.android.data.geojson.GeoJsonLayer
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


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLoadedCallback, GoogleMap.OnMyLocationButtonClickListener {
    //data storage to pass name without intents
    object Nation {
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
    private var activityVisible: Boolean = true
    private var firstLocationResult: Boolean = false
    var layer: KmlLayer? = null


    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        @SuppressLint("SetTextI18n")
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                //The last location in the list is the newest
                val location = locationList.last()
                mLastLocation = location
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker?.remove()
                }
                // move map camera
                val userLocation = LatLng(location.latitude, location.longitude)
                if (!firstLocationResult) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 8F))
                    firstLocationResult = true
                }

                val camPos = mMap.cameraPosition.target
                val mapHeaderTextView: TextView = findViewById(R.id.textViewMapHeader)
                val mapHeaderRel: RelativeLayout = findViewById(R.id.mapHeaderRel)
                val checkedCamPos = currentRegion(camPos, layer)
                if (checkedCamPos != null) {
                    mapHeaderTextView.text = checkedCamPos
                    //pull acknowledgement from database
                    val mapAckTextView: TextView = findViewById(R.id.textViewMapAck)
                    val docRef = FirebaseFirestore.getInstance().collection(
                        "zones"
                    ).document(checkedCamPos)

                    GlobalScope.launch(Dispatchers.IO) {
                        val region = docRef.get().await()
                        if (region.getString("Acknowledgements") != "") {
                            if(region.getString("Acknowledgements") != null) {
                                mapAckTextView.text = "Acknowledgments: " + region.getString(
                                    "Acknowledgements"
                                )
                            }
                            //no else here as the second if is a catch for slow-updating from firebase.
                            //-> Show's previous region ack until next is available(unless next doesn't exist, in which case, see below else)
                        } else {
                            mapAckTextView.text = getString(R.string.ack_unavailable)
                        }
                    }
                    mapHeaderRel.setOnClickListener {
                        if (mapAckTextView.visibility == View.GONE) {
                            mapAckTextView.visibility = View.VISIBLE
                        } else {
                            mapAckTextView.visibility = View.GONE
                        }
                    }

                    if (!activityVisible) {
                        sendNotification(checkedCamPos)
                    }
                }
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

        //determine if user has seen tooltip before, show if untrue
        val toolTip : ConstraintLayout = findViewById(R.id.toolTip)
        val check : Boolean = (this.application as DoAHAApplication).checkStatefulToolTip(
            getSharedPreferences(getString(R.string.toolTip_used), Context.MODE_PRIVATE)
        )
        if (check){
            toolTip.background.alpha = 180
            toolTip.visibility = View.VISIBLE

        }
        //default visibility is gone in xml, no else needed

        //allow user to hide tooltip
        toolTip.setOnClickListener {
            if(toolTip.visibility == View.VISIBLE) {
                toolTip.visibility = View.GONE
            }
        }

        //tooltip button toggle
        val ttButton : Button = findViewById(R.id.ttButton)
        ttButton.setOnClickListener{
            if(toolTip.visibility == View.VISIBLE) {
                toolTip.visibility = View.GONE
            }
            else{
                toolTip.visibility = View.VISIBLE
                toolTip.background.alpha = 180
            }
        }

        showLocationPrompt()
    }



    public override fun onPause() {
        super.onPause()
        activityVisible = false
    }

    public override fun onResume() {
        super.onResume()
        activityVisible = true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // sets map variable
        mMap = googleMap
        mMap.setOnMapLoadedCallback(this)
        // applies custom map style json
        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, getResourceIdForMapStyle((this.application as DoAHAApplication).getMapStyle(getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)))
                )
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }

        // set kml layer and map settings
        layer = loadMapFile()
        layer!!.addLayerToMap()
        with(mMap.uiSettings){
            //Enable RHS zoom controls for debug
            this.isZoomControlsEnabled = true
            //Enable gesture zoom controls
            this.isZoomGesturesEnabled = true
        }

        // Create nation name polygon layer
        if ((this.application as DoAHAApplication).getIsNationLabelEnabled(getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE))) {
            GeoJsonLayer(mMap, GeoJSONClassGenerator.create(layer!!)).addLayerToMap()
        }

        //start a handler thread for looper
        HandlerThread("Location").start()
	    mLocationRequest = LocationRequest()
        // In Milliseconds
        mLocationRequest.interval = 5000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

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
                    HandlerThread("Location").looper
                )
                mMap.isMyLocationEnabled = true
                mMap.setOnMyLocationButtonClickListener(this)
            } else {
                //Request Location Permission
                checkLocationPermission()
            }
        } else {
            mFusedLocationClient?.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback,
                HandlerThread("Location").looper
            )
            mMap.isMyLocationEnabled = true
            mMap.setOnMyLocationButtonClickListener(this)
        }
        // sends user to nation information page
	    layer!!.setOnFeatureClickListener {
            //Check if user has selected nation name overlay, if so ignore
            if(it != null) {
                val locName = it.getProperty("name")
                Nation.name = locName
                startActivity(Intent(this, MainListActivity::class.java))
            }
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
                            HandlerThread("Location").looper
                        )
                    }
                } else {
                    // disables location functionality
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    private fun loadMapFile(): KmlLayer {
        if ((this.application as DoAHAApplication).getXmlImportType(
                getSharedPreferences(
                    getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE
                )
            ) == MapSource.ONLINE
        ) {
            var fileString = ""
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
            ) && (this.application as DoAHAApplication).getTheRegionUserWasPreviouslyIn(
                getSharedPreferences(
                    getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE
                )
            ) != locName
        ) {
            (this.application as DoAHAApplication).setTheRegionUserWasPreviouslyIn(
                getSharedPreferences(
                    getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE
                ), locName
            )
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

    private fun currentRegion(location: LatLng, layer: KmlLayer?): String? {
        val kmlContainerList: MutableIterable<KmlContainer>? = layer?.containers
        val aSuperPolygon: MutableList<LatLng> = mutableListOf()
        if (kmlContainerList != null) {
            for (aKmlContainer in kmlContainerList) {
                for (eachContainer in aKmlContainer.containers) {
                    for (eachPlacemark in eachContainer.placemarks) {
                        if (eachPlacemark.geometry is KmlPolygon) {
                            //When a Polygon
                            val aPolygon: KmlPolygon = eachPlacemark.geometry as KmlPolygon
                            //make a super polygon to reduce computation time for user location
                            aSuperPolygon.addAll(aPolygon.outerBoundaryCoordinates)
                            if (PolyUtil.containsLocation(location, aSuperPolygon,true) && PolyUtil.containsLocation(location, aPolygon.outerBoundaryCoordinates, true)) {
                                return eachPlacemark.getProperty("name")
                            }
                        }
                    }
                }
            }
        }
        return null
    }

    override fun onMyLocationButtonClick(): Boolean {
        //Do nothing if location is not currently set
        if(mLastLocation == null){
            Toast.makeText(this@MapsActivity, "Your current location hasn't loaded just yet, please try again in a moment", Toast.LENGTH_SHORT).show()
            return true
        }
        //Set custom zoom distance for current location button
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude),
            8F
        ))

        //If set to true default method invocation will trigger
        return true
    }

    private fun getResourceIdForMapStyle(value : MapStyle) : Int {
        return when(value){
            MapStyle.SILVER -> R.raw.map_style_silver
            MapStyle.RETRO -> R.raw.map_style_retro
            else -> R.raw.map_style_standard
        }
    }

    companion object {
        const val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }

    override fun onMapLoaded() {
        // Set map bounds to australia and show aus map
        val australiaBounds = LatLngBounds(
            LatLng(-47.1, 110.4), LatLng(-8.6, 156.4)
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(australiaBounds, 0))
    }

    private fun showLocationPrompt() {
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val result: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                // All location settings are satisfied. The client can initialize location
                // requests here.
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            // Cast to a resolvable exception.
                            val resolvable: ResolvableApiException = exception as ResolvableApiException
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(
                                this@MapsActivity, LocationRequest.PRIORITY_HIGH_ACCURACY
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        } catch (e: ClassCastException) {
                            // Ignore, should be an impossible error.
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.

                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                    }
                }
            }
        }
    }
}
