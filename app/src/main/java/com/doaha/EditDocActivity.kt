package com.doaha

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_edit_doc.*

class EditDocActivity : AppCompatActivity() {
    val colRef = FirebaseFirestore.getInstance().collection("zones")
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
        menuInflater.inflate(R.menu.edit_doc_menu, menu)
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
            R.id.images -> {
                val i = Intent(this, AdminImageActivity::class.java)
                i.putExtra("IMG1", intent.getStringExtra("IMG1"))
                i.putExtra("IMG2", intent.getStringExtra("IMG2"))
                i.putExtra("IMG3", intent.getStringExtra("IMG3"))
                i.putExtra("ID", intent.getStringExtra("ID"))
                startActivity(i)
                true
            }
            R.id.deleteDoc -> {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Are you sure you want to delete this document?")
                    .setPositiveButton("Yes") { dialog, id ->
                        colRef.document(intent.getStringExtra("ID")!!.toString()).delete()
                        Toast.makeText(this, "Document Deleted", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .setNegativeButton("No"
                    ) { dialog, id ->
                        dialog.dismiss()
                    }
                // Create the AlertDialog object and return it
                val dialog = builder.create()
                dialog.show()
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

        //document reference to database which is used to overwrite document to Firestore using HashMap of data
        colRef.document(id).set(newDoc)

        //simple check that if the itemID was changed, it creates a new document using the new ID whilst deleting the old document with the old ID
        if (id!=intent.getStringExtra("ID")){
            colRef.document(intent.getStringExtra("ID")!!.toString()).delete()
            Toast.makeText(this, "Document Overwritten", Toast.LENGTH_SHORT).show()
        } else{
            Toast.makeText(this, "Document edited", Toast.LENGTH_SHORT).show()
        }
    }
}