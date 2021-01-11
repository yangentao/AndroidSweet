package dev.entao.ui.util.app

import android.os.PowerManager
import dev.entao.appbase.App

class PowerLock {
    val lock = App.powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PowerLock:${System.currentTimeMillis()}")

    init {
        lock.setReferenceCounted(false)
    }

    fun acquire(ms: Long) {
        lock.acquire(ms)
    }

    fun release() {
        lock.release()
    }

    companion object {
        fun acquire(block: () -> Unit) {
            val lock = App.powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PowerLock:${System.currentTimeMillis()}")
            lock.setReferenceCounted(false)
            lock.acquire(5000)
            block()
            lock.release()
        }
    }
}
