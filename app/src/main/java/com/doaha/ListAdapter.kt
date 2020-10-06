package com.doaha

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    lateinit var tempView : ImageView

    //image viewholder
    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(nat: Nation) {
            itemView.mainTitle.text = nat.title
            Log.d("BIGBOI", "nation is ${nat.title} amd ${nat.info} and ${nat.image}")
            Picasso.get()
                .load(nat.info)
                .fit()
                .into(itemView.image)
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
            if (position != 0) {
                itemView.expand_layout.setExpand(expandedPositionSet.contains(position))
            }
            //setCellExpanded()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        if (viewType == LIST_TYPE_DATA) {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false)
            return DataViewHolder(v)
        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_image, parent, false)
            return ImageViewHolder(v)
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
