package com.doaha

import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore

class DataLoad(locationName: String) {

    private var welcome = "test"
    private var check = true
    private var nationList = listOf<Dummy>()
    private val locName = locationName

    fun onCreate(savedInstanceState: Bundle?) {
        val docRef = FirebaseFirestore.getInstance().collection("zones").document(locName)

        docRef.get()
            .addOnSuccessListener { document ->
                welcome = "WELCOME: " + document.getString("Welcome").toString()
                nationList = listOf(Dummy("Welcome", welcome))
                check = true
            }
    }
    fun getData() : List<Dummy> {
        while (!check) {
            Thread.sleep(1000)
        }
        return nationList
    }
}