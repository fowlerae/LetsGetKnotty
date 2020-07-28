package edu.rosehulman.samuelma.letsgetknotty.rowCounter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import edu.rosehulman.samuelma.letsgetknotty.Constants
import edu.rosehulman.samuelma.letsgetknotty.R


class RowCounterAdapter(val context: Context, uid: String, projectId: String) : RecyclerView.Adapter<RowCounterViewHolder>() {

    private val rowCounters = ArrayList<RowCounter>()
    private val rowCounterRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.USERS_COLLECTION)
        .document(uid)
        .collection(Constants.PROJECTS_COLLECTION)
        .document(projectId)
        .collection(Constants.ROW_COUNTER_COLLECTION)

    private lateinit var listenerRegistration: ListenerRegistration

    fun addSnapshotListener() {
        listenerRegistration = rowCounterRef
            .orderBy(RowCounter.LAST_TOUCHED_KEY, Query.Direction.ASCENDING)
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
            val rowCounter = RowCounter.fromSnapshot(documentChange.document)
            when (documentChange.type) {
                DocumentChange.Type.ADDED -> {
                    Log.d(Constants.TAG, "Adding $rowCounter")
                    rowCounters.add(0, rowCounter)
                    notifyItemInserted(0)
                }
                DocumentChange.Type.REMOVED -> {
                    Log.d(Constants.TAG, "Removing $rowCounter")
                    val index = rowCounters.indexOfFirst { it.id == rowCounter.id }
                    rowCounters.removeAt(index)
                    notifyItemRemoved(index)
                }
                DocumentChange.Type.MODIFIED -> {
                    Log.d(Constants.TAG, "Modifying $rowCounter")
                    val index = rowCounters.indexOfFirst { it.id == rowCounter.id }
                    rowCounters[index] = rowCounter
                    notifyItemChanged(index)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowCounterViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_counter_card_view, parent, false)
        return RowCounterViewHolder(view)
    }

    override fun getItemCount() = rowCounters.size

    override fun onBindViewHolder(holder: RowCounterViewHolder, position: Int) {
        holder.bind(rowCounters[position])
    }

    fun add(rowCounter: RowCounter) {
        rowCounterRef.add(rowCounter)
    }

    private fun remove(position: Int) {
        rowCounterRef.document(rowCounters[position].id).delete()
    }
}