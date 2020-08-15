package edu.rosehulman.samuelma.letsgetknotty.createPattern

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.samuelma.letsgetknotty.Constants
import kotlinx.android.synthetic.main.create_pattern_grid_view.view.*

class CreatePatternViewHolder(itemView: View, private val adapter: CreatePatternAdapter): RecyclerView.ViewHolder(itemView) {
    private val cardView :CardView = itemView.grid_card_view
    private val imageView : ImageView = itemView.grid_stitch_image
    init {
        itemView.setOnClickListener {
            Log.d(Constants.TAG, "View Holder Stitch: ${adapter.stitch}")
            Log.d(Constants.TAG, "View Holder  Button Color: ${adapter.color}")
            adapter.updateColor(adapterPosition)
            cardView.setCardBackgroundColor(adapter.color!!)
            if(adapter.stitch!= null) {
                adapter.updateStitch(adapterPosition)
                imageView.setImageResource(adapter.stitch!!)
            }

        }

    }

    fun bind(grid: Grid) {
        Log.d(Constants.TAG, "On Bind: $grid")
        cardView.setCardBackgroundColor(grid.color)
        if(grid.image != null) {
            imageView.setImageResource(grid.image!!)
            imageView.visibility = View.VISIBLE
        } else {
            imageView.visibility = View.GONE
        }
    }
}