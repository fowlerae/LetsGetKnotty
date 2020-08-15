package edu.rosehulman.samuelma.letsgetknotty.rowCounter

import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Handler
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
    private val ACTION_INCREASE_BUTTON = "ACTION_INCREASE_BUTTON"
    private val ACTION_DECREASE_BUTTON = "ACTION_DECREASE_BUTTON"

    private val rowCounters = ArrayList<RowCounter>()
    private val projects = ArrayList<Project>()
    private lateinit var rowCounterRef : CollectionReference
    private lateinit var projectsRef : CollectionReference
    private lateinit var rowCounterListenerRegistration: ListenerRegistration
    private lateinit var listenerRegistration: ListenerRegistration
    private var uid : String = ""
    private var projectId : String = ""
    private var rowCounter : RowCounter? = null

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
        val handler = Handler()
        handler.postDelayed({
            // do something after 500ms
            // had to add delay as it was adding after the grid was created which led to adding the grid
            // to the wrong pattern
            Log.d(Constants.TAG, "Widget Service Project: ${projects[0]}")
            projectId = projects[0].id
            getRowCounter()
        }, 1000)

        handler.postDelayed({
            // do something after 500ms
            // had to add delay as it was adding after the grid was created which led to adding the grid
            // to the wrong pattern
            Log.d(Constants.TAG, "Widget Service Project: ${rowCounters[0]}")
            rowCounter = rowCounters[0]
            setCounter(rowCounter!!)

            // Reaches the view on widget and displays the number
            val view = RemoteViews(packageName, R.layout.simple_app_widget)
            if(rowCounters[0] != null) {
                view.setTextViewText(R.id.widget_row_counter_name, "${projects[0].name} ${rowCounters[0].name}")
                view.setTextViewText(R.id.widget_current_row, rowCounters[0].currentRow.toString())
            } else {
                view.setTextViewText(R.id.widget_row_counter_name, "Row Counter not getting passed")
                view.setTextViewText(R.id.widget_current_row, "0")
            }
            val intent = Intent(applicationContext, SimpleAppWidget::class.java)
            intent.action = ACTION_INCREASE_BUTTON
            val pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            view.setOnClickPendingIntent(R.id.widget_row_counter_increase_button, pendingIntent)

            val intent2 = Intent(applicationContext, SimpleAppWidget::class.java)
            intent2.action = ACTION_DECREASE_BUTTON
            val pendingIntent2 = PendingIntent.getBroadcast(applicationContext, 1, intent2, PendingIntent.FLAG_UPDATE_CURRENT)
            view.setOnClickPendingIntent(R.id.widget_row_counter_decrease_button, pendingIntent2)


            val theWidget = ComponentName(this, SimpleAppWidget::class.java)
            val manager = AppWidgetManager.getInstance(this)
            manager.updateAppWidget(theWidget, view)

        }, 10000)

        return super.onStartCommand(intent, flags, startId)
    }

    private fun setCounter(rowCounter: RowCounter) {
        counter = rowCounter
        rowCountersRef = rowCounterRef
        Log.d(Constants.TAG, "Widget Service setting Counter: $counter")
    }



    companion object {
        var counter : RowCounter? = null
        private lateinit var rowCountersRef : CollectionReference

        fun increaseCount() : String {
            Log.d(Constants.TAG, "Widget Service setting Counter: $counter")
            counter?.increaseRow()
            edit()
            val c = counter?.currentRow
            return c?.toString() ?: "0"
        }

        fun decreaseCount() : String {
            Log.d(Constants.TAG, "Widget Service setting Counter: $counter")
            counter?.decreaseRow()
            edit()
            val c = counter?.currentRow
            return c?.toString() ?: "0"
        }

        private fun edit() {
            rowCountersRef.document(counter.id).set(counter!!)
        }

    }

}