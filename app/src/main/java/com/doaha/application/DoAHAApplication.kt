package com.doaha.application

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.doaha.R
import com.doaha.model.enum.MapSource
import com.doaha.model.enum.MapStyle

class DoAHAApplication : Application() {
    private var isNotificationEnabled: Boolean = false
    private var theRegionUserWasPreviouslyIn: String = ""
    private var xmlImportType: MapSource = MapSource.LOCAL
    private var mapStyle: MapStyle? = null

    fun getIsNotificationEnabled(sharedPref: SharedPreferences): Boolean {
        //Get saved value, and set to isNotificationEnabled if not found
        var extractedValue: String? = getStringValue(sharedPref, R.string.notification_key)
        if (extractedValue == null) {
            setIsNotificationEnabled(sharedPref, isNotificationEnabled)
            extractedValue = isNotificationEnabled.toString()
        }
        this.isNotificationEnabled = extractedValue.toBoolean()
        return isNotificationEnabled
    }

    fun setIsNotificationEnabled(sharedPref: SharedPreferences, value: Boolean) {
        saveStringValue(sharedPref, value.toString(), R.string.notification_key)
        this.isNotificationEnabled = value
    }

    fun getTheRegionUserWasPreviouslyIn(sharedPref: SharedPreferences): String {
        return getStringValue(sharedPref, R.string.theRegionUserWasPreviouslyIn_key)
            ?: theRegionUserWasPreviouslyIn
    }

    fun setTheRegionUserWasPreviouslyIn(sharedPref: SharedPreferences, value: String) {
        saveStringValue(sharedPref, value, R.string.theRegionUserWasPreviouslyIn_key)
        this.theRegionUserWasPreviouslyIn = value
    }

    fun getXmlImportType(sharedPref: SharedPreferences): MapSource {
        val value: String? = getStringValue(sharedPref, R.string.XmlImportType)

        return when (getStringValue(sharedPref, R.string.XmlImportType)) {
            MapSource.LOCAL.toString() -> {
                MapSource.LOCAL
            }
            MapSource.ONLINE.toString() -> {
                MapSource.ONLINE
            }
            else -> {
                setXmlImportType(sharedPref, xmlImportType)
                xmlImportType
            }
        }
    }

    fun setXmlImportType(sharedPref: SharedPreferences, value: MapSource) {
        saveStringValue(sharedPref, value.toString(), R.string.XmlImportType)
        this.xmlImportType = value
    }

    fun getMapStyle(sharedPref: SharedPreferences): MapStyle {
        when (getStringValue(sharedPref, R.string.mapStyle)) {
            MapStyle.STANDARD.toString() -> {
                return MapStyle.STANDARD
            }
            MapStyle.SILVER.toString() -> {
                return MapStyle.SILVER
            }
            MapStyle.RETRO.toString() -> {
                return MapStyle.RETRO
            }
            else -> {
                setMapStyle(sharedPref, MapStyle.STANDARD)
                this.mapStyle = MapStyle.STANDARD
                return MapStyle.STANDARD
            }
        }
    }

    fun setMapStyle(sharedPref: SharedPreferences, value: MapStyle) {
        saveStringValue(sharedPref, value.toString(), R.string.mapStyle)
        this.mapStyle = value
    }

    private fun getStringValue(sharedPref: SharedPreferences, key: Int): String? {
        return sharedPref.getString(getString(key), null)
    }

    private fun saveStringValue(sharedPref: SharedPreferences, value: String, key: Int) {
        with(sharedPref.edit()) {
            putString(getString(key), value)
            commit()
        }
    }

    fun checkStatefulToolTip(sharedPref : SharedPreferences) : Boolean{
        //get string value
        val checkString = getStringValue(sharedPref, R.string.toolTip_used)
        //if string not used
        return if (checkString.equals("unused")){
            //set string, return true
            saveStringValue(sharedPref, "used", R.string.toolTip_used)
            true
        } else{
            false
        }
    }
}