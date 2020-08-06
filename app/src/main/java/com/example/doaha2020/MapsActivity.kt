package com.example.doaha2020

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polygon
import com.google.maps.android.PolyUtil
import com.google.maps.android.data.kml.KmlContainer
import com.google.maps.android.data.kml.KmlLayer
import com.google.maps.android.data.kml.KmlPoint
import com.google.maps.android.data.kml.KmlPolygon


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    //Google map object
    private lateinit var map: GoogleMap
//
//    //Live location object
//    private lateinit var fusedLocationClient: FusedLocationProviderClient
//
//    //Live location object
//    private lateinit var lastLocation: Location

    //To request location object
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    //Live location updates



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
//
//        //Live location object
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMapReady(googleMap: GoogleMap) {

        //mMap = googleMap
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_HYBRID

        //Set Map Type for ??
        //googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID


        //Add map overlay layer
        val layer = KmlLayer(map, R.raw.proto, applicationContext)
        layer.addLayerToMap()
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney))


        //when a map overlay feature is clicked
        layer.setOnFeatureClickListener {
            //Print out the feautre id
            println(
                "KmlClick" +
                        "Feature clicked: " + it.id
            )
            true
        }

        //when google location map overlay feature is clicked
        map.setOnMarkerClickListener {
            println(
                "UNKOWN Click on current location" +
                        "Feature clicked : " + it.id
            )
            true
        }

        map.setOnMyLocationButtonClickListener {

           // this.getColor()
            //Clicking RHS Corner location center button
            println(
                "Click on current location"
            )

            //Implement a center to live location

            true
        }

        map.setOnMyLocationClickListener {
            //Clicking blue dot
            println(
                "Click on current location"
            )

            //Add place marker on blue live location and this is a action
            //map.addMarker(MarkerOptions().position(LatLng(it.latitude, it.longitude)))

            placeMarkerOnMap(LatLng(it.latitude, it.longitude))

            //val list: MutableList<Geometry<>> = ArrayList()
            var kmlContainerList: MutableIterable<KmlContainer>? = layer.containers


            if(kmlContainerList == null){
                //Exit function if we have an issue
                return@setOnMyLocationClickListener
            }

//            kmlContainerList?.let {
//                //Loop every cotainer
//                println("EveryKMlContainer")
//            }
            //Construct a super polygon which is essentially a list of all points in the kmlcontainer for quick check
            var aSuperPolygon: MutableList<LatLng> = mutableListOf();
            for (aKmlContainer in kmlContainerList){
                //var kmlContainer: KmlContainer? = aKmlContainer
                //check if you need to go through 2 layers of kml
                for(kmlContainerList in aKmlContainer.containers){


                    for(eachMapElement in kmlContainerList.placemarks){
                        println(eachMapElement.styleId)
                        //Need to find map element which live location is in

//                        if(eachMapElement.geometry.geometryObject)
                        println(eachMapElement.geometry)

                        if(eachMapElement.geometry is KmlPolygon) {
                            //When an Polygon
                            var aPolygon : KmlPolygon = eachMapElement.geometry as KmlPolygon

                            //lets make a super polygon to reduce comutaion time for user location
                            aSuperPolygon.addAll(aPolygon.outerBoundaryCoordinates)
                            if(PolyUtil.containsLocation(LatLng(it.latitude, it.longitude), aSuperPolygon, true)){
                                //when a user is within a greater polygon
                                println("in the super polygon")
                            }
                            //If the user's location is within this polygon
                            if(PolyUtil.containsLocation(LatLng(it.latitude, it.longitude), aPolygon.outerBoundaryCoordinates, true)){
                                println("We are in the polygon which the user is located in")



                                //we can access several different points
//                                eachMapElement.properties
//                                for(property in eachMapElement.properties){
//                                    //var hasMapOfProperty : HashMap<String, String> = property as HashMap<String, String>
//                                    //The list of properties we have access to / care about
//                                    property.
//                                }
                            }


//                            val objectA = eachMapElement.geometry
//                            val objectB = objectA.geometryObject
//                            val objectC = listOf(objectB)
//                            val objectD = objectC[0]
//                            if(PolyUtil.containsLocation(LatLng(it.latitude, it.longitude),
//                                listOf(objectC[0]) as MutableList<LatLng>?, true)){
//                                println("working")
//                            }
//                            println("test")
//                            val obj : MutableList<Any> = mutableListOf(eachMapElement.geometry)
//                            for(ob in obj){
//                                println("name")
//
//                            }
//                            for(geo in listOf(eachMapElement.geometry) ){
//                                PolyUtil.containsLocation(LatLng(it.latitude, it.longitude), MulistOf(eachMapElement.geometry), true)
//                                println(geo.toString())
//                            }
//                            for (geoObject in listOf(objectA)) {
//                                for(obj in listOf(geoObject)){
//                                    for(ob in listOf(obj)) {
//                                        println(ob.toString())
//                                    }
//                                }
//                            }
                        }


                        if(eachMapElement.geometry is KmlPoint){
                            //Do not verify Kmlpoint
                            println(eachMapElement.geometry.geometryObject.toString())
                        }
                    }
                }
            }
            true
        }


        setUpMap()
//        map.getUiSettings().setZoomControlsEnabled(true)
//        map.setOnMarkerClickListener(this)

        //Random loop for objects
        for ( containers in layer.containers){
            println(containers.containerId);
            for(conainers1 in containers.containers){
                for( placemarks in conainers1.placemarks){
                    println(placemarks.styleId)
                }
            }
        }

    }


    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        // isMyLocationEnabled = true enables the my-location layer which draws a light blue dot on the user’s location. It also adds a button to the map that, when tapped, centers the map on the user’s location.
        map.isMyLocationEnabled = true


//        map.myLocation.latitude
//        map.myLocation.longitude

//
//        //Set marker on current location
//        placeMarkerOnMap(LatLng(map.myLocation.latitude, map.myLocation.longitude))

//// 2 fusedLocationClient.getLastLocation() gives you the most recent location currently available.
//        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
//            // Got last known location. In some rare situations this can be null.
//            // 3 If you were able to retrieve the the most recent location, then move the camera to the user’s current location.
//            if (location != null) {
//                lastLocation = location
//                val currentLatLng = LatLng(location.latitude, location.longitude)
//                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
//            }
//        }

    }


    private fun placeMarkerOnMap(location: LatLng) {
        // 1
        val markerOptions = MarkerOptions().position(location)

        //add custom icon
//        markerOptions.icon(
//            BitmapDescriptorFactory.fromBitmap(
//            BitmapFactory.decodeResource(resources, R.mipmap.ic_user_location)))
        // 2
        map.addMarker(markerOptions)
    }
}
