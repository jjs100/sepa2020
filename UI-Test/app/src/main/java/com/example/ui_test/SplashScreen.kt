package com.example.ui_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.splash_layout.*
import android.content.Intent
import kotlin.collections.Map

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_layout)

        StartButton.setOnClickListener {
            val intent = Intent(this, Map::class.java)
            startActivity(intent)
        }
    }
}
