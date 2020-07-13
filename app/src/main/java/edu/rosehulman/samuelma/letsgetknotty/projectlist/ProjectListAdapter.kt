package edu.rosehulman.samuelma.letsgetknotty.projectlist

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.firestore.*
import edu.rosehulman.samuelma.letsgetknotty.project.Project
import edu.rosehulman.samuelma.letsgetknotty.Constants
import edu.rosehulman.samuelma.letsgetknotty.R
import kotlinx.android.synthetic.main.dialog_add_edit_image.view.*

class ProjectListAdapter(val context: Context, uid: String, var listener: ProjectListFragment.OnProjectSelectedListener?) : RecyclerView.Adapter<ProjectListViewHolder>() {
    private val pictures = ArrayList<Project>()
    private val picturesRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.USERS_COLLECTION)
        .document(uid)
        .collection(Constants.PROJECTS_COLLECTION)
    private lateinit var listenerRegistration: ListenerRegistration

    fun addSnapshotListener() {
        listenerRegistration = picturesRef
            .orderBy(Project.LAST_TOUCHED_KEY, Query.Direction.ASCENDING)
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
            val project = Project.fromSnapshot(documentChange.document)
            when (documentChange.type) {
                DocumentChange.Type.ADDED -> {
                    Log.d(Constants.TAG, "Adding $project")
                    pictures.add(0, project)
                    notifyItemInserted(0)
                }
                DocumentChange.Type.REMOVED -> {
                    Log.d(Constants.TAG, "Removing $project")
                    val index = pictures.indexOfFirst { it.id == project.id }
                    pictures.removeAt(index)
                    notifyItemRemoved(index)
                }
                DocumentChange.Type.MODIFIED -> {
                    Log.d(Constants.TAG, "Modifying $project")
                    val index = pictures.indexOfFirst { it.id == project.id }
                    pictures[index] = project
                    notifyItemChanged(index)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, index: Int): ProjectListViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.grid_view, parent, false)
        return ProjectListViewHolder(view, this)
    }

    override fun onBindViewHolder(
        listViewHolder: ProjectListViewHolder,
        index: Int
    ) {
        listViewHolder.bind(pictures[index])
    }

    override fun getItemCount() = pictures.size

    @SuppressLint("InflateParams")
    fun showAddEditDialog(position: Int = -1) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Add a quote")
        val view = LayoutInflater.from(context).inflate(
            R.layout.dialog_add_edit_image, null, false
        )
        builder.setView(view)
        builder.setIcon(android.R.drawable.ic_input_add)
        if (position >= 0) {
            view.dialog_edit_text_name.setText(pictures[position].name)
            view.dialog_edit_text_image.setText(pictures[position].imageUrl)
        }

        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            val quote = view.dialog_edit_text_name.text.toString()
            val movie = view.dialog_edit_text_image.text.toString()
            if (position < 0) {
                add(Project(quote, movie))
            } else {
                edit(position, quote, movie)
            }

        }
        builder.setNegativeButton(android.R.string.cancel, null)
        builder.setNeutralButton("Remove") { _, _ ->
            remove(position)
        }
        builder.show()
    }

    private fun add(project: Project) {
        picturesRef.add(project)
    }

    private fun edit(position: Int, quote: String, movie: String) {
        pictures[position].name = quote
        pictures[position].imageUrl = movie
        picturesRef.document(pictures[position].id).set(pictures[position])
    }

    private fun remove(position: Int) {
        picturesRef.document(pictures[position].id).delete()
    }

    fun selectMovieQuote(position: Int) {
//        val mq =pictures[position]
//        mq.showDark = !mq.showDark
//        picturesRef.document(mq.id).set(mq)
        listener?.onProjectSelected(pictures[position])
    }
}