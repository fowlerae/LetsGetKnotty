package edu.rosehulman.samuelma.letsgetknotty.rowCounter

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import edu.rosehulman.samuelma.letsgetknotty.Constants
import edu.rosehulman.samuelma.letsgetknotty.R


class SimpleAppWidget : AppWidgetProvider() {
    private val ACTION_INCREASE_BUTTON = "ACTION_INCREASE_BUTTON"
    private val ACTION_DECREASE_BUTTON = "ACTION_DECREASE_BUTTON"
 //   private lateinit var rowCounter : RowCounter
    private var count : Int = 0

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
//        // make the RemoteViews object
//        val views = RemoteViews(context.packageName, R.layout.simple_app_widget)
//
//        // make the intents and assign them to the buttons
//        val increaseIntentButton = Intent(context, SimpleAppWidget::class.java)
//        increaseIntentButton.action = ACTION_INCREASE_BUTTON
//
//        val increasePendingIntentButton =PendingIntent.getBroadcast(
//            context, 0, increaseIntentButton,
//            PendingIntent.FLAG_UPDATE_CURRENT
//        )
//
//        views.setOnClickPendingIntent(R.id.widget_row_counter_increase_button, increasePendingIntentButton)
//
//        views.setOnClickPendingIntent(R.id.widget_button_row, increasePendingIntentButton)
//        val decreaseIntentButton = Intent(context, SimpleAppWidget::class.java)
//        decreaseIntentButton.action = ACTION_DECREASE_BUTTON
//
//        val decreasePendingIntentButton =
//            PendingIntent.getBroadcast(context, 0, increaseIntentButton, PendingIntent.FLAG_UPDATE_CURRENT)
//
//        views.setOnClickPendingIntent(R.id.widget_row_counter_decrease_button, decreasePendingIntentButton)
//
//        // tell the manager to update the widget
//        appWidgetManager.updateAppWidget(appWidgetId, views)

        val views = RemoteViews(context.packageName, R.layout.simple_app_widget)
        // Construct an Intent which is pointing this class.
        // Construct an Intent which is pointing this class.
        val intent = Intent(context, SimpleAppWidget::class.java)
        intent.action = ACTION_INCREASE_BUTTON
        // And this time we are sending a broadcast with getBroadcast
        // And this time we are sending a broadcast with getBroadcast
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        views.setOnClickPendingIntent(R.id.widget_row_counter_increase_button, pendingIntent)

        val intent2 = Intent(context, SimpleAppWidget::class.java)
        intent2.action = ACTION_DECREASE_BUTTON
        // And this time we are sending a broadcast with getBroadcast
        // And this time we are sending a broadcast with getBroadcast
        val pendingIntent2 = PendingIntent.getBroadcast(context, 1, intent2, PendingIntent.FLAG_UPDATE_CURRENT)


        views.setOnClickPendingIntent(R.id.widget_row_counter_decrease_button, pendingIntent2)
        appWidgetManager.updateAppWidget(appWidgetId, views)

    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(Constants.TAG, "Got to the onReceive, Intent: ${intent.action}")
        super.onReceive(context, intent)
        Log.d(Constants.TAG, "Got to the onReceive, Intent: ${intent.action}")
        val views = RemoteViews(context.packageName, R.layout.simple_app_widget)
        if(ACTION_INCREASE_BUTTON == intent.action) {
            // change values in
           // rowCounter.increaseRow()
            // views.setTextViewText(R.id.widget_current_row, rowCounter.currentRow.toString())
//            count++
//            views.setTextViewText(R.id.widget_current_row, count.toString())
//            Log.d(Constants.TAG, "Increase: $count")
            val c = increaseCount()
            // Construct the RemoteViews object
            // Construct the RemoteViews object
            Log.d(Constants.TAG, "Increase: $c")
            val views =
                RemoteViews(context.packageName, R.layout.simple_app_widget)
            views.setTextViewText(R.id.widget_current_row, c.toString())
            // This time we dont have widgetId. Reaching our widget with that way.
            // This time we dont have widgetId. Reaching our widget with that way.
            val appWidget = ComponentName(context, SimpleAppWidget::class.java)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            // Instruct the widget manager to update the widget
            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidget, views)

        }

        else if(ACTION_DECREASE_BUTTON == intent.action) {
           // rowCounter.decreaseRow()
           // views.setTextViewText(R.id.widget_current_row, rowCounter.currentRow.toString())
//            count--
//            views.setTextViewText(R.id.widget_current_row, count.toString())
//            Log.d(Constants.TAG, "Decrease: $count")

            val c = decreaseCount()
            // Construct the RemoteViews object
            // Construct the RemoteViews object
            Log.d(Constants.TAG, "Increase: $c")
            val views =
                RemoteViews(context.packageName, R.layout.simple_app_widget)
            views.setTextViewText(R.id.widget_current_row, c.toString())
            // This time we dont have widgetId. Reaching our widget with that way.
            // This time we dont have widgetId. Reaching our widget with that way.
            val appWidget = ComponentName(context, SimpleAppWidget::class.java)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            // Instruct the widget manager to update the widget
            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidget, views)
        }
//
//        val appWidget = ComponentName(context, SimpleAppWidget::class.java)
//        val appWidgetManager = AppWidgetManager.getInstance(context)
//        appWidgetManager.updateAppWidget(appWidget, views)
    }

    private fun increaseCount() : Int {
        count +=1
        return count
    }

    fun decreaseCount() : Int {
        count -=1
        return count
    }

}