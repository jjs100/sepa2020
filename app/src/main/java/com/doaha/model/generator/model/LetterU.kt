package com.doaha.model.generator.model

import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray

class LetterU (centerPoint: LatLng, offset: Int) : Character(centerPoint, offset) {
    override fun generateCharacterJSONArrayObject(): JSONArray {
        //1
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong))
        //2
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong + longLine))
        //3
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + longLine + 2 * shortLine,
                startPointLong + longLine
            )
        )
        //4
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + longLine + 2 * shortLine,
                startPointLong + 2 * longLine / 3
            )
        )
        //5
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + (longLine + 2 * shortLine) / 5,
                startPointLong + 2 * longLine / 3
            )
        )
        //6
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + (longLine + 2 * shortLine) / 5,
                startPointLong + 1 * longLine / 3
            )
        )
        //7
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + longLine + 2 * shortLine,
                startPointLong + 1 * longLine / 3
            )
        )
        //8
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + longLine + 2 * shortLine,
                startPointLong
            )
        )
        //9
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong))
        jsonArrayParent.put(jsonArrayChild)
        return jsonArrayParent
    }
}