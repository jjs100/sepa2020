package com.doaha

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.DocumentSnapshot


class DocAdapter(options: FirestoreRecyclerOptions<adminDoc?>, var clickListener: OnNoteItemClickListener) :
    FirestoreRecyclerAdapter<adminDoc, DocAdapter.DocHolder>(options) {

    override fun onBindViewHolder(holder: DocHolder, position: Int, model: adminDoc) {
        //assigns data from Firestore to populate adminDoc class
        holder.viewID.text = model.idDoc
        holder.viewWelcome.text = model.welcomeDoc
        holder.viewAck.text = model.ackDoc
        holder.viewInfo.text = model.infoDoc

        //creates variable of the document snapshot which allows firestore data to be accessed in different activities
        var docSnap : DocumentSnapshot = snapshots.getSnapshot(position)

        holder.initialize(snapshots[position],clickListener, docSnap)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocHolder {
        //creates RecyclerView utilising cardview
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.doc_item,
            parent, false
        )
        return DocHolder(v)
    }


    class DocHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //creates variables to assign to TextViews inside Cardview
        var viewID: TextView = itemView.findViewById(R.id.textID)
        var viewWelcome: TextView = itemView.findViewById(R.id.textWelcome)
        var viewAck: TextView = itemView.findViewById(R.id.textAck)
        var viewInfo: TextView = itemView.findViewById(R.id.textInfo)

        //needed for ClickListener functionality for Editing Documents
        fun initialize(item: adminDoc, action:OnNoteItemClickListener, documentSnapshot: DocumentSnapshot){
            itemView.setOnClickListener{
                action.onItemClick(item,adapterPosition, documentSnapshot)
            }

        }
    }

    interface OnNoteItemClickListener{
        fun onItemClick(item: adminDoc, position: Int, documentSnapshot: DocumentSnapshot)
    }
}
