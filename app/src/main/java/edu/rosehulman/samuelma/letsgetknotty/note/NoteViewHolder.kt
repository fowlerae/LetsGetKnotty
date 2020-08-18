package edu.rosehulman.samuelma.letsgetknotty.note

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.samuelma.letsgetknotty.R
import kotlinx.android.synthetic.main.note_card_view.view.*

class NoteViewHolder(itemView: View, private val adapter: NoteAdapter): RecyclerView.ViewHolder(itemView) {
    private val noteTextView: TextView = itemView.findViewById(R.id.note_description)
    private var cardView: CardView = itemView.note_card_view


    fun bind(note: Note) {
        noteTextView.text = note.description
        if (note.showDark) {
            cardView.setCardBackgroundColor(
                ContextCompat.getColor(adapter.context, R.color.colorAccent)
            )
        } else {
            cardView.setCardBackgroundColor(Color.WHITE)
        }
    }

}