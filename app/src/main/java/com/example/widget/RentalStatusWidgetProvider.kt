package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RentalStatusWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.rental_status_appwidget)

            val currentMonthStr = SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(Date())
            views.setTextViewText(R.id.widget_month, currentMonthStr)

            // PendingIntent to launch MainActivity on widget click
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            // Async load data from database to populate widget
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getInstance(context)
                    val payments = db.paymentDao().getAllPayments().firstOrNull() ?: emptyList()
                    val properties = db.propertyDao().getAllProperties().firstOrNull() ?: emptyList()

                    val totalCollected = payments.sumOf { it.amountPaid }
                    val totalOverdue = payments.sumOf { it.balanceDue }
                    val lunasCount = payments.count { it.status == "LUNAS" && it.balanceDue <= 0 }
                    val overdueCount = payments.count { it.status == "TERTUNGGAK" || it.balanceDue > 0 }

                    views.setTextViewText(R.id.widget_collected, "RM ${String.format(Locale.US, "%.0f", totalCollected)}")
                    views.setTextViewText(R.id.widget_overdue, "RM ${String.format(Locale.US, "%.0f", totalOverdue)}")
                    views.setTextViewText(R.id.widget_status_count, "$lunasCount Lunas • $overdueCount Tunggak")

                    appWidgetManager.updateAppWidget(appWidgetId, views)
                } catch (_: Exception) {
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }
        }

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, RentalStatusWidgetProvider::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            for (widgetId in allWidgetIds) {
                updateAppWidget(context, appWidgetManager, widgetId)
            }
        }
    }
}
