package com.doaha.application

import android.app.Application
import android.content.SharedPreferences
import com.doaha.R

class DoAHAApplication : Application() {
    private var isNotificationEnabled: Boolean = false

    fun getIsNotificationEnabled(sharedPref : SharedPreferences): Boolean {
        //Get saved value, and set to isNotificationEnabled if not found
        var extractedValue: String? = sharedPref.getString(getString(R.string.notification_key), null)
        if(extractedValue == null){
            setIsNotificationEnabled(sharedPref, isNotificationEnabled)
            extractedValue = isNotificationEnabled.toString()
        }
        this.isNotificationEnabled = extractedValue.toBoolean()
        return isNotificationEnabled
    }

    fun setIsNotificationEnabled(sharedPref : SharedPreferences, value: Boolean) {
        with(sharedPref.edit()){
            putString(getString(R.string.notification_key), value.toString())
            commit()
        }
        this.isNotificationEnabled = value
    }
}