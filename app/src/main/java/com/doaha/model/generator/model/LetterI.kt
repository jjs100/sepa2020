package com.doaha.model.generator.model

import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray

class LetterI (centerPoint: LatLng, offset: Int) : Character(centerPoint, offset) {
    override fun generateCharacterJSONArrayObject(): JSONArray {
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong))

        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong + longLine))
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + shortLine,
                startPointLong + longLine
            )
        )
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + shortLine,
                startPointLong + longLine - shortLine / 2
            )
        )
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + shortLine + longLine,
                startPointLong + longLine - shortLine / 2
            )
        )
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + shortLine + longLine,
                startPointLong + longLine
            )
        )
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + shortLine + longLine + shortLine,
                startPointLong + longLine
            )
        )
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + shortLine + longLine + shortLine,
                startPointLong
            )
        )
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + shortLine + longLine,
                startPointLong
            )
        )
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + shortLine + longLine,
                startPointLong + shortLine / 2
            )
        )
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + shortLine,
                startPointLong + shortLine / 2
            )
        )
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat + shortLine, startPointLong))
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong))
        jsonArrayParent.put(jsonArrayChild)
        return jsonArrayParent
    }
}