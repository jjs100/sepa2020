package com.doaha.model.generator.model

import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray

class LetterV (centerPoint: LatLng, offset: Int) : Character(centerPoint, offset) {
    override fun generateCharacterJSONArrayObject(): JSONArray {
        //1
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong+longLine/2))
        //2
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat+2*longLine, startPointLong+longLine))
        //3
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat+2*longLine, startPointLong+2*longLine/3))
        //4
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat+longLine, startPointLong+longLine/2))
        //5
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat+2*longLine, startPointLong+longLine/3))
        //6
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat+2*longLine, startPointLong))
        //7
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong+longLine/2))
        jsonArrayParent.put(jsonArrayChild)
        return jsonArrayParent
    }
}