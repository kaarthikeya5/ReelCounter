package com.example.reelcounter

import android.content.Context

/**
 * Holds the counter state (on/off and the number of reels) in SharedPreferences,
 * so the Activity and the AccessibilityService can share it.
 */
class CounterStore(context: Context) {

    private val prefs = context.getSharedPreferences("reel_counter", Context.MODE_PRIVATE)

    fun isCounting(): Boolean = prefs.getBoolean(KEY_COUNTING, false)

    fun setCounting(on: Boolean) {
        prefs.edit().putBoolean(KEY_COUNTING, on).apply()
    }

    fun getCount(): Int = prefs.getInt(KEY_COUNT, 0)

    fun increment() {
        prefs.edit().putInt(KEY_COUNT, getCount() + 1).apply()
    }

    fun reset() {
        prefs.edit().putInt(KEY_COUNT, 0).apply()
    }

    companion object {
        private const val KEY_COUNTING = "counting"
        private const val KEY_COUNT = "count"
    }
}
