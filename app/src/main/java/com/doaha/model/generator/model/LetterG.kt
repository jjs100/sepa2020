package com.doaha.model.generator.model

import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray

class LetterG (centerPoint: LatLng, offset: Int) : Character(centerPoint, offset) {
    override fun generateCharacterJSONArrayObject(): JSONArray {
        //1
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong))
        //2
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong + longLine))
        //3
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + longLine,
                startPointLong + longLine
            )
        )
        //4
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + longLine,
                startPointLong + 11 * longLine / 24
            )
        )
        //5
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + 4 * longLine / 6,
                startPointLong + 11 * longLine / 24
            )
        )
        //6
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + 4 * longLine / 6,
                startPointLong + 8 * longLine / 12
            )
        )
        //7
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + 2 * longLine / 6,
                startPointLong + 8 * longLine / 12
            )
        )
        //8
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + 2 * longLine / 6,
                startPointLong + 3 * longLine / 12
            )
        )
        //9
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + 10 * longLine / 6,
                startPointLong + 3 * longLine / 12
            )
        )
        //10
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + 10 * longLine / 6,
                startPointLong + longLine
            )
        )
        //11
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + 2 * longLine,
                startPointLong + longLine
            )
        )
        //12
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat + 2 * longLine, startPointLong))
        //13
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong))
        jsonArrayParent.put(jsonArrayChild)
        return jsonArrayParent
    }
}