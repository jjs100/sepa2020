package com.doaha

import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class DataLoad(locationName: String) {

    private var welcome = "test"
    private val locName = locationName
    private val nationList = getData()

    fun getData() = runBlocking {
        val docRef = FirebaseFirestore.getInstance().collection("zones").document(locName)
        var natList = listOf<Dummy>()

        val job = GlobalScope.launch(Dispatchers.Main) {
            delay(1000L)
            val region = docRef.get().await()
            welcome = region.getString("Welcome").toString()
            natList = listOf(Dummy("Welcome", welcome))
        }
        //job.join()
    }
    fun returnData() {
        return nationList
    }
}