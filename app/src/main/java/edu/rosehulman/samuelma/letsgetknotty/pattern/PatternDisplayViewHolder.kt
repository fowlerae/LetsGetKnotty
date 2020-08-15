package edu.rosehulman.samuelma.letsgetknotty.pattern

import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.samuelma.letsgetknotty.createPattern.Grid
import edu.rosehulman.samuelma.letsgetknotty.createPattern.PatternDisplayAdapter
import kotlinx.android.synthetic.main.create_pattern_grid_view.view.*

class PatternDisplayViewHolder(itemView: View, private val adapter: PatternDisplayAdapter): RecyclerView.ViewHolder(itemView) {

    private val cardView :CardView = itemView.grid_card_view
    private val imageView : ImageView = itemView.grid_stitch_image

    fun bind(grid: Grid) {
        cardView.setCardBackgroundColor(grid.color)
        if(grid.image != null) {
            imageView.setImageResource(grid.image!!)
        }
    }
}