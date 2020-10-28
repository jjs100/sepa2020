package com.doaha

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_admin_recycler.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AdminRecycler : AppCompatActivity(),DocAdapter.OnNoteItemClickListener {
    private val db = FirebaseFirestore.getInstance()
    private val notebookRef = db.collection("zones")
    private var adapter: DocAdapter? = null
    var nationID = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_recycler)

        //Creating variable for floating button to add new documents
        val buttonAddNote = findViewById<FloatingActionButton>(R.id.button_add_note)
        buttonAddNote.setOnClickListener{
            startActivity(Intent(this, NewDocActivity::class.java))
        }

        //Creates variable for search to search documents and display on different activity
        val imageSearch : ImageView = findViewById(R.id.searchBtn)
        imageSearch.setOnClickListener{
            val searchTerm : String = search_bar.text.toString()
            if (nationID.contains(searchTerm)){
                val i = Intent(this, AdminSearchResult::class.java)
                i.putExtra("Searchterm", searchTerm)
                startActivity(i)
            } else {
                Toast.makeText(this, "Search Failed", Toast.LENGTH_SHORT).show()
            }

        }

        //RecyclerView setup
        //query to allow all documents to be ordered  by their ID (alphabetically)

        val query : Query = notebookRef.orderBy("itemID", Query.Direction.ASCENDING)
        setUpAuto()
        setUpRecyclerView(query)

        //Creates auto-complete Search Bar using IDs collected from existing documents in database
        val searchAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, nationID)
        search_bar.threshold = 1
        search_bar.setAdapter(searchAdapter)
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
        intent.putExtra("ID",  documentSnapshot.getString("itemID"))
        intent.putExtra("IMG1",  documentSnapshot.getString("img1"))
        intent.putExtra("IMG2", documentSnapshot.getString("img2"))
        intent.putExtra("IMG3", documentSnapshot.getString("img3"))

        intent.putExtra("Document", DocData("${documentSnapshot.getString("Acknowledgements")}", "${documentSnapshot.getString("Welcome")}", "${documentSnapshot.getString("History")}",
                "${documentSnapshot.getString("RAP")}", "${documentSnapshot.getString("Elders")}"))
        //starts activity with intent and the documents information
        startActivity(intent)
    }

    private fun setUpAuto() {
        GlobalScope.launch(Dispatchers.IO) {
            notebookRef.whereNotEqualTo("itemID", "")
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            Log.d("TAG", "${document.getString("itemID")} => ${document.data}")
                            nationID.add(document.getString("itemID").toString())
                        }
                    }
        }
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