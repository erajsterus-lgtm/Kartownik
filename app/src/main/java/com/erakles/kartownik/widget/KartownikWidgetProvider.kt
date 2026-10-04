package com.erakles.kartownik.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.erakles.kartownik.KartownikApp
import com.erakles.kartownik.MainActivity
import com.erakles.kartownik.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class KartownikWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val views = RemoteViews(context.packageName, R.layout.kartownik_widget_layout).apply {
                setOnClickPendingIntent(R.id.widget_root, pendingIntent)
            }

            // Pobierz aktualną liczbę kart z bazy w tle
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = (context.applicationContext as KartownikApp).database
                    val count = db.cardDao().getCardCount()
                    val text = if (count == 0) {
                        "Brak kart - dotknij, aby dodać"
                    } else {
                        "Zapisane karty w portfelu: $count"
                    }
                    views.setTextViewText(R.id.widget_cards_count, text)
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                } catch (_: Exception) {
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }
        }
    }
}
