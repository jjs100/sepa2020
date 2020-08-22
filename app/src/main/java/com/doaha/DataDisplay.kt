package com.doaha

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.doaha.SplashScreen.Companion.EXTRA_MESSAGE
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.data_display.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


class DataDisplay : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.data_display)
        val zoneId = intent.getStringExtra(EXTRA_MESSAGE)
        getInfo(zoneId)
    }

    private fun getInfo(zoneId: String) {
        val docRef = FirebaseFirestore.getInstance().collection("zones").document(zoneId)

        GlobalScope.launch(Dispatchers.Main) {
            delay(1000L)
            val region = docRef.get().await()
            Picasso.get().load(region.getString("img1")).into(image1)
            Picasso.get().load(region.getString("img2")).into(image2)
            Picasso.get().load(region.getString("img3")).into(image3)
            welcomeText.text = region.getString("Welcome").toString()
            ackText.text = "Acknowledgments: " + region.getString("Acknowledgements")
            infoText.text = region.getString("Info").toString()
        }
    }
}
