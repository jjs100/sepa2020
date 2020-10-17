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
            geoJsonObject.put("features", createGeoJSONLayerForEachNation(layer, geoJsonObject))
            return geoJsonObject
        }

        private fun createGeoJSONLayerForEachNation(
            layer: KmlLayer,
            geoJsonObject: JSONObject
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
            if(" ".single() != letter) {
                jsonObject.put("type", "Polygon")
                jsonObject.put(
                    "coordinates",
                    createJSONCoordinatesObject(centerPoint, letter, letterPosition)
                )
            }
            return jsonObject
        }

        private fun createJSONCoordinatesObject(
            centerPoint: LatLng,
            letter: Char,
            letterPosition: Int
        ): JSONArray {
            //var jsonArray: JSONArray = JSONArray()
            //jsonArray.put(createJSONArrayOfLatLngForALetter(centerPoint, letter, letterPosition))
            //return jsonArray
            return createJSONArrayOfLatLngForALetter(centerPoint, letter, letterPosition)
        }

        private fun createJSONArrayOfLatLngForALetter(
            centerPoint: LatLng,
            letter: Char,
            letterPosition: Int
        ): JSONArray {
            var aCharacter: Character = when (letter) {
//                "a".single() -> {
//                    LetterA()
//                }
//                "A".single() -> {
//                    LetterA()
//                }

                "-".single() -> {
                    CharDash(centerPoint, letterPosition)
                }
//                " ".single() -> {
//                    CharSpace()
//                }
//                "'".single() -> {
//                    CharTalkingMark()
//                }
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