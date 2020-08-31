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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

//dummy data class for proof of progress
//replace this with correct dataclass for information from database
data class Dummy(val title: String, val info: String)

class ListFragment : Fragment() {

    //implementation of dummy data class for now, will be replaced with data from database
    private var nationData: MutableList<Dummy> = mutableListOf<Dummy>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        nationData.add(Dummy("Title", "This Better Work"))
        GlobalScope.async {
            val docRef = FirebaseFirestore.getInstance().collection("zones").document("Baraba Baraba")
            //docRef.get().await()
            var welcomeTest = docRef.get().await().getString("Welcome")
            var ackTest = docRef.get().await().getString("Acknowledgements")
            var infoTest = docRef.get().await().getString("Info")
            nationData.add(Dummy("Welcome", "$welcomeTest"))
            nationData.add(Dummy("Acknowledgements", "$ackTest"))
            nationData.add(Dummy("Information", "$infoTest"))
        }
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
}
