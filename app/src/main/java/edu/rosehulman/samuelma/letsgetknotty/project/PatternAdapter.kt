package edu.rosehulman.fowlerae.letsgetknotty.project

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.firestore.*
import edu.rosehulman.samuelma.letsgetknotty.Constants
import edu.rosehulman.samuelma.letsgetknotty.R
import edu.rosehulman.samuelma.letsgetknotty.pattern.Pattern
import edu.rosehulman.samuelma.letsgetknotty.project.PatternViewHolder
import edu.rosehulman.samuelma.letsgetknotty.project.Project
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
        val builder= AlertDialog.Builder(context)
        builder.setTitle((if (position < 0) "Add Pattern" else "Edit Pattern"))
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_pattern,null, false)
        builder.setView(view)
        if(position >= 0) {
            view.pattern_name_edit_text.setText(patterns[position]?.name)
            view.number_of_rows_in_repeat_edit_text.setText(patterns[position]?.rowsInRepeat)
            view.number_of_stitches_in_repeat_edit_text.setText(patterns[position]?.stitchesInRepeat)
            view.total_number_of_rows_edit_text.setText(patterns[position]?.totalRows)
            view.total_number_of_stitches_edit_text.setText(patterns[position]?.totalStitches)
        }
        builder.setPositiveButton(android.R.string.ok) {_ : DialogInterface?, _ : Int ->
            val name = view.pattern_name_edit_text.text.toString()
            val rowsInRepeat = view.number_of_rows_in_repeat_edit_text.text.toString().toInt()
            val stitchesInRepeat = view.number_of_stitches_in_repeat_edit_text.text.toString().toInt()
            val totalRows = view.total_number_of_rows_edit_text.text.toString().toInt()
            val totalStitches = view.total_number_of_stitches_edit_text.text.toString().toInt()
            val pattern = Pattern(name,"",rowsInRepeat,stitchesInRepeat,totalRows,totalStitches,false)
            if(position < 0) {
                add(pattern)
            } else {
                edit(position, name, rowsInRepeat,stitchesInRepeat,totalRows,totalStitches)
            }

        }
        builder.setNeutralButton(android.R.string.cancel, null)
        builder.create().show()
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