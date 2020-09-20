package com.doaha

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.doaha.application.DoAHAApplication
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class AdminPage : AppCompatActivity(),  DocAdapter.OnNoteItemClickListener {
    private val db = FirebaseFirestore.getInstance()
    private val notebookRef = db.collection("zones")
    private var adapter: DocAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_page)

        //Create Notification Button Switch
        loadButtonConfig(findViewById<Switch>(R.id.notifications_toggle))

        //Creating variable for floating button to add new documents
        var buttonAddNote = findViewById<FloatingActionButton>(R.id.button_add_note)
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
        val options = FirestoreRecyclerOptions.Builder<adminDoc>()
            .setQuery(query, adminDoc::class.java)
            .build()
        adapter = DocAdapter(options, this)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    override fun onItemClick(item: adminDoc, position: Int, documentSnapshot : DocumentSnapshot) {
        val intent = Intent(this, EditDocActivity::class.java)

        //uses the intent for the EditDocActivity to attach the text displayed in the cardview so it can be viewed/edited in the next activity
        var docID : String? = documentSnapshot.getString("itemID")
        var docWelcome : String? = documentSnapshot.getString("Welcome")
        var docAck : String? = documentSnapshot.getString("Acknowledgements")
        var docInfo : String? = documentSnapshot.getString("Info")

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

    private fun loadButtonConfig(notificationButton: Switch) {
        //Load Application variable value
        val isNotificationsEnabled: Boolean =
            (this.application as DoAHAApplication).getIsNotificationEnabled(
                getSharedPreferences()
            )

        //Set in button value
        notificationButton.isChecked = isNotificationsEnabled

        //Notification Button default to true and inverse current value if not set
        notificationButton.setOnClickListener {
            (this.application as DoAHAApplication).setIsNotificationEnabled(
                getSharedPreferences(),
                !(this.application as DoAHAApplication).getIsNotificationEnabled(
                    getSharedPreferences()
                )
            )
        }
    }

    private fun getSharedPreferences(): SharedPreferences {
        return applicationContext.getSharedPreferences(
            getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
    }
}