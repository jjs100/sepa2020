package com.doaha


import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_new_doc.*


class NewDocActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_doc)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
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
        //assigns text from TextViews to values
        val id = newID.text.toString()
        val welcome = newWelcome.text.toString()
        val ack = newAck.text.toString()
        val info = newInfo.text.toString()

        //assigns values to hashmap of data
        val newDoc = hashMapOf(
            "itemID" to id,
            "Welcome" to welcome,
            "Acknowledgements" to ack,
            "Info" to info
        )


        //creates document reference to database and uses hashmap of values to create new document
        val colRef = FirebaseFirestore.getInstance().collection("zones")
        colRef.document("$id").set(newDoc)
        Toast.makeText(this, "Document Added", Toast.LENGTH_SHORT).show()
    }
}