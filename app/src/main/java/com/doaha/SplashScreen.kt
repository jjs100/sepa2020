package com.doaha

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.splash_layout.*
import android.content.Intent
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM
import androidx.core.widget.TextViewCompat.setAutoSizeTextTypeWithDefaults


class SplashScreen : AppCompatActivity() {

    private var actionValue: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_layout)

        //Making LinearLayout able to register touch, executing action on touch
        val touchLayout: ConstraintLayout = findViewById(R.id.splash)
        touchLayout.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, m: MotionEvent): Boolean {
                handleTouch(m)
                return true
            }
        })
        StartButton.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        val resCredButton: TextView = findViewById(R.id.randc)
        val resCredContent: TextView = findViewById(R.id.randccontent)
        resCredButton.setOnClickListener {
            if (resCredContent.visibility == View.GONE) {
                resCredButton.visibility = View.GONE
                resCredContent.visibility = View.VISIBLE
                resCredContent.background.alpha = 145
            }
        }

        resCredContent.setOnClickListener {
            if (resCredContent.visibility == View.VISIBLE) {
                resCredContent.visibility = View.GONE
                resCredButton.visibility = View.VISIBLE
            }
        }
    }

    //this function iterates ActionValue everytime the user swipes down, 20 swipes results in the admin page loading
    private fun handleTouch(m: MotionEvent) {
        val pointerCount = m.pointerCount

        for (i in 0 until pointerCount) {
            val action = m.actionMasked
            when (action) {
                MotionEvent.ACTION_DOWN -> actionValue++
            }
            Log.d("Action Value: ", "$actionValue")
            if (actionValue == 20) {
                actionValue = 0
                startActivity(Intent(this, AdminLogin::class.java))
            }
        }
    }
}



