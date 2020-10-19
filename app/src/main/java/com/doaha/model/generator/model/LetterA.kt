package com.doaha.model.generator.model

import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray

class LetterA(centerPoint: LatLng, offset: Int) : Character(centerPoint, offset) {
    override fun generateCharacterJSONArrayObject(): JSONArray {
        //1
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong))
        //2
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong + longLine * 0.3))
        //3
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + 2 * longLine / 3,
                startPointLong + longLine * 0.4
            )
        )
        //4
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + 2 * longLine / 3,
                startPointLong + longLine * 0.6
            )
        )
        //5
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong + longLine * 0.7))
        //6
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong + longLine))
        //7
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + 2 * longLine,
                startPointLong + longLine * 0.5
            )
        )
        //8
        jsonArrayParent.put(jsonArrayChild)

        //1
        jsonArrayChild2.put(
            funCreateLatLngPoint(
                startPointLat + 5 * longLine / 6,
                startPointLong + longLine * 0.4
            )
        )
        //2
        jsonArrayChild2.put(
            funCreateLatLngPoint(
                startPointLat + 5 * longLine / 6,
                startPointLong + longLine * 0.6
            )
        )
        //3
        jsonArrayChild2.put(
            funCreateLatLngPoint(
                startPointLat + 8 * longLine / 6,
                startPointLong + longLine * 0.5
            )
        )
        //4
        jsonArrayChild2.put(
            funCreateLatLngPoint(
                startPointLat + 5 * longLine / 6,
                startPointLong + longLine * 0.4
            )
        )

        jsonArrayParent.put(jsonArrayChild2)
        return jsonArrayParent
    }
}