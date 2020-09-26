package com.doaha.model.map.kml

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.data.kml.KmlContainer
import com.google.maps.android.data.kml.KmlPlacemark
import com.google.maps.android.data.kml.KmlPolygon
import java.util.*
import kotlin.collections.ArrayList

class CustomKmlContainer() {

    lateinit var kmlContainer:KmlContainer
    lateinit var aSuperRegion:MutableList<LatLng>

    constructor(aKmlContainer: KmlContainer) : this() {
        this.kmlContainer = aKmlContainer
        this.aSuperRegion = getOuterBoundaryForSuperRegion(aKmlContainer)
    }

    private fun getOuterBoundaryForSuperRegion(aKmlContainer: KmlContainer): MutableList<LatLng>{
        var listOfLatLngPoints: MutableList<LatLng> = ArrayList()
        listOfLatLngPoints = extractAllPointsFromAKmlContainer(aKmlContainer)

        var aSuperRegionListOfLatLngPoints: MutableList<LatLng> = ArrayList()
        aSuperRegionListOfLatLngPoints = grahamScan(listOfLatLngPoints)

        return aSuperRegionListOfLatLngPoints
    }

    private fun extractAllPointsFromAKmlContainer(aKmlContainer: KmlContainer): MutableList<LatLng>{
        var listOfLatLngPoints: MutableList<LatLng> = ArrayList()
        for (aMapObject in aKmlContainer.placemarks) {
            listOfLatLngPoints.addAll(extractAllPointFromAnObject(aMapObject))
        }
        return listOfLatLngPoints
    }
    private fun extractAllPointFromAnObject(aMapObject: KmlPlacemark):MutableList<LatLng>{
        return if(aMapObject.geometry is KmlPolygon){
            return (aMapObject.geometry as KmlPolygon).outerBoundaryCoordinates
        }else{
            ArrayList()
        }
    }

    fun grahamScan(listOfLatLngPoints : MutableList<LatLng>): MutableList<LatLng> {
        var variableListOfLatLngPoints = listOfLatLngPoints
        var stack = Stack<LatLng>()

        //the lowest y-coordinate and leftmost point
        var P0:LatLng? = null
        variableListOfLatLngPoints.forEach {

            if(P0 == null){
                //Assign P0 a value if not set
                P0 = it
            }else if(P0!!.latitude > it.latitude ){
                //Check if point in lowest y
                P0 = it
            }else if(P0!!.latitude == it.latitude && P0!!.longitude > it.longitude){
                //Check if same y value then check for left most x value
                P0 = it
            }
        }

        //Sort by the polar angle between two points
        variableListOfLatLngPoints.sortBy {
            kotlin.math.atan2((it.latitude - P0!!.latitude), (it.longitude - P0!!.longitude))
        }

        for(LanLngPoint in listOfLatLngPoints){
            //Pop the last point from stack if we turn clockwise to reach the next point
            while(stack.size > 2 && checkForCounterClockWiseTurn(stack[stack.size-2], stack[stack.size-1], LanLngPoint)){
                stack.pop()
            }
            //Add point to stack
            stack.add(LanLngPoint)
        }

        return stack
    }

    private fun checkForCounterClockWiseTurn(secondFromTopOfStackPoint:LatLng, topOfStackPoint: LatLng, currentPoint:LatLng) : Boolean {
        val intArray = arrayListOf<DoubleArray>(DoubleArray(3), DoubleArray(3), DoubleArray(3))

        intArray[0][0] = currentPoint.longitude;
        intArray[0][1] = currentPoint.latitude;
        intArray[0][2] = 1.0;

        intArray[1][0] = topOfStackPoint.longitude;
        intArray[1][1] = topOfStackPoint.latitude;
        intArray[1][2] = 1.0;

        intArray[2][0] = secondFromTopOfStackPoint.longitude;
        intArray[2][1] = secondFromTopOfStackPoint.latitude;
        intArray[2][2] = 1.0;

        //Counter clockwise turn > 0
        //Collinear turn = 0
        //Clockwise turn < 0
        return determinantOfMatrix(intArray) >= 0
    }

    private fun determinantOfMatrix(mat: ArrayList<DoubleArray>): Double {
        /*
           [ a b c ]         [ e f ]        [ d f ]        [ d e ]        [ currentPoint(long)              currentPoint(lat)              1 ]
    det =  [ d e f ]  = a*det[ h i ] - b*det[ g i ] - c*det[ g h ]  = det [ topOfStackPoint(long)           topOfStackPoint(lat)           1 ]
           [ g h i ]                                                      [ secondFromTopOfStackPoint(long) secondFromTopOfStackPoint(lat) 1 ]
         */
        val a = mat[0][0]
        val b = mat[0][1]
        val c = mat[0][2]
        val d = mat[1][0]
        val e = mat[1][1]
        val f = mat[1][2]
        val g = mat[2][0]
        val h = mat[2][1]
        val i = mat[2][2]
        val value = a * (e * i - f * h) - b * (d * i - g * f) + c * (d * h - e * g)
        return value
    }

}