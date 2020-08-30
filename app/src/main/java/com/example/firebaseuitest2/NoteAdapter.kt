package com.example.firebaseuitest2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions


class NoteAdapter(options: FirestoreRecyclerOptions<Note?>) :
    FirestoreRecyclerAdapter<Note, NoteAdapter.NoteHolder>(options) {
    override fun onBindViewHolder(holder: NoteHolder, position: Int, model: Note) {

        holder.textViewName.setText(model.name)
        holder.textViewInfo.setText(model.info)
        holder.textViewRole.setText(model.role)
        holder.textViewAge.setText(model.age)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val v = LayoutInflater.from(parent.context).inflate(
            R.layout.note_item,
            parent, false
        )
        return NoteHolder(v)
    }

    class NoteHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewName: TextView = itemView.findViewById(R.id.text_view_title)
        var textViewInfo: TextView =itemView.findViewById(R.id.text_view_info)
        var textViewRole: TextView = itemView.findViewById(R.id.text_view_role)
        var textViewAge: TextView = itemView.findViewById(R.id.text_view_age)
    }
}
