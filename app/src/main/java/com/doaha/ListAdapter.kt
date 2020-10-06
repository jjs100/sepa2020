package com.doaha

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recyclerview_item.view.*

class ListAdapter(private val list: List<Nation>) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private val expandedPositionSet: HashSet<Int> = HashSet()
    private lateinit var context: Context
    lateinit var tempView : ImageView
    var expandedByDefault = true

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false)
        val vh = ViewHolder(v)
        context = parent.context
        return vh
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //add data to the cells
        holder.itemView.mainTitle.text = list[position].title
        holder.itemView.subItem.text = list[position].info

        
        //expand when you click on a cell
        holder.itemView.expand_layout.setOnExpandListener(object : ExpandableLayout.OnExpandListener {
            override fun onExpand(expanded: Boolean) {
                if (expandedPositionSet.contains(position)) {
                    expandedPositionSet.remove(position)
                } else {
                    expandedPositionSet.add(position)
                }
            }
        })
        if (position != 0) {
            holder.itemView.expand_layout.setExpand(expandedPositionSet.contains(position))
        }
    setFirstCellExpanded(holder, position)
    }

    private fun setFirstCellExpanded(holder: ViewHolder, position: Int) {
        if (position == 0) {
            if (expandedByDefault) {
                holder.itemView.post {
                    holder.itemView.expand_layout.setExpand(true)
                }
            } else {
                holder.itemView.post {
                    holder.itemView.expand_layout.setExpand(false)
                }
            }
        }
    }
}
