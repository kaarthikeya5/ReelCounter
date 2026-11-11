package com.example.reelcounter

import android.accessibilityservice.AccessibilityService
import android.os.SystemClock
import android.view.accessibility.AccessibilityEvent

/**
 * Watches scroll events in the apps listed in accessibility_service_config.xml
 * (Instagram and YouTube). It only counts while the counter is switched on.
 *
 * It does not read any text or content on screen; it only sees that a scroll happened.
 */
class ReelCounterService : AccessibilityService() {

    private var lastScrollTime = 0L

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null || event.eventType != AccessibilityEvent.TYPE_VIEW_SCROLLED) return

        val store = CounterStore(this)
        if (!store.isCounting()) {
            // Counter is off: do nothing, and forget the last scroll so the next start is clean.
            lastScrollTime = 0L
            return
        }

        val now = SystemClock.elapsedRealtime()
        // One swipe fires several scroll events in a row. A pause longer than
        // GESTURE_GAP_MS means a new swipe, which we count as one reel.
        if (now - lastScrollTime > GESTURE_GAP_MS) {
            store.increment()
        }
        lastScrollTime = now
    }

    override fun onInterrupt() {
        // Nothing to clean up.
    }

    companion object {
        private const val GESTURE_GAP_MS = 600L
    }
}
