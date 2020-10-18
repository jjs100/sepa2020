package com.doaha.model.generator.model

import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray

class LetterS (centerPoint: LatLng, offset: Int) : Character(centerPoint, offset) {
    override fun generateCharacterJSONArrayObject(): JSONArray {
        //1
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong))
        //2
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong))
        //3
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong))
        //4
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong))
        //5
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong))
        //6
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong))
        //7
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong))
        //8
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong))
        //9
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong))
        //10
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong))
        //11
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong))
        //12
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong))
        jsonArrayParent.put(jsonArrayChild)
        return jsonArrayParent
    }
}