package com.breathingtimer.widget

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper

class BreathingTimerService : Service() {

    private var timerHandler: Handler? = null
    private var isRunning = false
    private var currentPhase = "INHALE"
    private var phaseTime = 0
    private var totalTime = 0

    companion object {
        private const val PREFS_NAME = "BreathingTimerPrefs"
        private const val KEY_CURRENT_PHASE = "current_phase"
        private const val KEY_PHASE_TIME = "phase_time"
        private const val KEY_TOTAL_TIME = "total_time"

        const val INHALE_DURATION = 4
        const val HOLD_DURATION = 7
        const val EXHALE_DURATION = 8
        const val TOTAL_CYCLE = INHALE_DURATION + HOLD_DURATION + EXHALE_DURATION
    }

    override fun onCreate() {
        super.onCreate()
        timerHandler = Handler(Looper.getMainLooper())
        loadState()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isRunning) {
            startTimer()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startTimer() {
        isRunning = true
        timerHandler?.post(timerRunnable)
    }

    private fun stopTimer() {
        isRunning = false
        timerHandler?.removeCallbacks(timerRunnable)
    }

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                phaseTime++
                totalTime++

                when (currentPhase) {
                    "INHALE" -> {
                        if (phaseTime >= INHALE_DURATION) {
                            currentPhase = "HOLD"
                            phaseTime = 0
                        }
                    }
                    "HOLD" -> {
                        if (phaseTime >= HOLD_DURATION) {
                            currentPhase = "EXHALE"
                            phaseTime = 0
                        }
                    }
                    "EXHALE" -> {
                        if (phaseTime >= EXHALE_DURATION) {
                            currentPhase = "INHALE"
                            phaseTime = 0
                            totalTime = 0
                        }
                    }
                }

                saveState()
                updateWidget()
                timerHandler?.postDelayed(this, 1000)
            }
        }
    }

    private fun saveState() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString(KEY_CURRENT_PHASE, currentPhase)
            putInt(KEY_PHASE_TIME, phaseTime)
            putInt(KEY_TOTAL_TIME, totalTime)
            apply()
        }
    }

    private fun loadState() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        currentPhase = prefs.getString(KEY_CURRENT_PHASE, "INHALE") ?: "INHALE"
        phaseTime = prefs.getInt(KEY_PHASE_TIME, 0)
        totalTime = prefs.getInt(KEY_TOTAL_TIME, 0)
    }

    private fun updateWidget() {
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(this, BreathingTimerWidget::class.java)
        )
        val intent = Intent(this, BreathingTimerWidget::class.java)
        intent.action = BreathingTimerWidget.ACTION_TIMER_UPDATE
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }
}
