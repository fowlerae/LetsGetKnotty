package edu.rosehulman.samuelma.letsgetknotty.createPattern

import android.graphics.Color
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.samuelma.letsgetknotty.R
import kotlinx.android.synthetic.main.create_pattern_grid_view.view.*
import kotlinx.android.synthetic.main.project_list_grid_card_view.view.*

class CreatePatternViewHolder(itemView: View, private val adapter: CreatePatternAdapter): RecyclerView.ViewHolder(itemView) {
//    private val nameTextView: TextView = itemView.findViewById(R.id.name_text_view)
//    private val imageView: ImageView = itemView.findViewById(R.id.image_text_view)
    private val cardView :CardView = itemView.grid_card_view
    init {
        itemView.setOnClickListener {
            adapter.updateColor(adapterPosition, adapter.context.resources.getColor(R.color.colorPrimaryDark))
            cardView.setCardBackgroundColor(adapter.context.resources.getColor(R.color.colorPrimaryDark))
        }
        itemView.setOnLongClickListener {
         //   adapter.showAddEditDialog(adapterPosition)
            true
        }
    }

    fun bind(grid: Grid) {
        cardView.setCardBackgroundColor(grid.color)
    }
}