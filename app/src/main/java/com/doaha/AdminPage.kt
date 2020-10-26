package com.doaha

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.doaha.application.DoAHAApplication
import com.doaha.model.enum.MapSource
import com.doaha.model.enum.MapStyle


class AdminPage : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_page)

        //Create Notification Button Switch
        loadButtonConfig(findViewById(R.id.notifications_toggle))

        //Create KML Toggle Button Switch
        loadXMLButtonConfig(findViewById(R.id.xmlLiveImportToggle))

        //Create Radio Group
        loadRadioGroupConfig(findViewById(R.id.mapStyleSelection))

        //Create Nation Label Button Switch
        loadNationLabelButtonConfig(findViewById(R.id.nationLabelToggle))

        //Creating variable for floating button to add new documents
        val buttonRecycler = findViewById<Button>(R.id.docButton)
        buttonRecycler.setOnClickListener {
            startActivity(Intent(this, AdminRecycler::class.java))
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        startActivity(Intent(this, SplashScreen::class.java))
    }

    private fun loadNationLabelButtonConfig(nationLabelButton: Switch) {
        nationLabelButton.isChecked =
            (this.application as DoAHAApplication).getIsNationLabelEnabled(getSharedPreferences())

        nationLabelButton.setOnClickListener {
            (this.application as DoAHAApplication).setIsNationLabelEnabled(
                getSharedPreferences(),
                !(this.application as DoAHAApplication).getIsNationLabelEnabled(getSharedPreferences())
            )
        }
    }

    private fun loadRadioGroupConfig(radioGroup: RadioGroup) {
        when ((this.application as DoAHAApplication).getMapStyle(getSharedPreferences())) {
            MapStyle.STANDARD -> {
                findViewById<RadioButton>(R.id.standard).isChecked = true
            }
            MapStyle.SILVER -> {
                findViewById<RadioButton>(R.id.silver).isChecked = true
            }
            MapStyle.RETRO -> {
                findViewById<RadioButton>(R.id.retro).isChecked = true
            }
        }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val checkedButton: RadioButton = findViewById(checkedId)
            val checked = checkedButton.isChecked
            var mapStyle: MapStyle = MapStyle.STANDARD

            when (checkedButton.id) {
                R.id.retro -> if (checked) {
                    mapStyle = MapStyle.RETRO
                }
                R.id.silver -> if (checked) {
                    mapStyle = MapStyle.SILVER
                }
                else -> mapStyle = MapStyle.STANDARD
            }
            (this.application as DoAHAApplication).setMapStyle(getSharedPreferences(), mapStyle)
        }
    }

    private fun loadButtonConfig(@SuppressLint("UseSwitchCompatOrMaterialCode") notificationButton: Switch) {
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

    private fun loadXMLButtonConfig(@SuppressLint("UseSwitchCompatOrMaterialCode") notificationButton: Switch) {
        //Load Application variable value
        val mapSource: MapSource =
            (this.application as DoAHAApplication).getXmlImportType(
                getSharedPreferences()
            )

        //Set in button value
        notificationButton.isChecked = booleanConversionForXmlLiveImportSwitch(mapSource)


        //Notification Button default to true and inverse current value if not set
        notificationButton.setOnClickListener {
            (this.application as DoAHAApplication).setXmlImportType(
                getSharedPreferences(),
                switchValue(
                    (this.application as DoAHAApplication).getXmlImportType(
                        getSharedPreferences()
                    )
                )
            )
        }
    }

    private fun booleanConversionForXmlLiveImportSwitch(mapSource: MapSource): Boolean {
        return mapSource != MapSource.LOCAL
    }

    private fun switchValue(value: MapSource): MapSource {
        return if (value == MapSource.LOCAL) {
            MapSource.ONLINE
        } else {
            MapSource.LOCAL
        }
    }

    private fun getSharedPreferences(): SharedPreferences {
        return applicationContext.getSharedPreferences(
            getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
    }
}