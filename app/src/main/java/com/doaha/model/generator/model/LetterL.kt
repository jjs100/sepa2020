package com.doaha.model.generator.model

import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray

class LetterL (centerPoint: LatLng, offset: Int) : Character(centerPoint, offset) {
    override fun generateCharacterJSONArrayObject(): JSONArray {
        //1
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong))
        //2
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong+longLine))
        //3
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat+2*longLine/3, startPointLong+longLine))
        //4
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat+2*longLine/3, startPointLong+longLine/2))
        //5
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat+2*longLine, startPointLong+longLine/2))
        //6
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat+2*longLine, startPointLong))
        //7
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong))
        jsonArrayParent.put(jsonArrayChild)
        return jsonArrayParent
    }
}