package com.doaha

import android.content.Intent
import android.os.Bundle

class MainListActivity : SingleFragmentActivity() {

    //val locName = intent.getStringExtra ("name")
    //private val compNationData = DataLoad("Bidwell")
    //val adwfkiushkjadhkajshfbkas = compNationData.returnData()
    //var test = "test"
    override fun createFragment() = ListFragment.newInstance()
}