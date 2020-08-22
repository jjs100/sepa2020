package com.doaha

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.splash_layout.*
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

class SplashScreen : AppCompatActivity(), AdapterView.OnItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_layout)

        StartButton.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        //LocationSpinner Implementation
        val spinner: Spinner = findViewById(R.id.LocationSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.zones_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = this
    }
    private var itemSelect = false
    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        itemSelect = true
        parent.getItemAtPosition((pos))
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        itemSelect = false
    }

    fun searchButton(v: View) {
        if (itemSelect){
            val id: String = LocationSpinner.selectedItem.toString()
            val intent = Intent(this, DataDisplay::class.java).apply {
                putExtra(EXTRA_MESSAGE, id)
            }
            startActivity(intent)
        }

    }

    companion object {
            const val EXTRA_MESSAGE = ""
    }
}

