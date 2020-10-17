package com.doaha.model.generator.model

import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray

abstract class Character(centerPoint: LatLng, offset: Int) {
    private val spacing: Double = 0.06
    val shortLine: Double = 0.02
    val longLine: Double = 0.04
    var jsonArrayParent: JSONArray = JSONArray()
    var jsonArrayChild: JSONArray = JSONArray()
    val startPointLat: Double = centerPoint.latitude
    val startPointLong: Double = centerPoint.longitude + offset * spacing


    abstract fun generateCharacterJSONArrayObject(): JSONArray

    fun funCreateLatLngPoint(latPoint: Double, longPoint: Double): JSONArray {
        var jsonArray: JSONArray = JSONArray()
        jsonArray.put(longPoint)
        jsonArray.put(latPoint)
        return jsonArray
    }
}