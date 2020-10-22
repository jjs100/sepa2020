package com.doaha

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_admin_image.*

class AdminImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_image)

        //displays images if they are available in database using intent from the last activity
        when (intent.getStringExtra("IMG1")){
            null, "" -> {
                modifyImage(img1Text, "img1", false)
                }
            else -> {
                Picasso.get()
                        .load(intent.getStringExtra("IMG1"))
                        .into(img1View)
                modifyImage(img1Text, "img1", true)
            }
        }

        when (intent.getStringExtra("IMG2")){
            null, "" -> {
                modifyImage(img2Text, "img2", false)
            }
            else -> {
                Picasso.get()
                        .load(intent.getStringExtra("IMG2"))
                        .into(img2View)
                modifyImage(img2Text, "img2", true)
            }
        }

        when (intent.getStringExtra("IMG3")){
            null, "" -> {
                modifyImage(img3Text, "img3", false)
            }
            else -> {
                Picasso.get()
                        .load(intent.getStringExtra("IMG3"))
                        .into(img3View)
                modifyImage(img3Text, "img3", true)
            }
        }
    }

    //when not available or if the user wants to modify their images, they will be directed
    //to appropriate activities to either upload new images or update their existing files
    private fun modifyImage(imageText : TextView, imageField : String, condition : Boolean){
        when (condition) {
            true -> {
                imageText.text = getString(R.string.modify_image)
                val i = Intent(this, AdminModifyImageActivity::class.java)
                imageText.setOnClickListener {
                    i.putExtra("Image Field", imageField)
                    i.putExtra("ID", intent.getStringExtra("ID"))
                    startActivity(i)
                }
            }
            false -> {
                imageText.text = getString(R.string.add_image)
                val i = Intent(this, UploadImageActivity::class.java)
                imageText.setOnClickListener {
                    i.putExtra("Image Field", imageField)
                    i.putExtra("ID", intent.getStringExtra("ID"))
                    startActivity(i)
                }
            }
        }
    }
}


