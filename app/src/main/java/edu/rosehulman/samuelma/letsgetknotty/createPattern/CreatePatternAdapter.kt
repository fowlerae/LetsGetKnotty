package edu.rosehulman.samuelma.letsgetknotty.createPattern

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import edu.rosehulman.samuelma.letsgetknotty.Constants
import edu.rosehulman.samuelma.letsgetknotty.R
import edu.rosehulman.samuelma.letsgetknotty.pattern.Pattern
import edu.rosehulman.samuelma.letsgetknotty.project.Project

class CreatePatternAdapter(val context: Context, uid: String, project: Project, val pattern: Pattern) :
    RecyclerView.Adapter<CreatePatternViewHolder>() {
    val rectangles = ArrayList<Grid>()
    private val gridRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.USERS_COLLECTION)
        .document(uid)
        .collection(Constants.PROJECTS_COLLECTION)
        .document(project.id)
        .collection(Constants.PATTERNS_COLLECTION)
        .document(pattern.id)
        .collection(Constants.GRID_COLLECTION)
    private val patternRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.USERS_COLLECTION)
        .document(uid)
        .collection(Constants.PROJECTS_COLLECTION)
        .document(project.id)
        .collection(Constants.PATTERNS_COLLECTION)

    var color: Int = Color.BLACK
    var stitch: Int? = null

    private lateinit var listenerRegistration: ListenerRegistration

    fun addSnapshotListener() {
        listenerRegistration = gridRef
            .orderBy(Grid.CREATED_KEY, Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, e ->
                if (e != null) {
                    Log.w(Constants.TAG, "listen error", e)
                } else {
                    processSnapshotChanges(querySnapshot!!)
                }
            }
    }

    private fun processSnapshotChanges(querySnapshot: QuerySnapshot) {
        // Snapshots has documents and documentChanges which are flagged by type,
        // so we can handle C,U,D differently.
        for (documentChange in querySnapshot.documentChanges) {
            val grid = Grid.fromSnapshot(documentChange.document)
            when (documentChange.type) {
                DocumentChange.Type.ADDED -> {
                    Log.d(Constants.TAG, "Adding $grid")
                    rectangles.add(0, grid)
                    notifyItemInserted(0)
                }
                DocumentChange.Type.REMOVED -> {
                    Log.d(Constants.TAG, "Removing $grid")
                    val index = rectangles.indexOfFirst { it.id == grid.id }
                    rectangles.removeAt(index)
                    notifyItemRemoved(index)
                }
                DocumentChange.Type.MODIFIED -> {
                    Log.d(Constants.TAG, "Modifying $grid")
                    val index = rectangles.indexOfFirst { it.id == grid.id }
                    rectangles[index] = grid
                    notifyItemChanged(index)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, index: Int): CreatePatternViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.create_pattern_grid_view, parent, false)
        return CreatePatternViewHolder(view, this)
    }

    override fun onBindViewHolder(
        listViewHolder: CreatePatternViewHolder,
        index: Int
    ) {
        listViewHolder.bind(rectangles[index])
    }

    override fun getItemCount() = rectangles.size


    fun add(grid: Grid) {
        gridRef.add(grid)
    }

    fun removePattern() {
        patternRef.document(pattern.id).delete()
    }

    private fun remove(position: Int) {
        gridRef.document(rectangles[position].id).delete()
    }

    fun updateColor(position: Int) {
        rectangles[position].color = this.color!!
        if(rectangles[position].image != null) {
            rectangles[position].image = null
        }
        gridRef.document(rectangles[position].id).set(rectangles[position])
    }

    fun updateStitch(position: Int) {
        rectangles[position].image = this.stitch!!
        rectangles[position].color = Color.WHITE
        gridRef.document(rectangles[position].id).set(rectangles[position])
    }
}