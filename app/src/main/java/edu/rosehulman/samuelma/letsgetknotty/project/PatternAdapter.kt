package edu.rosehulman.samuelma.letsgetknotty.project

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Handler
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
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
import edu.rosehulman.samuelma.letsgetknotty.Constants
import edu.rosehulman.samuelma.letsgetknotty.R
import edu.rosehulman.samuelma.letsgetknotty.pattern.Pattern
import kotlinx.android.synthetic.main.dialog_add_pattern.view.*
import java.io.ByteArrayOutputStream
import kotlin.math.abs
import kotlin.random.Random


class PatternAdapter(
    val context: Context,
    uid: String,
    projectId: String,
    private var listener: OnPatternSelectedListener?
) : RecyclerView.Adapter<PatternViewHolder>() {
    private val patterns = ArrayList<Pattern>()
    private val patternsRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.USERS_COLLECTION)
        .document(uid)
        .collection(Constants.PROJECTS_COLLECTION)
        .document(projectId)
        .collection(Constants.PATTERNS_COLLECTION)
    private lateinit var listenerRegistration: ListenerRegistration
    var image : String = ""

    private val storageRef: StorageReference = FirebaseStorage
        .getInstance()
        .reference
        .child("images")

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
                    pattern.lastTouched = Timestamp.now()
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
        val addImageButton : Button = view.findViewById(R.id.add_pattern_image_button)
        if (position >= 0) {
            addImageButton.setOnClickListener {
                removeImageFromStorageOnly(position)
                listener!!.showPictureDialog()
            }
            view.pattern_name_edit_text.setText(patterns[position].name)
            view.number_of_rows_in_repeat_edit_text.setText(patterns[position].rowsInRepeat.toString())
            view.number_of_stitches_in_repeat_edit_text.setText(patterns[position].stitchesInRepeat.toString())
            view.total_number_of_rows_edit_text.setText(patterns[position].totalRows.toString())
            view.total_number_of_stitches_edit_text.setText(patterns[position].totalStitches.toString())
            image = image
        } else {
            addImageButton.setOnClickListener {
                listener!!.showPictureDialog()
            }
        }

        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            val name = view.pattern_name_edit_text.text.toString()
            var rowsInRepeat = 10
            if (view.number_of_rows_in_repeat_edit_text.text.toString() != "") {
                rowsInRepeat = view.number_of_rows_in_repeat_edit_text.text.toString().toInt()
            }
            var stitchesInRepeat = 10
            if (view.number_of_stitches_in_repeat_edit_text.text.toString() != "") {
                stitchesInRepeat =
                    view.number_of_stitches_in_repeat_edit_text.text.toString().toInt()
            }
            var totalRows = 10
            if (view.total_number_of_rows_edit_text.text.toString() != "") {
                totalRows = view.total_number_of_rows_edit_text.text.toString().toInt()
            }
            var totalStitches = 10
            if (view.total_number_of_stitches_edit_text.text.toString() != "") {
                totalStitches = view.total_number_of_stitches_edit_text.text.toString().toInt()
            }
            val pattern = Pattern(name, image, rowsInRepeat, stitchesInRepeat, totalRows, totalStitches, "", false)
            if(pattern.imageUrl == "") {
                pattern.imageUrl = "https://firebasestorage.googleapis.com/v0/b/let-s-get-knotty-296d2.appspot.com/o/images%2F705735745717022433?alt=media&token=d28563b8-8adb-4895-80a6-08de1827f186"
            }
            if(position < 0) {
                add(pattern)
                Log.d(Constants.TAG, pattern.toString())
                val handler = Handler()
                handler.postDelayed({
                    // do something after 500ms
                    // had to add delay as it was adding after the grid was created which led to adding the grid
                    // to the wrong pattern
                    listener?.onAddPatternSelected(patterns[0])
                }, 500)
            } else {
                edit(pattern, position)
                Log.d(Constants.TAG, pattern.toString())
                val handler = Handler()
                handler.postDelayed({
                    // do something after 500ms
                    // had to add delay as it was adding after the grid was created which led to adding the grid
                    // to the wrong pattern
                    listener?.onAddPatternSelected(patterns[position])
                }, 500)
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

    private fun edit(pattern: Pattern, position: Int) {
        patternsRef.document(patterns[position].id).set(pattern)
    }


    private fun remove(position: Int) {
        val image = patterns[position].imageUrl
        if(image != "https://firebasestorage.googleapis.com/v0/b/let-s-get-knotty-296d2.appspot.com/o/images%2F705735745717022433?alt=media&token=d28563b8-8adb-4895-80a6-08de1827f186"){
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(patterns[position].imageUrl)
            ref.delete().addOnFailureListener {
                Log.d(Constants.TAG, "deleted image url: ${patterns[position].imageUrl}")
            }
        }
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
        fun onAddPatternSelected(pattern: Pattern)
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
        val id = abs(Random.nextLong()).toString()
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
        val image = patterns[position].imageUrl
        if(image != "https://firebasestorage.googleapis.com/v0/b/let-s-get-knotty-296d2.appspot.com/o/images%2F705735745717022433?alt=media&token=d28563b8-8adb-4895-80a6-08de1827f186"){
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(patterns[position].imageUrl)
            ref.delete().addOnFailureListener {
                Log.d(Constants.TAG, "deleted image url: ${patterns[position].imageUrl}")
            }
        }

    }
}