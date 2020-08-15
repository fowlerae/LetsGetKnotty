package edu.rosehulman.samuelma.letsgetknotty.createPattern

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.samuelma.letsgetknotty.R
import kotlinx.android.synthetic.main.create_pattern_grid_view.view.*
import kotlinx.android.synthetic.main.project_list_grid_card_view.view.*

class CreatePatternViewHolder(itemView: View, private val adapter: CreatePatternAdapter): RecyclerView.ViewHolder(itemView) {
    private val cardView :CardView = itemView.grid_card_view
    private val imageView : ImageView = itemView.grid_stitch_image
    init {
        itemView.setOnClickListener {
            if(adapter.stitch == null) {
                adapter.updateColor(adapterPosition)
                cardView.setCardBackgroundColor(adapter.color)
            } else {
                adapter.updateStitch(adapterPosition)
                adapter.updateStitch(adapterPosition)
                imageView.setImageResource(adapter.stitch!!)
            }

        }
    }

    fun bind(grid: Grid) {
        cardView.setCardBackgroundColor(grid.color)
    }
}