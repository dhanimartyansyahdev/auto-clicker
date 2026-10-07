package com.example.autoclicker

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Path
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.core.app.NotificationCompat

class AutoClickService : AccessibilityService() {

    companion object {
        private const val ACTION_START = "com.example.autoclicker.action.START"
        private const val EXTRA_X = "extra_x"
        private const val EXTRA_Y = "extra_y"
        private const val EXTRA_INTERVAL_MS = "extra_interval_ms"
        private const val PREFS_NAME = "auto_click_prefs"
        private const val KEY_RUNNING = "running"

        fun start(context: Context, x: Int, y: Int, intervalMs: Long) {
            val intent = Intent(context, AutoClickService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_X, x)
                putExtra(EXTRA_Y, y)
                putExtra(EXTRA_INTERVAL_MS, intervalMs)
            }
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, AutoClickService::class.java)
            context.stopService(intent)
        }

        fun isRunning(context: Context): Boolean {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_RUNNING, false)
        }

        fun isAccessibilityEnabled(context: Context): Boolean {
            val enabled = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false

            val serviceName = "${context.packageName}/${AutoClickService::class.java.name}"
            return enabled.split(":").contains(serviceName)
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private var clickRunnable: Runnable? = null
    private var targetX = 0
    private var targetY = 0
    private var intervalMs = 1000L

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_START) {
            targetX = intent.getIntExtra(EXTRA_X, 0)
            targetY = intent.getIntExtra(EXTRA_Y, 0)
            intervalMs = intent.getLongExtra(EXTRA_INTERVAL_MS, 1000L).coerceAtLeast(100L)
            startLoop()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        stopLoop()
        super.onDestroy()
    }

    private fun startLoop() {
        setRunningState(true)
        clickRunnable?.let { handler.removeCallbacks(it) }

        clickRunnable = object : Runnable {
            override fun run() {
                performTap(targetX, targetY)
                handler.postDelayed(this, intervalMs)
            }
        }
        handler.post(clickRunnable!!)
    }

    private fun stopLoop() {
        clickRunnable?.let { handler.removeCallbacks(it) }
        clickRunnable = null
        setRunningState(false)
    }

    private fun setRunningState(running: Boolean) {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_RUNNING, running).apply()
    }

    private fun performTap(x: Int, y: Int) {
        val path = Path().apply {
            moveTo(x.toFloat(), y.toFloat())
            lineTo(x.toFloat(), y.toFloat())
        }

        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0L, 50L))
            .build()

        dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
            }
        }, null)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "autoclicker_channel",
                "Auto Clicker",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification() = NotificationCompat.Builder(this, "autoclicker_channel")
        .setContentTitle("Auto Clicker")
        .setContentText("Klik otomatis sedang berjalan")
        .setSmallIcon(android.R.drawable.star_on)
        .build()

    override fun onAccessibilityEvent(event: android.view.accessibility.AccessibilityEvent?) {
        // Kosong, tidak diperlukan
    }

    override fun onInterrupt() {
        // Kosong, tidak diperlukan
    }
}
