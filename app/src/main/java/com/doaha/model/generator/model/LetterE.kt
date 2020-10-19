package com.doaha.model.generator.model

import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray

class LetterE (centerPoint: LatLng, offset: Int) : Character(centerPoint, offset) {
    override fun generateCharacterJSONArrayObject(): JSONArray {
        //1
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong))
        //2
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong + longLine))
        //3
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + 2 * longLine / 5,
                startPointLong + longLine
            )
        )
        //4
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + 2 * longLine / 5,
                startPointLong + 2 * longLine / 3
            )
        )
        //5
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + 4 * longLine / 5,
                startPointLong + 2 * longLine / 3
            )
        )
        //6
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + 4 * longLine / 5,
                startPointLong + longLine
            )
        )
        //7
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + 6 * longLine / 5,
                startPointLong + longLine
            )
        )
        //8
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + 6 * longLine / 5,
                startPointLong + 2 * longLine / 3
            )
        )
        //9
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + 8 * longLine / 5,
                startPointLong + 2 * longLine / 3
            )
        )
        //10
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + 8 * longLine / 5,
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