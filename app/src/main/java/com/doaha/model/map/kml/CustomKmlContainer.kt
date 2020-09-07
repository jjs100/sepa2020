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
        this.aSuperRegion = extractAllPointsFromAKmlContainer(aKmlContainer)
    }

    private fun extractAllPointsFromAKmlContainer(aKmlContainer: KmlContainer): MutableList<LatLng>{
        var listOfLatLngPoints: MutableList<LatLng> = ArrayList()
        for (aMapObject in aKmlContainer.placemarks) {
            listOfLatLngPoints.addAll(extractAllPointFromAnObject(aMapObject))
        }
        return listOfLatLngPoints
    }
    private fun extractAllPointFromAnObject(aMapObject: KmlPlacemark):MutableList<LatLng>{
        return if(aMapObject is KmlPolygon){
            aMapObject.outerBoundaryCoordinates
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
            }else if(P0!!.latitude > it.latitude && P0!!.longitude > it.longitude){
                //Check if point in lowest y and x value
                P0 = it
            }else{
                println("The point: " + it.latitude + ", " + it.longitude)
            }
        }

        //Sort by the polar angle between two points
        variableListOfLatLngPoints.sortBy {
            kotlin.math.atan2((it.latitude - P0!!.latitude), (it.longitude - P0!!.longitude))
        }

        for(LanLngPoint in listOfLatLngPoints){
            //Pop the last point from stack if we turn clockwise to reach the next point
            while(stack.size > 1 && checkForCouterClockWiseTurn(stack[stack.size-1], stack[stack.size], LanLngPoint)){
                stack.pop()
            }
            //Add point to stack
            stack.add(LanLngPoint)
        }

        return listOfLatLngPoints
    }

    fun checkForCouterClockWiseTurn(secondFromTopOfStackPoint:LatLng, topOfStackPoint: LatLng, currentPoint:LatLng) : Boolean{
        //counter clockwise turn > 0
        //collinar turn = 0
        /*
           [ a b c ]         [ e f ]        [ d f ]        [ d e ]        [ currentPoint(long)              currentPoint(lat)              1 ]
    det =  [ d e f ]  = a*det[ h i ] - b*det[ g i ] - c*det[ g h ]  = det [ topOfStackPoint(long)           topOfStackPoint(lat)           1 ]
           [ g h i ]                                                      [ secondFromTopOfStackPoint(long) secondFromTopOfStackPoint(lat) 1 ]
         */
        val intArray = arrayListOf<DoubleArray>()

        intArray[0][0] = currentPoint.longitude;
        intArray[0][1] = currentPoint.latitude;
        intArray[0][2] = 1.0;

        intArray[1][0] = topOfStackPoint.longitude;
        intArray[1][1] = topOfStackPoint.latitude;
        intArray[1][2] = 1.0;

        intArray[2][0] = currentPoint.longitude;
        intArray[2][1] = currentPoint.longitude;
        intArray[2][2] = 1.0;

       // val det = determinantOfMatrix(intArray, 3)

        return true
        //https://www.geeksforgeeks.org/determinant-of-a-matrix/
        //https://www.chilimath.com/lessons/advanced-algebra/determinant-3x3-matrix/
        //https://math.stackexchange.com/questions/1324179/how-to-tell-if-3-connected-points-are-connected-clockwise-or-counter-clockwise
        //https://stackoverflow.com/questions/42258637/how-to-know-the-angle-between-two-points
        //https://www.google.com/search?q=lat+long+map&rlz=1C1CHBF_en-GBAU892AU892&sxsrf=ALeKk032GWiKKgWOi2muKI59ISrvOU5nog:1598886880139&source=lnms&tbm=isch&sa=X&ved=2ahUKEwj_gpvk3cXrAhXP7XMBHZ2VCwkQ_AUoAXoECGYQAw&biw=1455&bih=688#imgrc=NZ-IFOAr1N6IfM
        //https://en.wikipedia.org/wiki/Graham_scan#Pseudocode
        //https://www.geeksforgeeks.org/convex-hull-set-1-jarviss-algorithm-or-wrapping/
        //https://www.geeksforgeeks.org/convex-hull-set-1-jarviss-algorithm-or-wrapping/
    }

    fun determinantOfMatrix(mat: ArrayList<DoubleArray>): Int {
        return 1
    }

}