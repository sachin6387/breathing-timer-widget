package com.breathingtimer.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class BreathingTimerWidget : AppWidgetProvider() {

    companion object {
        private const val PREFS_NAME = "BreathingTimerPrefs"
        private const val KEY_CURRENT_PHASE = "current_phase"
        private const val KEY_PHASE_TIME = "phase_time"
        private const val KEY_TOTAL_TIME = "total_time"

        const val INHALE_DURATION = 4
        const val HOLD_DURATION = 7
        const val EXHALE_DURATION = 8
        const val TOTAL_CYCLE = INHALE_DURATION + HOLD_DURATION + EXHALE_DURATION

        const val ACTION_TIMER_UPDATE = "com.breathingtimer.widget.TIMER_UPDATE"
        const val ACTION_START_SERVICE = "com.breathingtimer.widget.START_SERVICE"
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        val serviceIntent = Intent(context, BreathingTimerService::class.java)
        context.startService(serviceIntent)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (context != null && intent != null) {
            if (intent.action == ACTION_TIMER_UPDATE) {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(
                    ComponentName(context, BreathingTimerWidget::class.java)
                )
                for (appWidgetId in appWidgetIds) {
                    updateAppWidget(context, appWidgetManager, appWidgetId)
                }
            }
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.breathing_timer_widget)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val currentPhase = prefs.getString(KEY_CURRENT_PHASE, "INHALE") ?: "INHALE"
        val phaseTime = prefs.getInt(KEY_PHASE_TIME, 0)

        val remainingTime = when (currentPhase) {
            "INHALE" -> INHALE_DURATION - phaseTime
            "HOLD" -> HOLD_DURATION - phaseTime
            "EXHALE" -> EXHALE_DURATION - phaseTime
            else -> TOTAL_CYCLE
        }

        val phaseText = when (currentPhase) {
            "INHALE" -> "INHALE"
            "HOLD" -> "HOLD"
            "EXHALE" -> "EXHALE"
            else -> "READY"
        }

        views.setTextViewText(R.id.phase_text, phaseText)
        views.setTextViewText(R.id.timer_text, remainingTime.toString())

        val phaseColor = when (currentPhase) {
            "INHALE" -> android.graphics.Color.parseColor("#4CAF50")
            "HOLD" -> android.graphics.Color.parseColor("#FF9800")
            "EXHALE" -> android.graphics.Color.parseColor("#2196F3")
            else -> android.graphics.Color.parseColor("#9E9E9E")
        }
        views.setTextColor(R.id.phase_text, phaseColor)
        views.setTextColor(R.id.timer_text, phaseColor)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
