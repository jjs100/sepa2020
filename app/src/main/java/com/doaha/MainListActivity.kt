package com.doaha

class MainListActivity : SingleFragmentActivity() {
    override fun createFragment() = ListFragment.newInstance()
}