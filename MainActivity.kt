package com.example.reelcounter

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var store: CounterStore
    private lateinit var countText: TextView
    private lateinit var statusText: TextView
    private lateinit var toggleButton: Button
    private lateinit var resetButton: Button
    private lateinit var permissionButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        store = CounterStore(this)
        countText = findViewById(R.id.countText)
        statusText = findViewById(R.id.statusText)
        toggleButton = findViewById(R.id.toggleButton)
        resetButton = findViewById(R.id.resetButton)
        permissionButton = findViewById(R.id.permissionButton)

        toggleButton.setOnClickListener {
            if (!store.isCounting() && !isServiceEnabled()) {
                Toast.makeText(this, R.string.status_need_permission, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            store.setCounting(!store.isCounting())
            refresh()
        }

        resetButton.setOnClickListener {
            store.reset()
            refresh()
        }

        permissionButton.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        val counting = store.isCounting()
        val enabled = isServiceEnabled()

        countText.text = store.getCount().toString()
        toggleButton.setText(if (counting) R.string.stop_counter else R.string.start_counter)
        statusText.setText(
            when {
                counting -> R.string.status_counting
                !enabled -> R.string.status_need_permission
                else -> R.string.status_idle
            }
        )
        permissionButton.visibility = if (enabled) View.GONE else View.VISIBLE
    }

    private fun isServiceEnabled(): Boolean {
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        val expected = ComponentName(this, ReelCounterService::class.java).flattenToString()
        return enabledServices.split(':').any { it.equals(expected, ignoreCase = true) }
    }
}
