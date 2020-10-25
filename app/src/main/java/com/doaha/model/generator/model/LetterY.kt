package com.doaha.model.generator.model

import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray

class LetterY (centerPoint: LatLng, offset: Int) : Character(centerPoint, offset) {
    override fun generateCharacterJSONArrayObject(): JSONArray {
        //1
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong + longLine / 4))
        //2
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong + 3 * longLine / 4))
        //3
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + longLine,
                startPointLong + 3 * longLine / 4
            )
        )
        //4
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + 2 * longLine,
                startPointLong + longLine
            )
        )
        //5
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + 2 * longLine,
                startPointLong + 3 * longLine / 4
            )
        )
        //6
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + longLine,
                startPointLong + 2 * longLine / 4
            )
        )
        //7
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + 2 * longLine,
                startPointLong + longLine / 4
            )
        )
        //8
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat + 2 * longLine, startPointLong))
        //9
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + longLine,
                startPointLong + longLine / 4
            )
        )
        //10
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong + longLine / 4))
        jsonArrayParent.put(jsonArrayChild)
        return jsonArrayParent
    }
}