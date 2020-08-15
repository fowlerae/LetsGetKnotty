package edu.rosehulman.samuelma.letsgetknotty.createPattern

import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.create_pattern_grid_view.view.*

class CreatePatternViewHolder(itemView: View, private val adapter: CreatePatternAdapter): RecyclerView.ViewHolder(itemView) {
    private val cardView :CardView = itemView.grid_card_view
    private val imageView : ImageView = itemView.grid_stitch_image
    init {
        itemView.setOnClickListener {
            adapter.updateColor(adapterPosition)
            cardView.setCardBackgroundColor(adapter.color!!)
            if(adapter.stitch!= null) {
                adapter.updateStitch(adapterPosition)
                imageView.setImageResource(adapter.stitch!!)
            }

        }

    }

    fun bind(grid: Grid) {
        cardView.setCardBackgroundColor(grid.color)
    }
}