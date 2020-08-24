package com.doaha

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_main.*
import com.doaha.ListAdapter


//dummy data class for proof of progress
//replace this with correct dataclass for information from database
data class Dummy(val title: String, val info: String)


class ListFragment : Fragment() {

    //implementation of dummy data class for now, will be replaced with data from database
    private var nationData = listOf(
        Dummy("Local Elders", "Here is some elder data"),
        Dummy("Some Other Title", "Here is some other data"),
        Dummy("Some Other Title", "Here is some other data"),
        Dummy("Some Other Title", "Here is some other data"),
        Dummy("Some Other Title", "Here is some other data"),
        Dummy("Some Other Title", "Here is some other data"),
        Dummy("Some Other Title", "Here is some other data"),
        Dummy("Some Other Title", "Here is some other data"),
        Dummy("Some Other Title", "Here is some other data"),
        Dummy("Some Other Title", "Here is some other data"),
        Dummy("Some Other Title", "Here is some other data"),
        Dummy("Some Other Title", "Here is some other data")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list_recycler_view.apply {
            val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            divider.setDrawable(ContextCompat.getDrawable(context, R.drawable.divier)!!)
            list_recycler_view.addItemDecoration(divider)
            layoutManager = LinearLayoutManager(activity)
            adapter = ListAdapter(nationData)
            this.setHasFixedSize(true)
        }
    }

    companion object {
        fun newInstance(): ListFragment = ListFragment()
    }
}
