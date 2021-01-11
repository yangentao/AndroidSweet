@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package dev.entao.appbase


import android.os.Handler
import android.os.Looper
import dev.entao.base.BlockUnit
import dev.entao.log.Yog
import dev.entao.log.loge
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Created by yet on 2015-11-20.
 */


@Suppress("UNUSED_PARAMETER")
private fun uncaughtException(thread: Thread, ex: Throwable) {
    ex.printStackTrace()
    loge(ex)
    Yog.flush()
    System.exit(-1)
}

private class SafeRun(val r: Runnable) : Runnable {
    override fun run() {
        try {
            r.run()
        } catch (ex: Throwable) {
            ex.printStackTrace()
        }
    }
}

private class SafeBlock(val callback: BlockUnit) : Runnable {
    override fun run() {
        try {
            callback()
        } catch (ex: Throwable) {
            loge(ex)
        }
    }
}

private class GroupTask(val groupName: String, private var block: BlockUnit?) : Runnable {

    init {
        synchronized(allGroupTask) {
            allGroupTask.add(this)
        }
    }

    override fun run() {
        synchronized(allGroupTask) {
            allGroupTask.remove(this)
        }
        try {
            this.block?.invoke()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    companion object {
        private val allGroupTask = ArrayList<GroupTask>()

        fun cancelGroup(name: String) {
            synchronized(allGroupTask) {
                for (item in allGroupTask) {
                    if (item.groupName == name) {
                        item.block = null
                    }
                }
                allGroupTask.removeAll { it.block == null }
            }
        }
    }

}

private class GroupTaskX(val groupName: String, private var block: BlockUnit?) : Runnable {

    init {
        synchronized(allGroupTask) {
            allGroupTask.add(this)
        }
    }

    override fun run() {
        synchronized(allGroupTask) {
            allGroupTask.remove(this)
        }
        val a = this.block ?: return
        this.block = null
        cancelGroup(this.groupName)
        try {
            a.invoke()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    companion object {
        private val allGroupTask = ArrayList<GroupTaskX>()

        private fun cancelGroup(name: String) {
            synchronized(allGroupTask) {
                val ls = allGroupTask.filter { it.groupName == name }.toMutableList()
                while (ls.size > 1) {
                    val item = ls.removeAt(0)
                    item.block = null
                }
                allGroupTask.removeAll { it.block == null }
            }
        }
    }

}

object Task {
    val mainHandler = Handler(Looper.getMainLooper())
    private val es: ScheduledExecutorService = Executors.newScheduledThreadPool(1) { r ->
        val t = Thread(SafeRun(r))
        t.isDaemon = true
        t.priority = Thread.NORM_PRIORITY - 1
        t.setUncaughtExceptionHandler(::uncaughtException)
        t
    }
    val isMainThread: Boolean get() = Thread.currentThread().id == Looper.getMainLooper().thread.id

    //第一次执行block的时候, 取消所有同组任务, 至少millsec会执行一次
    fun mergeX(groupName: String, millSec: Long = 300, block: BlockUnit) {
        mainHandler.postDelayed(GroupTaskX(groupName, block), millSec)
    }

    //后面的merge调用会取消前面所有的同组任务, 连续不超过millSec的同组任务, 只有最后一个被执行
    fun merge(groupName: String, millSec: Long = 300, block: BlockUnit) {
        GroupTask.cancelGroup(groupName)
        mainHandler.postDelayed(GroupTask(groupName, block), millSec)
    }

    fun mainThread(block: BlockUnit) {
        if (InMainThread) {
            block()
        } else {
            fore(block)
        }
    }

    fun fore(callback: BlockUnit) {
        mainHandler.post(callback)
    }

    fun foreDelay(delay: Long, callback: BlockUnit) {
        mainHandler.postDelayed(callback, delay)
    }

    fun foreSeconds(delaySeconds: Int, callback: BlockUnit) {
        mainHandler.postDelayed(callback, (delaySeconds * 1000).toLong())
    }

    fun back(callback: () -> Unit) {
        es.submit(SafeBlock(callback))
    }

    fun backDelay(delay: Long, callback: BlockUnit): ScheduledFuture<*> {
        return es.schedule(SafeBlock(callback), delay, TimeUnit.MILLISECONDS)
    }

    fun backSeconds(delaySeconds: Int, callback: BlockUnit): ScheduledFuture<*> {
        return es.schedule(SafeBlock(callback), delaySeconds.toLong(), TimeUnit.SECONDS)
    }

    //返回true, 继续;  返回false, 中止
    fun backRepeat(delay: Long, callback: () -> Boolean): ScheduledFuture<*> {
        val run = object : Runnable {
            var future: ScheduledFuture<*>? = null
            var goon = true
            override fun run() {
                if (goon) {
                    goon = try {
                        callback()
                    } catch (ex: Throwable) {
                        loge(ex)
                        false
                    }
                }
                if (!goon) {
                    future?.cancel(false)
                }
            }
        }
        val f = es.scheduleAtFixedRate(run, delay, delay, TimeUnit.MILLISECONDS)
        run.future = f
        return f
    }

    //返回true, 继续;  返回false, 中止
    fun foreRepeat(delay: Long, callback: () -> Boolean) {
        foreDelay(delay) {
            val b = callback()
            if (b) {
                foreRepeat(delay, callback)
            }
        }
    }

    private val onceProcessSet = HashSet<String>()

    //进程内只执行一次
    fun onceProcess(key: String, block: BlockUnit) {
        sync(onceProcessSet) {
            if (key in onceProcessSet) {
                return
            }
            onceProcessSet.add(key)
        }
        block.invoke()
    }

    //每个版本只运行一次
    fun onceVersion(key: String, block: BlockUnit) {
        val p = Prefer("once_${App.versionCode}")
        val exist = p.setIfNotPresent(key, true)
        if (!exist) {
            block()
        }
    }

    fun isVersionFirst(key: String): Boolean {
        val p = Prefer("first_${App.versionCode}")
        val exist = p.setIfNotPresent(key, true)
        return !exist
    }

    //maxSec秒倒计时    10, 9, 8, ... 0
//callback(second:Int), 返回false停止计时, 返回true继续计时,直到0
    fun countDown(maxSec: Int, callback: (Int) -> Boolean) {
        if (maxSec < 0) {
            return
        }
        fore {
            val b = callback(maxSec)
            if (b) {
                foreSeconds(1) {
                    countDown(maxSec - 1, callback)
                }
            }
        }
    }

}


val InMainThread: Boolean get() = Thread.currentThread().id == Looper.getMainLooper().thread.id


inline fun <R> sync(lock: Any, block: () -> R): R {
    return synchronized(lock, block)
}


