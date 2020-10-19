package com.doaha.model.generator.model

import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray

class LetterB (centerPoint: LatLng, offset: Int) : Character(centerPoint, offset) {
    override fun generateCharacterJSONArrayObject(): JSONArray {
        //1
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong))
        //2
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong + longLine))
        //3
        jsonArrayChild.put(
            funCreateLatLngPoint(
                startPointLat + 1.5 * longLine,
                startPointLong + longLine
            )
        )
        //4
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat + 1.5 * longLine, startPointLong))
        //5
        jsonArrayChild.put(funCreateLatLngPoint(startPointLat, startPointLong))
        jsonArrayParent.put(jsonArrayChild)

        //1
        jsonArrayChild2.put(
            funCreateLatLngPoint(
                startPointLat + longLine / 3,
                startPointLong + longLine / 3
            )
        )
        //2
        jsonArrayChild2.put(
            funCreateLatLngPoint(
                startPointLat + longLine / 3,
                startPointLong + 2 * longLine / 3
            )
        )
        //3
        jsonArrayChild2.put(
            funCreateLatLngPoint(
                startPointLat + longLine,
                startPointLong + 2 * longLine / 3
            )
        )
        //4
        jsonArrayChild2.put(
            funCreateLatLngPoint(
                startPointLat + longLine,
                startPointLong + longLine / 3
            )
        )
        //5
        jsonArrayChild2.put(
            funCreateLatLngPoint(
                startPointLat + longLine / 3,
                startPointLong + longLine / 3
            )
        )
        jsonArrayParent.put(jsonArrayChild2)

        //1
        jsonArrayChild3.put(funCreateLatLngPoint(startPointLat + 1.5 * longLine, startPointLong))
        //2
        jsonArrayChild3.put(
            funCreateLatLngPoint(
                startPointLat + 1.5 * longLine,
                startPointLong + 2 * longLine / 3
            )
        )
        //3
        jsonArrayChild3.put(
            funCreateLatLngPoint(
                startPointLat + 2 * longLine,
                startPointLong + 2 * longLine / 3
            )
        )
        //4
        jsonArrayChild3.put(funCreateLatLngPoint(startPointLat + 2 * longLine, startPointLong))
        //5
        jsonArrayChild3.put(funCreateLatLngPoint(startPointLat + 1.5 * longLine, startPointLong))
        jsonArrayParent.put(jsonArrayChild3)

        //1
        jsonArrayChild4.put(
            funCreateLatLngPoint(
                startPointLat + 10 * longLine / 6,
                startPointLong + 1 * longLine / 6
            )
        )
        //2
        jsonArrayChild4.put(
            funCreateLatLngPoint(
                startPointLat + 10 * longLine / 6,
                startPointLong + 3 * longLine / 6
            )
        )
        //3
        jsonArrayChild4.put(
            funCreateLatLngPoint(
                startPointLat + 11 * longLine / 6,
                startPointLong + 3 * longLine / 6
            )
        )
        //4
        jsonArrayChild4.put(
            funCreateLatLngPoint(
                startPointLat + 11 * longLine / 6,
                startPointLong + 1 * longLine / 6
            )
        )
        //5
        jsonArrayChild4.put(
            funCreateLatLngPoint(
                startPointLat + 10 * longLine / 6,
                startPointLong + 1 * longLine / 6
            )
        )
        jsonArrayParent.put(jsonArrayChild4)
        return jsonArrayParent
    }
}