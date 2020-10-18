package com.doaha.model.generator.model

import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray

class CharDash(centerPoint: LatLng, offset: Int) : Character(centerPoint, offset) {

    override fun generateCharacterJSONArrayObject(): JSONArray {
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat +  2* longLine/3, startPointLong))
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat+  2* longLine/3, startPointLong + longLine))
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat +  4* longLine/3, startPointLong + longLine))
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat +  4* longLine/3, startPointLong))
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat+  2* longLine/3, startPointLong))
        jsonArrayParent.put(jsonArrayChild)
        return jsonArrayParent
    }
}