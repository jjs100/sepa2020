package com.doaha

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.splash_layout.*
import android.content.Intent
import android.text.Html
import android.widget.TextView
import kotlinx.android.synthetic.*

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_layout)

        StartButton.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        splashMessage.setOnClickListener {
            //admin login didn't work correctly
            startActivity(Intent(this, AdminLogin::class.java))
        }
    }
}
