package edu.rosehulman.samuelma.letsgetknotty.projectlist

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.opengl.Visibility
import android.os.AsyncTask
import android.os.Handler
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import edu.rosehulman.samuelma.letsgetknotty.BitmapUtils
import edu.rosehulman.samuelma.letsgetknotty.project.Project
import edu.rosehulman.samuelma.letsgetknotty.Constants
import edu.rosehulman.samuelma.letsgetknotty.R
import kotlinx.android.synthetic.main.dialog_add_edit_image.view.*
import java.io.ByteArrayOutputStream
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.random.Random.Default.nextLong

class ProjectListAdapter(val context: Context, uid: String, var listener: OnProjectSelectedListener?) : RecyclerView.Adapter<ProjectListViewHolder>() {
    private val projects = ArrayList<Project>()
    private val projectsRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.USERS_COLLECTION)
        .document(uid)
        .collection(Constants.PROJECTS_COLLECTION)
    private lateinit var listenerRegistration: ListenerRegistration
    var image : String = ""

    private val storageRef: StorageReference = FirebaseStorage
        .getInstance()
        .reference
        .child("images")

    fun addSnapshotListener() {
        listenerRegistration = projectsRef
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
                    projects.add(0, project)
                    notifyItemInserted(0)
                }
                DocumentChange.Type.REMOVED -> {
                    Log.d(Constants.TAG, "Removing $project")
                    val index = projects.indexOfFirst { it.id == project.id }
                    projects.removeAt(index)
                    notifyItemRemoved(index)
                }
                DocumentChange.Type.MODIFIED -> {
                    Log.d(Constants.TAG, "Modifying $project")
                    val index = projects.indexOfFirst { it.id == project.id }
                    project.lastTouched = Timestamp.now()
                    projects[index] = project
                    notifyItemChanged(index)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, index: Int): ProjectListViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.project_list_grid_card_view, parent, false)
        return ProjectListViewHolder(view, this)
    }

    override fun onBindViewHolder(
        listViewHolder: ProjectListViewHolder,
        index: Int
    ) {
        listViewHolder.bind(projects[index])
    }

    override fun getItemCount() = projects.size

    @SuppressLint("InflateParams")
    fun showAddEditDialog(position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle((if (position < 0) "Add Project Name and Thumbnail" else "Edit Project Name and Thumbnail"))
        val view = LayoutInflater.from(context).inflate(
            R.layout.dialog_add_edit_image, null, false
        )
        builder.setView(view)
        if (position >= 0) {
            view.dialog_edit_text_name.setText(projects[position].name)
            image = projects[position].imageUrl
        }
        val addImage : Button = view.add_image_button
        addImage.setOnClickListener {
            listener?.showPictureDialog()
        }


        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            if(image == "") {
                image = "https://firebasestorage.googleapis.com/v0/b/let-s-get-knotty-296d2.appspot.com/o/images%2F705735745717022433?alt=media&token=d28563b8-8adb-4895-80a6-08de1827f186"
            }
            val name = view.dialog_edit_text_name.text.toString()
            Log.d(Constants.TAG, "Image: $image")
            val handler = Handler()
            handler.postDelayed({
                if (position < 0) {
                    add(Project(name, image))
                } else {
                    edit(position, name, image)
                }
            },2000)


        }
        builder.setNegativeButton(android.R.string.cancel, null)
        builder.setNeutralButton("Remove") { _, _ ->
            remove(position)
        }
        builder.show()
    }

    private fun add(project: Project) {
        projectsRef.add(project)
    }

    private fun edit(position: Int, name : String, image : String) {
        projects[position].name = name
        projects[position].imageUrl = image
        projectsRef.document(projects[position].id).set(projects[position])
    }



    private fun remove(position: Int) {
        val image = projects[position].imageUrl
        if(image != "https://firebasestorage.googleapis.com/v0/b/let-s-get-knotty-296d2.appspot.com/o/images%2F705735745717022433?alt=media&token=d28563b8-8adb-4895-80a6-08de1827f186"){
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(projects[position].imageUrl)
            ref.delete().addOnFailureListener {
                Log.d(Constants.TAG, "deleted image url: ${projects[position].imageUrl}")
            }
        }
        projectsRef.document(projects[position].id).delete()
    }

    fun selectProject(position: Int) {
        listener?.onProjectSelected(projects[position])
    }

    interface OnProjectSelectedListener {
        fun onProjectSelected(pic: Project)
        fun showPictureDialog()
    }

    fun addImage(localPath: String) {
        // TODO: You'll want to wait to add this to Firetore until after you have a Storage download URL.
        // Move this line of code there.
        //thumbnailRef.add(Thumbnail(localPath))
        ImageRescaleTask(localPath).execute()
    }


    // Could save a smaller version to Storage to save time on the network.
    // But if too small, recognition accuracy can suffer.
    inner class ImageRescaleTask(val localPath: String) : AsyncTask<Void, Void, Bitmap>() {
        override fun doInBackground(vararg p0: Void?): Bitmap? {
            // Reduces length and width by a factor (currently 2).
            val ratio = 2
            return BitmapUtils.rotateAndScaleByRatio(context, localPath, ratio)
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            // TODO: Write and call a new storageAdd() method with the path and bitmap
            // that uses Firebase storage.
            // https://firebase.google.com/docs/storage/android/upload-files
            storageAdd(localPath, bitmap)
        }
    }
    private fun storageAdd(localPath: String, bitmap: Bitmap?) {
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val id = abs(nextLong()).toString()
        var uploadTask = storageRef.child(id).putBytes(data)
        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> {
                task ->
            if(!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation storageRef.child(id).downloadUrl
        }).addOnCompleteListener { task ->
            if(task.isSuccessful) {
                val downloadUri = task.result
                image = downloadUri.toString()
                Log.d(Constants.TAG, "${downloadUri.toString()}")
            } else {
                // handle failures
            }
        }
    }

    private fun removeImageFromStorageOnly(position: Int) {
        val image = projects[position].imageUrl
        if(image != "https://firebasestorage.googleapis.com/v0/b/let-s-get-knotty-296d2.appspot.com/o/images%2F705735745717022433?alt=media&token=d28563b8-8adb-4895-80a6-08de1827f186"){
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(projects[position].imageUrl)
            ref.delete().addOnFailureListener {
                Log.d(Constants.TAG, "deleted image url: ${projects[position].imageUrl}")
            }
        }

    }


}