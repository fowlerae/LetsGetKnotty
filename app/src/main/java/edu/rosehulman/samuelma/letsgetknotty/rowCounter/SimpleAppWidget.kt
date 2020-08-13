package edu.rosehulman.samuelma.letsgetknotty.rowCounter

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import edu.rosehulman.samuelma.letsgetknotty.R


class SimpleAppWidget : AppWidgetProvider() {
    private val ACTION_INCREASE_BUTTON = "ACTION_INCREASE_BUTTON"
    private val ACTION_DECREASE_BUTTON = "ACTION_DECREASE_BUTTON"
    private lateinit var rowCounter : RowCounter
    private var count : Int = 0

//    override fun onEnabled(context: Context?) {
//        super.onEnabled(context)
//        // need to set action listeners
//
//        // call getRowCounter
//
//        // it is null then you need to do something change text box????
//    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context, appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // make the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.simple_app_widget)

        // make the intents and assign them to the buttons
        val increaseIntentButton = Intent(context, SimpleAppWidget::class.java)
        increaseIntentButton.action = ACTION_INCREASE_BUTTON

        val increasePendingIntentButton =
            PendingIntent.getBroadcast(context, 0, increaseIntentButton, PendingIntent.FLAG_UPDATE_CURRENT)

        views.setOnClickPendingIntent(R.id.widget_row_counter_increase_button, increasePendingIntentButton)

        val decreaseIntentButton = Intent(context, SimpleAppWidget::class.java)
        decreaseIntentButton.action = ACTION_DECREASE_BUTTON

        val decreasePendingIntentButton =
            PendingIntent.getBroadcast(context, 0, increaseIntentButton, PendingIntent.FLAG_UPDATE_CURRENT)

        views.setOnClickPendingIntent(R.id.widget_row_counter_decrease_button, decreasePendingIntentButton)

        // tell the manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val views = RemoteViews(context.packageName, R.layout.simple_app_widget)
        if(ACTION_INCREASE_BUTTON == intent.action) {
            // change values in
           // rowCounter.increaseRow()
            // views.setTextViewText(R.id.widget_current_row, rowCounter.currentRow.toString())
            count++
            views.setTextViewText(R.id.widget_current_row, count.toString())

        } else if(ACTION_DECREASE_BUTTON == intent.action) {
           // rowCounter.decreaseRow()
           // views.setTextViewText(R.id.widget_current_row, rowCounter.currentRow.toString())
            count--
            views.setTextViewText(R.id.widget_current_row, count.toString())
        }

            val appWidget = ComponentName(context, SimpleAppWidget::class.java)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            appWidgetManager.updateAppWidget(appWidget, views)
    }


    /// need a function for getting the last touched row counter !!!!!

//    fun getRowCounter() {
//
//    }

}