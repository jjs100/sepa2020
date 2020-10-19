package com.doaha.model.generator.model

import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray

class CharTalkingMark(centerPoint: LatLng, offset: Int) : Character(centerPoint, offset) {
    override fun generateCharacterJSONArrayObject(): JSONArray {
        //1
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat+4*longLine/3, startPointLong+longLine/3))
        //2
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat+4*longLine/3, startPointLong+2*longLine/3))
        //3
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat+2*longLine, startPointLong+2*longLine/3))
        //4
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat+2*longLine, startPointLong+longLine/3))
        //5
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat+4*longLine/3, startPointLong+longLine/3))
        jsonArrayParent.put(jsonArrayChild)
        return jsonArrayParent
    }
}