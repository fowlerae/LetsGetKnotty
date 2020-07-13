package edu.rosehulman.samuelma.letsgetknotty

import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.row_view.view.*

class ProjectViewHolder(itemView: View, private val adapter: ProjectAdapter): RecyclerView.ViewHolder(itemView) {
    private val nameTextView: TextView = itemView.findViewById(R.id.name_text_view)
    private val imageView: ImageView = itemView.findViewById(R.id.image_text_view)
    private var cardView: CardView

    init {
        itemView.setOnClickListener {
            adapter.selectMovieQuote(adapterPosition)

        }
        itemView.setOnLongClickListener {
            adapter.showAddEditDialog(adapterPosition)
            true
        }
        cardView = itemView.row_card_view
    }

    fun bind(project: Project) {
        nameTextView.text = project.name
        // imageView.text = project.imageUrl

        if (project.showDark) {
            cardView.setCardBackgroundColor(
                ContextCompat.getColor(adapter.context, R.color.colorAccent)
            )
        } else {
            cardView.setCardBackgroundColor(Color.WHITE)
        }
    }
}