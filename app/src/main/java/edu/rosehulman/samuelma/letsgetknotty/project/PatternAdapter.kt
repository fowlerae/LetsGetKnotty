package edu.rosehulman.fowlerae.letsgetknotty.project

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.firestore.*
import edu.rosehulman.samuelma.letsgetknotty.Constants
import edu.rosehulman.samuelma.letsgetknotty.R
import edu.rosehulman.samuelma.letsgetknotty.pattern.Pattern
import edu.rosehulman.samuelma.letsgetknotty.pattern.PatternFragment
import edu.rosehulman.samuelma.letsgetknotty.project.PatternViewHolder
import edu.rosehulman.samuelma.letsgetknotty.project.Project


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
        val view = LayoutInflater.from(context).inflate(R.layout.grid_view, parent, false)
        return PatternViewHolder(view, this)
    }

    override fun getItemCount() = patterns.size


    fun add(pattern: Pattern) {
        patternsRef.add(pattern)
    }

    private fun edit(position: Int, quote: String, movie: String) {
        patterns[position].name = quote
        patterns[position].imageUrl = movie
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