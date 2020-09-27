package com.doaha.application

import android.app.Application
import android.content.SharedPreferences
import com.doaha.R

class DoAHAApplication : Application() {
    private var isNotificationEnabled: Boolean = false
    private var theRegionUserWasPreviouslyIn: String = ""

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

    private fun getStringValue(sharedPref: SharedPreferences, key: Int): String? {
        return sharedPref.getString(getString(key), null)
    }

    private fun saveStringValue(sharedPref: SharedPreferences, value: String, key: Int) {
        with(sharedPref.edit()) {
            putString(getString(key), value)
            commit()
        }
    }
}