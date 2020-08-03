
package com.example.backendproto

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.backendproto.MainActivity.Companion.EXTRA_MESSAGE
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_display_zone_info.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


class DisplayZoneInfo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_zone_info)
        val zoneId = intent.getStringExtra(EXTRA_MESSAGE)
        getInfo(zoneId)
    }

    private fun getInfo(zoneId: String) {
        val docRef = FirebaseFirestore.getInstance().collection("zones").document(zoneId)

        GlobalScope.launch(Dispatchers.Main){
            delay(1000L)
            val region = docRef.get().await()
            Picasso.get().load(region.getString("img1")).into(stateImage)
            Picasso.get().load(region.getString("img2")).into(stateImage2)
            Picasso.get().load(region.getString("img3")).into(stateImage3)
            welcome_data.text = region.getString("Welcome").toString()
            ack_data.text = "Acknowledgments: " + region.getString("Acknowledgements")
            info_data.text = region.getString("Info").toString()
        }
    }




              /*.addOnSuccessListener { document ->
                  if (document != null) {
                      Log.d("exist", "documentsnapshot data: ${document.data}")



                  } else {
                      Log.d("no exist", "no such document")
                  }
              }
              .addOnFailureListener() { exception ->
                  Log.d("errordb", "get failed with ", exception)
              }


      }*/


}



