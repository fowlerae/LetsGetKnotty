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

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
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


}