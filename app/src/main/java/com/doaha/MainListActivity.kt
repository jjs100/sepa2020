package com.doaha

import android.content.Intent
import android.os.Bundle

class MainListActivity : SingleFragmentActivity() {

    //val locName = intent.getStringExtra ("name")
    //val test = DataLoad("Bidwell")

    init {
        setContentView(R.layout.loading_screen)
    }


    override fun createFragment() = ListFragment.newInstance()
}