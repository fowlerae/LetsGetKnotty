package edu.rosehulman.samuelma.letsgetknotty.project


import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import edu.rosehulman.samuelma.letsgetknotty.R
import edu.rosehulman.samuelma.letsgetknotty.pattern.Pattern
import jp.wasabeef.picasso.transformations.CropSquareTransformation
import kotlinx.android.synthetic.main.project_list_grid_card_view.view.*

class PatternViewHolder(itemView: View, private val adapter: PatternAdapter): RecyclerView.ViewHolder(itemView) {
    private val nameTextView: TextView = itemView.findViewById(R.id.name_text_view)
    private val imageView: ImageView = itemView.findViewById(R.id.pattern_thumnail_image_view)
    private var cardView: CardView

    init {
        itemView.setOnClickListener {
            adapter.selectPattern(adapterPosition)
        }
        itemView.setOnLongClickListener{
            this.adapter.showAddEditDialog(adapterPosition)
            true
        }
        cardView = itemView.row_card_view
    }

    fun bind(pattern: Pattern) {
        nameTextView.text = pattern.name
        Picasso.get().load(pattern.imageUrl)
            .transform(CropSquareTransformation())
            .into(imageView)
    }
}