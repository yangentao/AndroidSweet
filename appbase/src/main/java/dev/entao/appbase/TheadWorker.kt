package dev.entao.appbase

import dev.entao.log.loge

abstract class TheadWorker {
    private var thread: Thread? = null
    protected var isStoping = false

    val isActive: Boolean get() = thread?.isAlive == true

    protected fun startWork() {
        if (thread != null) {
            stopWork()
        }
        val t = Thread(::threadRun)
        this.thread = t
        t.isDaemon = true
        isStoping = false
        t.start()
    }

    protected fun stopWork() {
        isStoping = true
        thread?.join(2000)
        if (thread?.isAlive == true) {
            thread?.interrupt()
        }
        thread = null
    }

    private fun threadRun() {
        try {
            runWork()
        } catch (ex: Exception) {
            ex.printStackTrace()
            loge(ex)
        }
        try {
            runClean()
        } catch (ex: Exception) {
            ex.printStackTrace()
            loge(ex)
        }
        thread = null
    }

    protected abstract fun runWork()
    protected open fun runClean() {
    }
}