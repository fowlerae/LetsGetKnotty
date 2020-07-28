package edu.rosehulman.samuelma.letsgetknotty.rowCounter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.samuelma.letsgetknotty.R


class RowCounterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)  {

    private val rowTextView: TextView = itemView.findViewById(R.id.current_row_text_view)
    private val upImageView : ImageView = itemView.findViewById(R.id.row_counter_increase_button)
    private val downImageView : ImageView = itemView.findViewById(R.id.row_counter_decrease_button)
  //  private var cardView: CardView = itemView.row_card_view

    fun bind(rowCounter: RowCounter) {
        rowTextView.text = rowCounter.currentRow.toString()
        upImageView.setOnClickListener {
            rowCounter.increaseRow()
        }
        downImageView.setOnClickListener {
            rowCounter.decreaseRow()
        }
    }
}