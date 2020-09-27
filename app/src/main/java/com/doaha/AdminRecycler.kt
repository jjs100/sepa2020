package com.doaha

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AdminRecycler : AppCompatActivity(),DocAdapter.OnNoteItemClickListener {
    private val db = FirebaseFirestore.getInstance()
    private val notebookRef = db.collection("zones")
    private var adapter: DocAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_recycler)
/*
        //Create Notification Button Switch
        loadButtonConfig(findViewById<Switch>(R.id.notifications_toggle))*/

        //Creating variable for floating button to add new documents
        val buttonAddNote = findViewById<FloatingActionButton>(R.id.button_add_note)
        buttonAddNote.setOnClickListener{
            startActivity(Intent(this, NewDocActivity::class.java))
        }
        //RecyclerView setup
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        //query to allow all documents to be ordered  by their ID (alphabetically)
        val query: Query = notebookRef.orderBy("itemID", Query.Direction.ASCENDING)
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