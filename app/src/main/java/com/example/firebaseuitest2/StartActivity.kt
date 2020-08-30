package com.example.firebaseuitest2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var itemSelect = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
    val spinner: Spinner = findViewById(R.id.spinner)
    ArrayAdapter.createFromResource(
    this,
    R.array.team_array,
    android.R.layout.simple_spinner_item
    ).also { adapter ->
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
    spinner.onItemSelectedListener = this
}


    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        itemSelect = true
        parent.getItemAtPosition((pos))
    }


    override fun onNothingSelected(parent: AdapterView<*>?) {
        itemSelect = false
    }

    fun searchBtn(v: View) {
        if (itemSelect){
            val id: String = spinner.selectedItem.toString()
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, id)
            }
            startActivity(intent)
        }

    }

    companion object {
        const val EXTRA_MESSAGE = ""
    }
}

