package edu.rosehulman.samuelma.letsgetknotty.project

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import edu.rosehulman.samuelma.letsgetknotty.Constants
import edu.rosehulman.samuelma.letsgetknotty.R
import edu.rosehulman.samuelma.letsgetknotty.createPattern.CreatePatternFragment
import edu.rosehulman.samuelma.letsgetknotty.note.Note
import edu.rosehulman.samuelma.letsgetknotty.note.NoteAdapter
import edu.rosehulman.samuelma.letsgetknotty.pattern.Pattern
import edu.rosehulman.samuelma.letsgetknotty.pattern.PatternFragment
import edu.rosehulman.samuelma.letsgetknotty.rowCounter.RowCounter
import edu.rosehulman.samuelma.letsgetknotty.rowCounter.RowCounterAdapter
import edu.rosehulman.samuelma.letsgetknotty.rowCounter.WidgetService
import kotlinx.android.synthetic.main.dialog_add_gauge.view.*
import kotlinx.android.synthetic.main.dialog_add_note.view.*
import kotlinx.android.synthetic.main.dialog_add_row_counter.view.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


private const val ARG_PROJECT = "project"
private const val ARG_UID = "UID"
private const val RC_TAKE_PICTURE = 1
private const val RC_CHOOSE_PICTURE = 2

class ProjectFragment : Fragment(), PatternAdapter.OnPatternSelectedListener{
    private lateinit var project: Project
    private lateinit var patternAdapter : PatternAdapter
    private lateinit var noteAdapter : NoteAdapter
    private lateinit var rowCounterAdapter : RowCounterAdapter
    private lateinit var listener: PatternAdapter.OnPatternSelectedListener
    private var uid : String = ""
    private lateinit var projectRef : DocumentReference
    private lateinit var root : View
    private var currentPhotoPath = ""

    companion object {
        @JvmStatic
        fun newInstance(pro: Project, u: String?) =
            ProjectFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PROJECT, pro)
                    if (u != null) {
                        uid = u
                    }
                    project = pro
                    projectRef = FirebaseFirestore
                        .getInstance()
                        .collection(Constants.USERS_COLLECTION)
                        .document(uid)
                        .collection(Constants.PROJECTS_COLLECTION)
                        .document(project.id)

                }
            }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        project = arguments?.getParcelable(ARG_PROJECT)!!
        arguments?.let {
            project = it.getParcelable(ARG_PROJECT)!!
        }
        projectRef = uid.let {
            project.id.let { it1 ->
                FirebaseFirestore
                    .getInstance()
                    .collection(Constants.USERS_COLLECTION)
                    .document(it)
                    .collection(Constants.PROJECTS_COLLECTION)
                    .document(it1)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_project_view, container, false)

        val textView : TextView = root.findViewById(R.id.project_title_text_view)
        textView.text = project.name

        // pattern recycler
        val patternRecyclerView : RecyclerView = root.findViewById(R.id.pattern_recycler_view)
        patternAdapter = PatternAdapter(context!!, uid, project.id, listener)
        patternRecyclerView.adapter = patternAdapter
        patternAdapter.addSnapshotListener()
        patternRecyclerView.layoutManager = LinearLayoutManager(context,RecyclerView.HORIZONTAL ,false)

        // note recycler

        val noteRecyclerView : RecyclerView = root.findViewById(R.id.note_recycler_view)
        noteAdapter = NoteAdapter(context!!, uid, project.id)
        noteRecyclerView.adapter = noteAdapter
        noteAdapter.addSnapshotListener()
        noteRecyclerView.layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL ,false)

        // row counter recycler

        val rowCounterRecyclerView : RecyclerView = root.findViewById(R.id.row_counter_recycler_view)
        rowCounterAdapter = RowCounterAdapter(context!!, uid, project.id)
        rowCounterRecyclerView.adapter = rowCounterAdapter
        rowCounterAdapter.addSnapshotListener()
        rowCounterRecyclerView.layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL ,false)


        val addPattern = root.findViewById<TextView>(R.id.add_pattern_button)
        addPattern.setOnClickListener {
            patternAdapter.showAddEditDialog(-1)
        }
        val addCounter = root.findViewById<TextView>(R.id.add_row_counter_button)
        addCounter.setOnClickListener {
            showAddCounter()
        }
        val addGauge : TextView = root.findViewById(R.id.add_gauge_button)
        addGauge.setOnClickListener {
            showAddGauge()
        }

        val addNote : TextView = root.findViewById(R.id.add_note_button)
        addNote.setOnClickListener {
            showAddNote()
        }


        projectRef.addSnapshotListener { snapshot : DocumentSnapshot?, exception : FirebaseFirestoreException? ->
            if(exception != null) {
                return@addSnapshotListener
            }
            val gaugeTextView = root.findViewById<TextView>(R.id.project_gauge)
            gaugeTextView.text = (snapshot?.get("gauge") ?: "") as String
        }

        return root
    }

    @SuppressLint("InflateParams")
    fun showAddCounter() {
        val builder = context?.let { AlertDialog.Builder(it) }
        if (builder != null) {
            builder.setTitle("Add Row Counter")
            val view = LayoutInflater.from(context).inflate(
                R.layout.dialog_add_row_counter, null, false
            )
            builder.setView(view)
            builder.setPositiveButton(android.R.string.ok) { _, _ ->
                val name = "${view.row_counter_name_edit_text.text} Row Counter"
                val startingValue = view.starting_value_edit_text.text.toString()
                var num = 0
                if(startingValue != "") {
                    num = startingValue.toInt()
                }
                val rowCounter =
                    RowCounter(
                        name,
                        num,
                        0
                    )
                rowCounterAdapter.add(rowCounter)
            }
            builder.setNegativeButton(android.R.string.cancel, null)
            builder.show()
        }

    }


    @SuppressLint("InflateParams")
    fun showAddNote() {
        val builder = context?.let { AlertDialog.Builder(it) }
        if (builder != null) {
            builder.setTitle("Add Note")
            val view = LayoutInflater.from(context).inflate(
                R.layout.dialog_add_note, null, false
            )
            builder.setView(view)
            builder.setPositiveButton(android.R.string.ok) { _, _ ->
                val description = view.note_description_edit_text.text.toString()
                val note = Note(description)
                noteAdapter.add(note)
            }
            builder.setNegativeButton(android.R.string.cancel, null)
            builder.show()
        }

    }


    @SuppressLint("InflateParams")
    fun showAddGauge() {
        val builder = context?.let { AlertDialog.Builder(it) }
        if (builder != null) {
            builder.setTitle("Add Gauge")
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_gauge, null, false)
            builder.setView(view)
            builder.setPositiveButton(android.R.string.ok) { _, _ ->
                val rowsCountString = view.rows_completed_edit_text.text.toString()
                val widthString = view.project_width_edit_text.text.toString()
                val heightString : String = view.project_height_edit_text.text.toString()
                val stitchString : String = view.project_stitch_count_edit_text.text.toString()
                var rowCount = 0
                if(rowsCountString != "") {
                     rowCount = rowsCountString.toInt()
                }
                var width = 0
                if(widthString != "") {
                    width = widthString.toInt()
                }
                var height = 0
                if(heightString != "") {
                    height = heightString.toInt()
                }
                var stitchCount =0
                if(stitchString != "") {
                    stitchCount = stitchString.toInt()
                }
                addGuage(stitchCount, rowCount,width,height)
            }
            builder.setNegativeButton(android.R.string.cancel, null)
            builder.show()
        }

    }


    override fun onPatternSelected(pattern: Pattern) {
        pattern.lastTouched = Timestamp.now()
        val fragment = PatternFragment.newInstance(uid, pattern, project)
        val fm = fragmentManager
        val ft = fm?.beginTransaction()
        if (ft != null) {
            ft.replace(R.id.fragment_container, fragment)
            ft.addToBackStack("add")
            Log.d(Constants.TAG, "Trying to display pattern")
            ft.commit()
        }
    }

    override fun onAddPatternSelected(pattern: Pattern) {
        Log.d(Constants.TAG, "Pattern: ${pattern.id}, pattern name : ${pattern.name}")
        val fragment = project.let {
            uid.let { it1 ->
                CreatePatternFragment.newInstance(
                    it1,pattern, it)
            }
        }
        val fm = fragmentManager
        val ft = fm?.beginTransaction()
        if (ft != null) {
            ft.replace(R.id.fragment_container, fragment)
            ft.addToBackStack("add")
            Log.d(Constants.TAG, "Trying to add create pattern fragment")
            ft.commit()
        }
    }

    private fun addGuage(stitchCount : Int, rowCount : Int, width : Int, height : Int) {
        val horizontalGauge = (stitchCount/ width) * 4
        val verticalGauge : Int = (rowCount / height) * 4
        val gauge = "$horizontalGauge sts x $verticalGauge rows"
        val map = mapOf("gauge" to gauge)
        projectRef.update(map)
        val gaugeTextView = root.findViewById<TextView>(R.id.project_gauge)
        gaugeTextView.text = gauge
    }

    override fun showPictureDialog() {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Choose a photo source")
        builder.setMessage("Would you like to take a new picture?\nOr choose an existing one?")
        builder.setPositiveButton("Take Picture") { _, _ ->
            launchCameraIntent()
        }

        builder.setNegativeButton("Choose Picture") { _, _ ->
            launchChooseIntent()
        }
        builder.create().show()
    }

    // Everything camera- and storage-related is from
    // https://developer.android.com/training/camera/photobasics
    private fun launchCameraIntent() {
        Log.d(Constants.TAG, "launchCameraIntent")
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    // authority declared in manifest
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context!!,
                        "edu.rosehulman.samuelma.letsgetknotty",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, RC_TAKE_PICTURE)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        Log.d(Constants.TAG, "createImageFile")
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun launchChooseIntent() {
        Log.d(Constants.TAG, "launchChooseIntent")
        // https://developer.android.com/guide/topics/providers/document-provider
        val choosePictureIntent = Intent(
            Intent.ACTION_OPEN_DOCUMENT,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        choosePictureIntent.addCategory(Intent.CATEGORY_OPENABLE)
        choosePictureIntent.type = "image/*"
        startActivityForResult(choosePictureIntent, RC_CHOOSE_PICTURE)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(Constants.TAG, "onActivityResult")
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                RC_TAKE_PICTURE -> {
                    sendCameraPhotoToAdapter()
                }
                RC_CHOOSE_PICTURE -> {
                    sendGalleryPhotoToAdapter(data)
                }
            }
        }
    }

    private fun sendCameraPhotoToAdapter() {
        Log.d(Constants.TAG, "sendCameraPhotoToAdapter")
        addPhotoToGallery()
        Log.d(Constants.TAG, "Sending to adapter this photo: $currentPhotoPath")
        patternAdapter.addImage(currentPhotoPath)
    }

    private fun sendGalleryPhotoToAdapter(data: Intent?) {
        Log.d(Constants.TAG, "sendGalleryPhotoToAdapter")
        if (data != null && data.data != null) {
            val location = data.data!!.toString()
            patternAdapter.addImage(location)
        }
    }

    // Works Not working on phone
    private fun addPhotoToGallery() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            activity!!.sendBroadcast(mediaScanIntent)
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(Constants.KEY_URL, currentPhotoPath)
    }


}