package edu.rosehulman.samuelma.letsgetknotty.pattern

import android.view.View
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.samuelma.letsgetknotty.createPattern.Grid
import edu.rosehulman.samuelma.letsgetknotty.createPattern.PatternDisplayAdapter
import kotlinx.android.synthetic.main.create_pattern_grid_view.view.*

class PatternDisplayViewHolder(itemView: View, private val adapter: PatternDisplayAdapter): RecyclerView.ViewHolder(itemView) {

    private val cardView :CardView = itemView.grid_card_view

    fun bind(grid: Grid) {
        cardView.setCardBackgroundColor(grid.color)
    }
}