package com.doaha

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_admin_modify_image.*

class AdminModifyImageActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance().collection("zones")
    lateinit var filepath : Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_modify_image)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val docID = intent.getStringExtra("ID")!!.toString()
        val imageField = intent.getStringExtra("Image Field")!!.toString()
        db.document(docID).get()
            .addOnSuccessListener { document ->
                if (document != null)
                    Picasso.get()
                        .load(document.getString(imageField))
                        .into(currentImage)
            }

        selectButton.setOnClickListener {
            chooseImage()
        }

        updateButton.setOnClickListener {
            uploadImage()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //creates menu
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.modify_image_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //menu item to delete image in Firebase Storage and the reference in Firestore
        return when (item.itemId) {
            R.id.deleteImage -> {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Are you sure you want to delete this Image?")
                    .setPositiveButton("Yes") { dialog, id ->
                        val docID = intent.getStringExtra("ID")!!.toString()
                        val imageField = intent.getStringExtra("Image Field")!!.toString()
                        db.document(docID).update(imageField, FieldValue.delete())
                        val storagePath = "ZONEIMAGES/$docID/$imageField.png"
                        val storageRef = FirebaseStorage.getInstance().reference.child(storagePath)
                        storageRef.delete()
                        Toast.makeText(this, "Image Deleted", Toast.LENGTH_SHORT).show()
                        finish()
                        startActivity(Intent(this, AdminRecycler::class.java))
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

    private fun uploadImage(){
        //creates variables for values in Intent
        val docID : String = intent.getStringExtra("ID")!!.toString()
        val imageField : String = intent.getStringExtra("Image Field")!!.toString()
        if (filepath!=null){
            val dialog = ProgressDialog(this)
            dialog.setTitle("Updating Image")
            dialog.show()
            //creates the path to where the image will be overwritten in Firebase Storage and in the Firestore database
            val storagePath = "ZONEIMAGES/$docID/$imageField.png"
            val db = FirebaseFirestore.getInstance().collection("zones").document(docID)
            val storageRef = FirebaseStorage.getInstance().reference.child(storagePath)
            val uploadTask = storageRef.putFile(filepath)
            uploadTask.addOnSuccessListener {
                dialog.dismiss()
                Toast.makeText(applicationContext, "Image has been updated", Toast.LENGTH_SHORT).show()
                //uploads the image using the storage reference and the file path
                val imageReference = storageRef.downloadUrl
                imageReference.addOnSuccessListener {
                    val imageLink = it.toString()
                    db.update(imageField, imageLink)
                }

                finish()
                startActivity(Intent(this, AdminRecycler::class.java))
            }
            uploadTask.addOnFailureListener{p0 ->
                dialog.dismiss()
                Toast.makeText(applicationContext, p0.message, Toast.LENGTH_SHORT).show()
            }
            uploadTask.addOnProgressListener {
                dialog.setMessage("Please wait while the new image is uploading")
            }
        }
    }
    //opens the file manager allowing users to select only images
    private fun chooseImage() {
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(i, "Choose Image"), 111)
    }
    //displays image the user has selected in the ImageView and creates the filepath to the image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==111 && resultCode == Activity.RESULT_OK && data !=null){
            filepath = data.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filepath)
            currentImage.setImageBitmap(bitmap)
        }

    }
}