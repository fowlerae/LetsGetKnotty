package edu.rosehulman.samuelma.letsgetknotty.createPattern

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import edu.rosehulman.samuelma.letsgetknotty.Constants
import edu.rosehulman.samuelma.letsgetknotty.R
import edu.rosehulman.samuelma.letsgetknotty.pattern.Pattern
import edu.rosehulman.samuelma.letsgetknotty.project.Project

class CreatePatternAdapter(val context: Context, uid: String, project: Project, pattern: Pattern) : RecyclerView.Adapter<CreatePatternViewHolder>()  {
    private val rectangle = ArrayList<Grid>()
//    private val gridRef = FirebaseFirestore
//        .getInstance()
//        .collection(Constants.USERS_COLLECTION)
//        .document(uid)
//        .collection(Constants.PROJECTS_COLLECTION)
//        .document(project.id)
//        .collection(Constants.PATTERNS_COLLECTION)
//        .document(pattern.id)
//        .collection(Constants.GRID_COLLECTION)
//
//    private lateinit var listenerRegistration: ListenerRegistration
//
//    fun addSnapshotListener() {
//        listenerRegistration = gridRef
//            .orderBy(Project.LAST_TOUCHED_KEY, Query.Direction.ASCENDING)
//            .addSnapshotListener { querySnapshot, e ->
//                if (e != null) {
//                    Log.w(Constants.TAG, "listen error", e)
//                } else {
//                    processSnapshotChanges(querySnapshot!!)
//                }
//            }
//    }
//
//    private fun processSnapshotChanges(querySnapshot: QuerySnapshot) {
//        // Snapshots has documents and documentChanges which are flagged by type,
//        // so we can handle C,U,D differently.
//        for (documentChange in querySnapshot.documentChanges) {
//            val grid = Grid.fromSnapshot(documentChange.document)
//            when (documentChange.type) {
//                DocumentChange.Type.ADDED -> {
//                    Log.d(Constants.TAG, "Adding $grid")
//                    rectangle.add(0, grid)
//                    notifyItemInserted(0)
//                }
//                DocumentChange.Type.REMOVED -> {
//                    Log.d(Constants.TAG, "Removing $grid")
//                    val index = rectangle.indexOfFirst { it.id == grid.id }
//                    rectangle.removeAt(index)
//                    notifyItemRemoved(index)
//                }
//                DocumentChange.Type.MODIFIED -> {
//                    Log.d(Constants.TAG, "Modifying $grid")
//                    val index = rectangle.indexOfFirst { it.id == grid.id }
//                    rectangle[index] = grid
//                    notifyItemChanged(index)
//                }
//            }
//        }
//    }

    override fun onCreateViewHolder(parent: ViewGroup, index: Int): CreatePatternViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.create_pattern_grid_view, parent, false)
        return CreatePatternViewHolder(view, this)
    }

    override fun onBindViewHolder(
        listViewHolder: CreatePatternViewHolder,
        index: Int
    ) {
        listViewHolder.bind(rectangle[index])
    }

    override fun getItemCount() = rectangle.size


//    private fun add(project: Project) {
//        gridRef.add(project)
//    }
//
//    private fun edit(position: Int, quote: String, movie: String) {
//        rectangle[position].name = quote
//        rectangle[position].imageUrl = movie
//        gridRef.document(rectangle[position].id).set(rectangle[position])
//    }
//
//
//    private fun remove(position: Int) {
//        gridRef.document(rectangle[position].id).delete()
//    }

    fun buildGrid() {
        // build grid here loop the number of grid (width * height) and loop over number calling add method
    }

}