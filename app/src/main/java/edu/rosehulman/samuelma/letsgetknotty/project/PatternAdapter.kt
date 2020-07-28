package edu.rosehulman.samuelma.letsgetknotty.project

import android.app.AlertDialog
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.firestore.*
import edu.rosehulman.samuelma.letsgetknotty.Constants
import edu.rosehulman.samuelma.letsgetknotty.R
import edu.rosehulman.samuelma.letsgetknotty.pattern.Pattern
import kotlinx.android.synthetic.main.dialog_add_pattern.view.*


class PatternAdapter(val context: Context, uid: String, projectId: String, var listener: OnPatternSelectedListener?) : RecyclerView.Adapter<PatternViewHolder>() {
    private val patterns = ArrayList<Pattern>()
    private val patternsRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.USERS_COLLECTION)
        .document(uid)
        .collection(Constants.PROJECTS_COLLECTION)
        .document(projectId)
        .collection(Constants.PATTERNS_COLLECTION)
    private lateinit var listenerRegistration: ListenerRegistration
    private var gauge : String = ""

    fun addSnapshotListener() {
        listenerRegistration = patternsRef
            .orderBy(Project.LAST_TOUCHED_KEY, Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, e ->
                if (e != null) {
                    Log.w(Constants.TAG, "listen error", e)
                } else {
                    processSnapshotChanges(querySnapshot!!)
                }
            }

        Log.d(Constants.TAG, "added snapshot listner to pattern adapter")
    }

    private fun processSnapshotChanges(querySnapshot: QuerySnapshot) {
        // Snapshots has documents and documentChanges which are flagged by type,
        // so we can handle C,U,D differently.
        for (documentChange in querySnapshot.documentChanges) {
            val pattern = Pattern.fromSnapshot(documentChange.document)
            when (documentChange.type) {
                DocumentChange.Type.ADDED -> {
                    Log.d(Constants.TAG, "Adding $pattern")
                    patterns.add(0, pattern)
                    notifyItemInserted(0)
                }
                DocumentChange.Type.REMOVED -> {
                    Log.d(Constants.TAG, "Removing $pattern")
                    val index = patterns.indexOfFirst { it.id == pattern.id }
                    patterns.removeAt(index)
                    notifyItemRemoved(index)
                }
                DocumentChange.Type.MODIFIED -> {
                    Log.d(Constants.TAG, "Modifying $pattern")
                    val index = patterns.indexOfFirst { it.id == pattern.id }
                    patterns[index] = pattern
                    notifyItemChanged(index)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, index: Int): PatternViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.pattern_grid_view, parent, false)
        return PatternViewHolder(view, this)
    }

    override fun getItemCount() = patterns.size

    fun showAddEditDialog(position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle((if (position < 0) "Add Pattern" else "Edit Pattern"))
        val view = LayoutInflater.from(context).inflate(
            R.layout.dialog_add_pattern, null, false
        )
        builder.setView(view)
        if (position >= 0) {
            view.pattern_name_edit_text.setText(patterns[position].name)
            view.number_of_rows_in_repeat_edit_text.setText(patterns[position].rowsInRepeat.toString())
            view.number_of_stitches_in_repeat_edit_text.setText(patterns[position]?.stitchesInRepeat.toString())
            view.total_number_of_rows_edit_text.setText(patterns[position]?.totalRows.toString())
            view.total_number_of_stitches_edit_text.setText(patterns[position]?.totalStitches.toString())
        }

        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            val name = view.pattern_name_edit_text.text.toString()
            var rowsInRepeat = 0
            if( view.number_of_rows_in_repeat_edit_text.text.toString() != "") {
                rowsInRepeat = view.number_of_rows_in_repeat_edit_text.text.toString().toInt()
            }
            var stitchesInRepeat = 0
            if(view.number_of_stitches_in_repeat_edit_text.text.toString() != "") {
              stitchesInRepeat = view.number_of_stitches_in_repeat_edit_text.text.toString().toInt()
            }
            var totalRows = 0
            if(view.total_number_of_rows_edit_text.text.toString() != "") {
                totalRows = view.total_number_of_rows_edit_text.text.toString().toInt()
            }
            var totalStitches = 0
            if(view.total_number_of_stitches_edit_text.text.toString() != "") {
                totalStitches = view.total_number_of_stitches_edit_text.text.toString().toInt()
            }
            val pattern = Pattern(name,"",rowsInRepeat,stitchesInRepeat,totalRows,totalStitches,"",false)
            if(position < 0) {
                pattern.imageUrl = "https://cdn.shopify.com/s/files/1/0032/0025/4021/products/ilia_01_182d4112-7a3f-4057-807e-7f9cc68bfe79_480x480.jpg?v=1571710489"
                add(pattern)
            } else {
                edit(position, name, rowsInRepeat,stitchesInRepeat,totalRows,totalStitches)
            }
        }
        builder.setNegativeButton(android.R.string.cancel, null)
        builder.setNeutralButton("Remove") { _, _ ->
            remove(position)
        }
        builder.show()
    }

    fun add(pattern: Pattern) {
        patternsRef.add(pattern)
    }

    private fun edit(position: Int, name: String, rowsInRepeat: Int, stitchesInRepeat: Int, totalRows: Int, totalStitches: Int) {
        patterns[position].name = name
        patterns[position].rowsInRepeat = rowsInRepeat
        patterns[position].stitchesInRepeat = stitchesInRepeat
        patterns[position].totalRows = totalRows
        patterns[position].totalStitches = totalStitches
        patternsRef.document(patterns[position].id).set(patterns[position])
    }

//    fun addGuage(pattern : Pattern, rowCount : Int, width : Int, height : Int) {
//        val position : Int= patterns.indexOf(pattern)
//        val horizontalGauge = (patterns[position].totalStitches / width) * 4
//        val verticalGauge : Int = (rowCount / height) * 4
//        val gauge : String = "$horizontalGauge in x $verticalGauge in"
//        patterns[position].gauge = gauge
//        patternsRef.document(patterns[position].id).set(patterns[position])
//    }

    private fun remove(position: Int) {
        patternsRef.document(patterns[position].id).delete()
    }

    fun selectPattern(position: Int) {
        listener?.onPatternSelected(patterns[position])
    }

    override fun onBindViewHolder(holder: PatternViewHolder, position: Int) {
        holder.bind(patterns[position])
    }

    interface OnPatternSelectedListener {
        fun onPatternSelected(pattern: Pattern)
    }
}