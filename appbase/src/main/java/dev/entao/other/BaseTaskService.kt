package dev.entao.other

import android.content.Intent
import dev.entao.appbase.IdGen
import dev.entao.appbase.PowerLock
import dev.entao.appbase.RunTask
import dev.entao.appbase.TaskQueue
import dev.entao.log.logd

/**
 * IntentService有个问题, 如果后面的Intent提前处理完, 调用了stopSelf(int startId), 则, 整个service被关闭了.
 * 启动一个IntentService 4次, startId分别是: 1, 2, 3, 4
 * 如果 4提前完成并调用了stopSelf(4), 则, 前面的任务1,2,3都被关闭了.
 *
 *
 * 因此, 才有了这个类BaseTaskService,  TaskService, SeqTaskService
 */
@Suppress("OverridingDeprecatedMember")
abstract class BaseTaskService : BaseService() {
    protected val GROUP_TASK = javaClass.simpleName + ".task.group." + IdGen.gen()
    private var lock: PowerLock? = null
    var handler: TaskQueue? = null
        private set
    private var redely = false

    override fun onCreate() {
        super.onCreate()
        handler = TaskQueue("basetaskservice")
        this.redely = redelivery()

        if (needPowerLock()) {
            lock = PowerLock()
            lock!!.acquire(600000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        RunTask.cancel(GROUP_TASK)
        handler!!.quit()
        if (lock != null) {
            lock!!.release()
        }
        logd("Service Destroy")
    }

    override fun onStart(intent: Intent, startId: Int) {
        // TODO 子类实现
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (intent == null) {
            mayStopSelf()
            return if (redely) START_REDELIVER_INTENT else START_NOT_STICKY
        }
        if (!needPocess(intent, startId)) {
            mayStopSelf()
            return if (redely) START_REDELIVER_INTENT else START_NOT_STICKY
        }
        return if (redely) START_REDELIVER_INTENT else START_NOT_STICKY
    }


    open fun redelivery(): Boolean {
        return false
    }

    open fun needPowerLock(): Boolean {
        return true
    }

    open fun needPocess(intent: Intent, startId: Int): Boolean {
        return true
    }

    protected abstract fun mayStopSelf()

    abstract fun finish(startId: Int)

    /**
     * 子线程调用, 处理完成后, 调用finish(startId)来结束任务
     *
     * @param intent
     * @param startId
     */
    protected abstract fun onProcess(intent: Intent, startId: Int)
}
