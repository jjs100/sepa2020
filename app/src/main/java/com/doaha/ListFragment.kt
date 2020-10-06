package com.doaha

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_main.*

class ListFragment : Fragment() {

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
            divider.setDrawable(ContextCompat.getDrawable(context, R.drawable.divider)!!)
            list_recycler_view.addItemDecoration(divider)
            layoutManager = LinearLayoutManager(activity)
            //get name from maps activity
            val name = MapsActivity.nation.name
            val nationData = getDocuments(name)
            nation_name.text = name
            //Gets data for list adaptor
            adapter = ListAdapter(nationData)
            this.setHasFixedSize(true)
        }
    }

    companion object {
        fun newInstance(): ListFragment = ListFragment()
    }

    //Pulls data from firebase and inserts it into mutablelist
    private fun getDocuments(nation : String) : MutableList<Nation> {
        val tempOut = mutableListOf<Nation>()
        val db = FirebaseFirestore.getInstance()
        db.collection("zones").document(nation)
            //gets specific region from database
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                    //Puts data from database into NationData list and name section
                    tempOut.add(Nation("Welcome", document.getString("Welcome")))
                    tempOut.add(Nation("Acknowledgements", document.getString("Acknowledgements")))
                    tempOut.add(Nation("Information", document.getString("Info")))
                    tempOut.add(Nation("Images", document.getString("img1")))
                    //Refreshes recycler view
                    list_recycler_view.adapter?.notifyDataSetChanged()
                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }
        return tempOut
    }
}
