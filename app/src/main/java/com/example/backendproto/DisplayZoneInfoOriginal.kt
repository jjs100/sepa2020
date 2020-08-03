/*

package com.example.backendproto

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.backendproto.MainActivity.Companion.EXTRA_MESSAGE
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_display_zone_info.*


class DisplayZoneInfoOriginal : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_zone_info)
        val zoneId = intent.getStringExtra(EXTRA_MESSAGE)
        getInfo(zoneId)
    }

    private fun getInfo(zoneId: String) {
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("zones").document(zoneId)

        docRef.get()
              .addOnSuccessListener { document ->
                  if (document != null) {
                      Log.d("exist", "documentsnapshot data: ${document.data}")
                      Picasso.get().load(document.getString("img1")).into(stateImage)
                      Picasso.get().load(document.getString("img2")).into(stateImage2)
                      Picasso.get().load(document.getString("img3")).into(stateImage3)
                      welcome_data.text = document.getString("Welcome").toString()
                      ack_data.text = "Acknowledgments: " + document.getString("Acknowledgements")
                      info_data.text = document.getString("Info").toString()


                  } else {
                      Log.d("no exist", "no such document")
                  }
              }
              .addOnFailureListener() { exception ->
                  Log.d("errordb", "get failed with ", exception)
              }


      }


}



*/
