package edu.rosehulman.samuelma.letsgetknotty.createPattern

import android.graphics.Color
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.samuelma.letsgetknotty.R
import kotlinx.android.synthetic.main.project_list_grid_card_view.view.*

class CreatePatternViewHolder(itemView: View, private val adapter: CreatePatternAdapter): RecyclerView.ViewHolder(itemView) {
//    private val nameTextView: TextView = itemView.findViewById(R.id.name_text_view)
//    private val imageView: ImageView = itemView.findViewById(R.id.image_text_view)
    private var cardView: CardView

    init {
        itemView.setOnClickListener {
         //   adapter.selectProject(adapterPosition)

        }
        itemView.setOnLongClickListener {
         //   adapter.showAddEditDialog(adapterPosition)
            true
        }
        cardView = itemView.row_card_view
    }

    fun bind(grid: Grid) {
//        nameTextView.text = project.name
//        Picasso.get().load(project?.imageUrl)
//            .transform(CropSquareTransformation())
//            .into(imageView)
//        //  Log.d(Constants.TAG, "IMAGE VIEW SIZE: ${imageView.width}")
        if (grid.showDark) {
            cardView.setCardBackgroundColor(
                ContextCompat.getColor(adapter.context, R.color.colorAccent)
            )
        } else {
            cardView.setCardBackgroundColor(Color.WHITE)
        }
    }
}