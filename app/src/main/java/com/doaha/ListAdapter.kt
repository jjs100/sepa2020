package com.doaha

import android.content.Context
import android.content.ReceiverCallNotAllowedException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recyclerview_image.view.*
import kotlinx.android.synthetic.main.recyclerview_item.view.*
import kotlinx.android.synthetic.main.recyclerview_item.view.expand_layout
import kotlinx.android.synthetic.main.recyclerview_item.view.mainTitle

private const val LIST_TYPE_IMAGE: Int = 1
private const val LIST_TYPE_DATA: Int = 0

class ListAdapter(private var list: List<Nation>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var context: Context

    //image viewholder
    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(nat: Nation) {
            //all documents should have at least one image
            itemView.mainTitle.text = nat.title
            Picasso.get()
                .load(nat.info)
                .into(itemView.image)
            //these statements check if a second and third image exist
            if (nat.image2 != null){
                Picasso.get()
                    .load(nat.image2)
                    .into(itemView.image2)
            }
            if (nat.image3 != null){
                Picasso.get()
                    .load(nat.image3)
                    .into(itemView.image3)
            }
        }
    }

    //data viewholder
    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val expandedPositionSet: HashSet<Int> = HashSet()
        private var expandedByDefault = true

        private fun setCellExpanded(holder: RecyclerView.ViewHolder) {
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

        fun bind(nat: Nation) {
            //add data to the cells
            itemView.mainTitle.text = nat.title
            itemView.subItem.text = nat.info
            //expand when you click on a cell
            itemView.expand_layout.setOnExpandListener(object : ExpandableLayout.OnExpandListener {
                override fun onExpand(expanded: Boolean) {
                    if (expandedPositionSet.contains(position)) {
                        expandedPositionSet.remove(position)
                    } else {
                        expandedPositionSet.add(position)
                    }
                }
            })
            if (position == 0) {
                setCellExpanded(this)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        return if (viewType == LIST_TYPE_DATA) {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_item, parent, false)
            DataViewHolder(v)
        } else {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_image, parent, false)
            ImageViewHolder(v)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(list[position].image == 1) {
            LIST_TYPE_IMAGE
        } else {
            LIST_TYPE_DATA
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == LIST_TYPE_DATA) {
            (holder as DataViewHolder).bind(list[position])
        } else {
            (holder as ImageViewHolder).bind(list[position])
        }
    }
}
