package com.doaha

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.doaha.application.DoAHAApplication

class AdminPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_page)

        //Create Notification Button Switch
        loadButtonConfig(findViewById<Switch>(R.id.notifications_toggle))
    }

    private fun loadButtonConfig(notificationButton: Switch) {
        //Load Application variable value
        val isNotificationsEnabled: Boolean =
            (this.application as DoAHAApplication).getIsNotificationEnabled(
                getSharedPreferences()
            )

        //Set in button value
        notificationButton.isChecked = isNotificationsEnabled

        //Notification Button default to true and inverse current value if not set
        notificationButton.setOnClickListener {
            (this.application as DoAHAApplication).setIsNotificationEnabled(
                getSharedPreferences(),
                !(this.application as DoAHAApplication).getIsNotificationEnabled(
                    getSharedPreferences()
                )
            )
        }
    }

    private fun getSharedPreferences(): SharedPreferences {
        return applicationContext.getSharedPreferences(
            getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
    }
}