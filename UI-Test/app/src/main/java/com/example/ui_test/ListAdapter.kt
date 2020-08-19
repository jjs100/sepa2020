package com.example.ui_test

import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class ListAdapter(private val list: List<Dummy>) : RecyclerView.Adapter<DummyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DummyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return DummyViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: DummyViewHolder, position: Int) {
        val dummy: Dummy = list[position]
        holder.bind(dummy)
    }

    override fun getItemCount(): Int = list.size
}

class DummyViewHolder(inflater: LayoutInflater, parent: ViewGroup):
        RecyclerView.ViewHolder(inflater.inflate(R.layout.recyclerview_item, parent, false)) {

            private var dTitle: TextView? = null
            private var dInfo: TextView? = null

            init {
                dTitle = itemView.findViewById(R.id.dummyTitle)
                dInfo = itemView.findViewById(R.id.dummyInfo)
            }

            fun bind(dummy: Dummy) {
                dTitle?.text = dummy.title
                dInfo?.text = dummy.name
             }
}
