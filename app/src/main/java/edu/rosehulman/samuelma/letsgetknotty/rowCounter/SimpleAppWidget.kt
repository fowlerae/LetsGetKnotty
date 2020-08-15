package edu.rosehulman.samuelma.letsgetknotty.rowCounter

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.widget.RemoteViews
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import edu.rosehulman.samuelma.letsgetknotty.Constants
import edu.rosehulman.samuelma.letsgetknotty.R
import edu.rosehulman.samuelma.letsgetknotty.project.Project
import java.util.ArrayList


class SimpleAppWidget : AppWidgetProvider() {
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

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
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
            val views = RemoteViews(context?.packageName, R.layout.simple_app_widget)
            if(rowCounters[0] != null) {
                views.setTextViewText(R.id.widget_row_counter_name, "${projects[0].name} ${rowCounters[0].name}")
                views.setTextViewText(R.id.widget_current_row, rowCounters[0].currentRow.toString())
            } else {
                views.setTextViewText(R.id.widget_row_counter_name, "Row Counter not getting passed")
                views.setTextViewText(R.id.widget_current_row, "0")
            }

            val appWidget = context?.let { ComponentName(it, SimpleAppWidget::class.java) }
            val appWidgetManager = AppWidgetManager.getInstance(context)
            appWidgetManager.updateAppWidget(appWidget, views)
        }, 10000)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
       //     updateAppWidget(context, appWidgetManager, appWidgetId)
            val intent = Intent(context.applicationContext, WidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            context.startService(intent)

        }
    }

    private fun updateAppWidget(
        context: Context, appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {

        val views = RemoteViews(context.packageName, R.layout.simple_app_widget)
        val intent = Intent(context, SimpleAppWidget::class.java)
        intent.action = ACTION_INCREASE_BUTTON
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        views.setOnClickPendingIntent(R.id.widget_row_counter_increase_button, pendingIntent)

        val intent2 = Intent(context, SimpleAppWidget::class.java)
        intent2.action = ACTION_DECREASE_BUTTON
        val pendingIntent2 = PendingIntent.getBroadcast(context, 1, intent2, PendingIntent.FLAG_UPDATE_CURRENT)
        views.setOnClickPendingIntent(R.id.widget_row_counter_decrease_button, pendingIntent2)

        appWidgetManager.updateAppWidget(appWidgetId, views)

    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(Constants.TAG, "Got to the onReceive, Intent: ${intent.action}")
        super.onReceive(context, intent)
        Log.d(Constants.TAG, "Got to the onReceive, Intent: ${intent.action}")
        val views = RemoteViews(context.packageName, R.layout.simple_app_widget)
        if (ACTION_INCREASE_BUTTON == intent.action) {
            val c = WidgetService.increaseCount()
            Log.d(Constants.TAG, "Increase: $c")
            views.setTextViewText(R.id.widget_current_row, c)
            val appWidget = ComponentName(context, SimpleAppWidget::class.java)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            appWidgetManager.updateAppWidget(appWidget, views)

        } else if (ACTION_DECREASE_BUTTON == intent.action) {
            val c = WidgetService.decreaseCount()
            Log.d(Constants.TAG, "Increase: $c")
            views.setTextViewText(R.id.widget_current_row, c)
            val appWidget = ComponentName(context, SimpleAppWidget::class.java)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            appWidgetManager.updateAppWidget(appWidget, views)
        }

    }

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


    private fun setCounter(rowCounter: RowCounter) {
        counter = rowCounter
        Log.d(Constants.TAG, "Widget Service setting Counter: $counter")
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