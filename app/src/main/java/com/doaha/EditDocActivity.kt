package com.doaha

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_edit_doc.*

class EditDocActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_doc)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        //assigns EditText fields in the activity to the Firestore data stored in the Intent
       editID.setText(intent.getStringExtra("ID"))
       editWelcome.setText(intent.getStringExtra("WELCOME"))
       editAck.setText(intent.getStringExtra("ACK"))
       editInfo.setText(intent.getStringExtra("INFO"))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //creates menu
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.new_doc_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //when save button is pressed, uses Save function to save  document
        return when (item.itemId) {
            R.id.saveDoc -> {
                saveDoc()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    private fun saveDoc() {
        //assigns the EditText data to values
        val id = editID.text.toString()
        val welcome = editWelcome.text.toString()
        val ack = editAck.text.toString()
        val info = editInfo.text.toString()

        //values are then stored to HashMap
        val newDoc = hashMapOf(
            "itemID" to id,
            "Welcome" to welcome,
            "Acknowledgements" to ack,
            "Info" to info
        )

        //document reference to database which is used to overwrite document to Firestore using hashmap of data
        val colRef = FirebaseFirestore.getInstance().collection("zones")
        colRef.document(id).set(newDoc)

        //simple check that if the itemID was changed, it creates a new document using the new ID whilst deleting the old document with the old ID
        if (id!=intent.getStringExtra("ID")){
            colRef.document(intent.getStringExtra("ID")).delete()
            Toast.makeText(this, "Note Overwritten", Toast.LENGTH_SHORT).show()
        } else{
            Toast.makeText(this, "Note edited", Toast.LENGTH_SHORT).show()
        }
    }
}