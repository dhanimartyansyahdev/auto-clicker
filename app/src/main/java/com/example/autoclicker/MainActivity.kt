package com.example.autoclicker

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.content.Intent
import android.graphics.Path
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat

class MainActivity : AppCompatActivity() {

    private lateinit var xInput: EditText
    private lateinit var yInput: EditText
    private lateinit var intervalInput: EditText
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        xInput = findViewById(R.id.xInput)
        yInput = findViewById(R.id.yInput)
        intervalInput = findViewById(R.id.intervalInput)
        statusText = findViewById(R.id.statusText)

        findViewById<Button>(R.id.startButton).setOnClickListener { startAutoClick() }
        findViewById<Button>(R.id.stopButton).setOnClickListener { stopAutoClick() }
        findViewById<Button>(R.id.accessibilityButton).setOnClickListener { openAccessibilitySettings() }

        updateStatus()
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    private fun startAutoClick() {
        if (!AutoClickService.isAccessibilityEnabled(this)) {
            Toast.makeText(this, "Aktifkan Auto Clicker di Accessibility Settings", Toast.LENGTH_LONG).show()
            openAccessibilitySettings()
            return
        }

        val x = xInput.text.toString().toIntOrNull()
        val y = yInput.text.toString().toIntOrNull()
        val interval = intervalInput.text.toString().toLongOrNull()

        if (x == null || y == null || interval == null) {
            Toast.makeText(this, "Isi X, Y, dan interval dengan benar", Toast.LENGTH_SHORT).show()
            return
        }

        if (interval < 100L) {
            Toast.makeText(this, "Interval minimum 100 ms", Toast.LENGTH_SHORT).show()
            return
        }

        AutoClickService.start(this, x, y, interval)
        updateStatus()
    }

    private fun stopAutoClick() {
        AutoClickService.stop(this)
        updateStatus()
    }

    private fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }

    private fun updateStatus() {
        statusText.text = if (AutoClickService.isRunning(this)) {
            "Status: Running"
        } else {
            "Status: Stopped"
        }
    }
}
