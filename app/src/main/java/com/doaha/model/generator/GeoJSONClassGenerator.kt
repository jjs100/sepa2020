package com.doaha.model.generator

import com.doaha.model.generator.model.*
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.data.kml.KmlLayer
import com.google.maps.android.data.kml.KmlPlacemark
import com.google.maps.android.data.kml.KmlPolygon
import org.json.JSONArray
import org.json.JSONObject

class GeoJSONClassGenerator {
    companion object {
        //Static object creation method
        fun create(layer: KmlLayer): JSONObject {
            var geoJsonObject: JSONObject = JSONObject()
            geoJsonObject.put("type", "FeatureCollection")
            geoJsonObject.put("features", createGeoJSONLayerForEachNation(layer))
            return geoJsonObject
        }

        private fun createGeoJSONLayerForEachNation(
            layer: KmlLayer
        ): JSONArray {
            var jsonArray: JSONArray = JSONArray()
            for (aKmlContainerList in layer.containers) {
                for (aKmlContainerList2 in aKmlContainerList.containers) {
                    for (aKmlPlaceMarker in aKmlContainerList2.placemarks) {
                        if (aKmlPlaceMarker.geometry is KmlPolygon) {
                            createJSONForWordObjects(jsonArray, aKmlPlaceMarker)
                        }
                    }
                }
            }
            return jsonArray
        }

        private fun createJSONForWordObjects(
            jsonArray: JSONArray,
            aKmlPlaceMarker: KmlPlacemark
        ) {
            val nationName: String = getNationName(aKmlPlaceMarker)
            for (letterPosition in nationName.indices) {
                var jsonObject: JSONObject = JSONObject()
                jsonObject.put("type", "Feature")
                jsonObject.put(
                    "properties",
                    createJSONPropertiesObject(nationName, nationName[letterPosition])
                )
                jsonObject.put(
                    "geometry",
                    createJSONGeometryObject(
                        nationName,
                        nationName[letterPosition],
                        letterPosition,
                        getCentroidOfPolygon(aKmlPlaceMarker.geometry as KmlPolygon)
                    )
                )
                jsonArray.put(jsonObject)
            }
        }

        private fun createJSONGeometryObject(
            nationName: String,
            letter: Char,
            letterPosition: Int,
            centerPoint: LatLng
        ): JSONObject {
            var jsonObject: JSONObject = JSONObject()
            if (" ".single() != letter) {
                if (nationName == "Bidwell") {
                    jsonObject.put("type", "Polygon")
                    jsonObject.put(
                        "coordinates",
                        createJSONCoordinatesObject(centerPoint, letter, letterPosition)
                    )
                }
            }
            return jsonObject
        }

        private fun createJSONCoordinatesObject(
            centerPoint: LatLng,
            letter: Char,
            letterPosition: Int
        ): JSONArray {
            return createJSONArrayOfLatLngForALetter(centerPoint, letter, letterPosition)
        }

        private fun createJSONArrayOfLatLngForALetter(
            centerPoint: LatLng,
            letter: Char,
            letterPosition: Int
        ): JSONArray {
            val aCharacter: Character = when (letter) {
                "-".single() -> {
                    CharDash(centerPoint, letterPosition)
                }
                "a".single() -> {
                    LetterA(centerPoint, letterPosition)
                }
                "A".single() -> {
                    LetterA(centerPoint, letterPosition)
                }
                "b".single() -> {
                    LetterB(centerPoint, letterPosition)
                }
                "B".single() -> {
                    LetterB(centerPoint, letterPosition)
                }
                "d".single() -> {
                    LetterD(centerPoint, letterPosition)
                }
                "D".single() -> {
                    LetterD(centerPoint, letterPosition)
                }
                "e".single() -> {
                    LetterE(centerPoint, letterPosition)
                }
                "E".single() -> {
                    LetterE(centerPoint, letterPosition)
                }
                "g".single() -> {
                    LetterG(centerPoint, letterPosition)
                }
                "G".single() -> {
                    LetterG(centerPoint, letterPosition)
                }
                "i".single() -> {
                    LetterI(centerPoint, letterPosition)
                }
                "I".single() -> {
                    LetterI(centerPoint, letterPosition)
                }
                "k".single() -> {
                    LetterK(centerPoint, letterPosition)
                }
                "K".single() -> {
                    LetterK(centerPoint, letterPosition)
                }
                "l".single() -> {
                    LetterL(centerPoint, letterPosition)
                }
                "L".single() -> {
                    LetterL(centerPoint, letterPosition)
                }
                "m".single() -> {
                    LetterM(centerPoint, letterPosition)
                }
                "M".single() -> {
                    LetterM(centerPoint, letterPosition)
                }
                "n".single() -> {
                    LetterN(centerPoint, letterPosition)
                }
                "N".single() -> {
                    LetterN(centerPoint, letterPosition)
                }
                "o".single() -> {
                    LetterO(centerPoint, letterPosition)
                }
                "O".single() -> {
                    LetterO(centerPoint, letterPosition)
                }
                "p".single() -> {
                    LetterP(centerPoint, letterPosition)
                }
                "P".single() -> {
                    LetterP(centerPoint, letterPosition)
                }
                "r".single() -> {
                    LetterR(centerPoint, letterPosition)
                }
                "R".single() -> {
                    LetterR(centerPoint, letterPosition)
                }
                "t".single() -> {
                    LetterT(centerPoint, letterPosition)
                }
                "T".single() -> {
                    LetterT(centerPoint, letterPosition)
                }
                "u".single() -> {
                    LetterU(centerPoint, letterPosition)
                }
                "U".single() -> {
                    LetterU(centerPoint, letterPosition)
                }
                "w".single() -> {
                    LetterW(centerPoint, letterPosition)
                }
                "W".single() -> {
                    LetterW(centerPoint, letterPosition)
                }
                "y".single() -> {
                    LetterY(centerPoint, letterPosition)
                }
                "Y".single() -> {
                    LetterY(centerPoint, letterPosition)
                }
                else -> {
                    Unknown(centerPoint, letterPosition)
                }
            }

            return aCharacter.generateCharacterJSONArrayObject()
        }

        private fun createJSONPropertiesObject(nationName: String, letter: Char): JSONObject {
            var jsonObject: JSONObject = JSONObject()
            jsonObject.put("NationName", nationName)
            jsonObject.put("letter", letter)
            jsonObject.put("color", "blue")
            jsonObject.put("rank", "7")
            jsonObject.put("ascii", "71")
            return jsonObject
        }

        private fun getCentroidOfPolygon(kmlPolygon: KmlPolygon): LatLng {
            //Set base point for first iteration
            var xMax: Double = kmlPolygon.outerBoundaryCoordinates[0].latitude
            var xMin: Double = kmlPolygon.outerBoundaryCoordinates[0].latitude
            var yMax: Double = kmlPolygon.outerBoundaryCoordinates[0].longitude
            var yMin: Double = kmlPolygon.outerBoundaryCoordinates[0].longitude

            //Iterate through list of points
            for (aLatLngPoint in kmlPolygon.outerBoundaryCoordinates) {
                //Check if latitude is greater or less than current xMax and xMin points
                if (xMax < aLatLngPoint.latitude) {
                    xMax = aLatLngPoint.latitude
                } else if (xMin > aLatLngPoint.latitude) {
                    xMin = aLatLngPoint.latitude
                }

                //Check if latitude is greater or less than current xMax and xMin points
                if (yMax < aLatLngPoint.longitude) {
                    yMax = aLatLngPoint.longitude
                } else if (yMin > aLatLngPoint.longitude) {
                    yMin = aLatLngPoint.longitude
                }
            }

            return LatLng((xMax + xMin) / 2, (yMax + yMin) / 2)
        }

        private fun getNationName(kmlPlacemark: KmlPlacemark): String {
            return kmlPlacemark.getProperty("name")
        }
    }
}