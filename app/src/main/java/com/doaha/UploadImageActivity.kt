package com.doaha

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_upload_image.*

class UploadImageActivity : AppCompatActivity() {

    lateinit var filepath : Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_image)

        chooseButton.setOnClickListener{
            chooseImage()
        }

        uploadButton.setOnClickListener{
            uploadImage()
        }
    }

    private fun uploadImage(){
        //creates variables for values in Intent
        val docID : String = intent.getStringExtra("ID")!!.toString()
        val imageField : String = intent.getStringExtra("Image Field")!!.toString()
        if (filepath!=null){
            val dialog = ProgressDialog(this)
            dialog.setTitle("Uploading Image")
            dialog.show()
            //creates the path to where the image will be saved in Firebase Storage and in the Firestore database
            val storagePath = "ZONEIMAGES/$docID/$imageField.png"
            val db = FirebaseFirestore.getInstance().collection("zones").document(docID)
            val storageRef = FirebaseStorage.getInstance().reference.child(storagePath)
            val uploadTask = storageRef.putFile(filepath)
            //uploads the image using the storage reference and the file path
            uploadTask.addOnSuccessListener {
                        dialog.dismiss()
                        Toast.makeText(applicationContext, "Image has been uploaded", Toast.LENGTH_SHORT).show()
                        //after succesfully adding image to storage, the image reference is saved to the Firestore database
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
                        dialog.setMessage("Please wait while the image is uploading")
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
            chosenImage.setImageBitmap(bitmap)
        }

    }
}