package edu.rosehulman.samuelma.letsgetknotty.projectlist

import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import edu.rosehulman.samuelma.letsgetknotty.R
import edu.rosehulman.samuelma.letsgetknotty.project.Project
import edu.rosehulman.samuelma.letsgetknotty.projectlist.ProjectListAdapter
import jp.wasabeef.picasso.transformations.CropSquareTransformation
import kotlinx.android.synthetic.main.grid_view.view.*

class ProjectListViewHolder(itemView: View, private val listAdapter: ProjectListAdapter): RecyclerView.ViewHolder(itemView) {
    private val nameTextView: TextView = itemView.findViewById(R.id.name_text_view)
    private val imageView: ImageView = itemView.findViewById(R.id.image_text_view)
    private var cardView: CardView

    init {
        itemView.setOnClickListener {
            listAdapter.selectMovieQuote(adapterPosition)

        }
        itemView.setOnLongClickListener {
            listAdapter.showAddEditDialog(adapterPosition)
            true
        }
        cardView = itemView.row_card_view
    }

    fun bind(project: Project) {
        nameTextView.text = project.name
        Picasso.get().load(project?.imageUrl)
            .transform(CropSquareTransformation())
            .into(imageView)
      //  Log.d(Constants.TAG, "IMAGE VIEW SIZE: ${imageView.width}")
        if (project.showDark) {
            cardView.setCardBackgroundColor(
                ContextCompat.getColor(listAdapter.context, R.color.colorAccent)
            )
        } else {
            cardView.setCardBackgroundColor(Color.WHITE)
        }
    }
}