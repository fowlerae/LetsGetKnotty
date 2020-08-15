package edu.rosehulman.samuelma.letsgetknotty.rowCounter

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.Nullable
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import edu.rosehulman.samuelma.letsgetknotty.Constants
import edu.rosehulman.samuelma.letsgetknotty.R
import edu.rosehulman.samuelma.letsgetknotty.project.Project
import java.util.*


class WidgetService : Service() {

    private val rowCounters = ArrayList<RowCounter>()
    private val projects = ArrayList<Project>()
    private lateinit var rowCounterRef : CollectionReference
    private lateinit var projectsRef : CollectionReference
    private lateinit var rowCounterListenerRegistration: ListenerRegistration
    private lateinit var listenerRegistration: ListenerRegistration
    private var uid : String = ""
    private var projectId : String = ""
    lateinit var rowCounter : RowCounter

    private fun getProject() {
        val firebaseUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser;
        if (firebaseUser != null) {
            uid = firebaseUser.uid
            projectsRef =
                FirebaseFirestore
                    .getInstance()
                    .collection(Constants.USERS_COLLECTION)
                    .document(uid)
                    .collection(Constants.PROJECTS_COLLECTION)
        }
        addSnapshotListener()

    }

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
                }
                DocumentChange.Type.REMOVED -> {
                    Log.d(Constants.TAG, "Removing $project")
                    val index = projects.indexOfFirst { it.id == project.id }
                    projects.removeAt(index)
                }
                DocumentChange.Type.MODIFIED -> {
                    Log.d(Constants.TAG, "Modifying $project")
                    val index = projects.indexOfFirst { it.id == project.id }
                    project.lastTouched = Timestamp.now()
                    projects[index] = project
                }
            }
        }
    }

    private fun getRowCounter() {
        val firebaseUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser;
        if (firebaseUser != null) {
            rowCounterRef =
                FirebaseFirestore
                    .getInstance()
                    .collection(Constants.USERS_COLLECTION)
                    .document(firebaseUser.uid)
                    .collection(Constants.PROJECTS_COLLECTION)
                    .document(projectId)
                    .collection(Constants.ROW_COUNTER_COLLECTION)
        }
        rowCounterAddSnapshotListener()
    }

    private fun rowCounterAddSnapshotListener() {
        rowCounterListenerRegistration = rowCounterRef
            .orderBy(RowCounter.LAST_TOUCHED_KEY, Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, e ->
                if (e != null) {
                    Log.w(Constants.TAG, "listen error", e)
                } else {
                    rowCounterProcessSnapshotChanges(querySnapshot!!)
                }
            }
    }

    private fun rowCounterProcessSnapshotChanges(querySnapshot: QuerySnapshot) {
        // Snapshots has documents and documentChanges which are flagged by type,
        // so we can handle C,U,D differently.
        for (documentChange in querySnapshot.documentChanges) {
            val rowCounter = RowCounter.fromSnapshot(documentChange.document)
            when (documentChange.type) {
                DocumentChange.Type.ADDED -> {
                    Log.d(Constants.TAG, "Adding $rowCounter")
                    rowCounters.add(0, rowCounter)
                }
                DocumentChange.Type.REMOVED -> {
                    Log.d(Constants.TAG, "Removing $rowCounter")
                    val index = rowCounters.indexOfFirst { it.id == rowCounter.id }
                    rowCounters.removeAt(index)
                }
                DocumentChange.Type.MODIFIED -> {
                    Log.d(Constants.TAG, "Modifying $rowCounter")
                    val index = rowCounters.indexOfFirst { it.id == rowCounter.id }
                    rowCounters[index] = rowCounter
                }
            }
        }
    }

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // get projects
        getProject()
        projectId = projects[0].id
        getRowCounter()
        rowCounter = rowCounters[0]
        setCounter(rowCounter)

        // Reaches the view on widget and displays the number
        val view = RemoteViews(packageName, R.layout.simple_app_widget)
        view.setTextViewText(R.id.widget_current_row, rowCounter.currentRow.toString())
        val theWidget = ComponentName(this, SimpleAppWidget::class.java)
        val manager = AppWidgetManager.getInstance(this)
        manager.updateAppWidget(theWidget, view)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun setCounter(rowCounter: RowCounter) {
        counter = rowCounter
    }

    companion object {
        var counter : RowCounter? = null

        fun increaseCount() : String {
            counter?.increaseRow()
            val c = counter?.currentRow
            return c?.toString() ?: "0"
        }

        fun decreaseCount() : String {
            counter?.decreaseRow()
            val c = counter?.currentRow
            return c?.toString() ?: "0"
        }

    }

}