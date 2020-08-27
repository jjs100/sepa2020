package com.doaha

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_main.*
import com.doaha.ListAdapter

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
//import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

//dummy data class for proof of progress
//replace this with correct dataclass for information from database
data class Dummy(val title: String, val info: String)

class ListFragment : Fragment() {

    //implementation of dummy data class for now, will be replaced with data from database
    var welcome = "test"
    private var check = true
    private var nationData = callData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list_recycler_view.apply {
            val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            divider.setDrawable(ContextCompat.getDrawable(context, R.drawable.divier)!!)
            list_recycler_view.addItemDecoration(divider)
            layoutManager = LinearLayoutManager(activity)
            adapter = ListAdapter(nationData)
            this.setHasFixedSize(true)
        }
    }

    companion object {
        fun newInstance(): ListFragment = ListFragment()
    }

    private fun callData() : List<Dummy>{
        val docRef = FirebaseFirestore.getInstance().collection("zones").document("Bidwell")
        var nationList = listOf<Dummy>()
        //accessDatabase("Bidwell")

        docRef.get()
            .addOnSuccessListener { document ->
                welcome = "WELCOME: " + document.getString("Welcome").toString()
                /*if (document != null) {
                Log.d("exist", "documentsnapshot data: ${document.data}")
                welcome = document.getString("Welcome").toString()
                *//**//*ack_data.text = "Acknowledgments: " + document.getString("Acknowledgements")
                   info_data.text = document.getString("Info").toString()*//**//*

            } else {
                Log.d("no exist", "no such document")
                welcome = "test2"
            }*/
                check = false
            }

        /*nationList = listOf(Dummy("Welcome", "$welcome")*/
                /*Dummy("Acknowledgement", region.getString("Acknowledgements").toString()),
               Dummy("Information", region.getString("Info").toString())*/
        while(check)
        {
            Thread.sleep(1000)
        }
        return listOf(Dummy("Welcome", welcome))
    }

    private fun accessDatabase(zoneId: String) {
        val docRef = FirebaseFirestore.getInstance().collection("zones").document(zoneId)


        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                Log.d("exist", "documentsnapshot data: ${document.data}")
                welcome = document.getString("Welcome").toString()

                } else {
                    Log.d("no exist", "no such document")
                }
            }
    }


    /*private fun getInfo(zoneId: String) {
        val docRef = FirebaseFirestore.getInstance().collection("zones").document(zoneId)

        GlobalScope.launch(Dispatchers.Main) {
            delay(1000L)
            val region = docRef.get().await()

*/
            /*if (region.getString("img1") != "") {
                Picasso.get().load(region.getString("img1")).into(image1)
            }
            if (region.getString("img2") != "") {
                Picasso.get().load(region.getString("img2")).into(image2)
            }
            if (region.getString("img3") != "") {
                Picasso.get().load(region.getString("img3")).into(image3)
            }
        }
    }*/
}
