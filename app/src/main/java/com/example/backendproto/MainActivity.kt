package com.example.backendproto

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var itemSelect = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Disclaimer")
        builder.setMessage("An important distinction for names, our project is operating under the Nations and spelling of nation names defined in AIATSIS’ map of indigenous Australia, originally sourced from D Horton’s Encyclopedia of Aboriginal Australia(1994).\n" +
                "Some nations also use the Tindale name for their nation, that is, the name defined by Norman Tindale in the Tribal Linguistic Map of Australia(1974), the regions are fairly comparable, only with slight changes in name and border, as Horton accounts for more transient nation borders.\n")

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            Toast.makeText(applicationContext,
                android.R.string.yes, Toast.LENGTH_SHORT).show()
        }
        builder.show()

        val spinner: Spinner = findViewById(R.id.zoneSpinner)
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

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        itemSelect = true
        parent.getItemAtPosition((pos))
    }


    override fun onNothingSelected(parent: AdapterView<*>?) {
        itemSelect = false
    }

    fun searchBtn(v: View) {
        if (itemSelect){
            val id: String = zoneSpinner.selectedItem.toString()
            val intent = Intent(this, DisplayZoneInfo::class.java).apply {
                putExtra(EXTRA_MESSAGE, id)
            }
            startActivity(intent)
        }

    }

    companion object {
        const val EXTRA_MESSAGE = ""
    }


}



