package com.doaha

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AdminSearchResult : AppCompatActivity(),DocAdapter.OnNoteItemClickListener {
    private val db = FirebaseFirestore.getInstance()
    private val notebookRef = db.collection("zones")
    private var adapter: DocAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_search)

        //creates back button to exit search
        val buttonBack : ImageView = findViewById(R.id.button_back)
        buttonBack.setOnClickListener{
            onBackPressed()
        }

        //RecyclerView setup
        //query to allow searched document to display
        val searchQuery : Query = notebookRef.whereEqualTo("itemID", intent.getStringExtra("Searchterm"))
        setUpRecyclerView(searchQuery)
    }

    private fun setUpRecyclerView(query: Query) {
        //assigns query so it can be used when built with the adapter
        val options = FirestoreRecyclerOptions.Builder<AdminDocs>()
                .setQuery(query, AdminDocs::class.java)
                .build()
        adapter = DocAdapter(options, this)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    override fun onItemClick(item: AdminDocs, position: Int, documentSnapshot : DocumentSnapshot) {
        val intent = Intent(this, EditDocActivity::class.java)

        //uses the intent for the EditDocActivity to attach the text displayed in the cardview so it can be viewed/edited in the next activity
        val docID : String? = documentSnapshot.getString("itemID")
        val docWelcome : String? = documentSnapshot.getString("Welcome")
        val docAck : String? = documentSnapshot.getString("Acknowledgements")
        val docInfo : String? = documentSnapshot.getString("Info")

        intent.putExtra("ID", docID)
        intent.putExtra("WELCOME", docWelcome)
        intent.putExtra("ACK", docAck)
        intent.putExtra("INFO", docInfo)

        //starts activity with intent and the documents information
        startActivity(intent)
    }
    override fun onStart() {
        //FirebaseUI recyclerview starts listening for changes
        super.onStart()
        adapter!!.startListening()
    }

    override fun onStop() {
        //FirebaseUI recyclerview stops listening for changes
        super.onStop()
        adapter!!.stopListening()
    }
}