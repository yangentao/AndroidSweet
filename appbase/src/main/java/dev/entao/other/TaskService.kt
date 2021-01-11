package dev.entao.other

import android.content.Intent
import dev.entao.appbase.RunTask
import dev.entao.appbase.Task
import java.util.*

/**
 * 调用onProcess来处理任务, 调用finish任务来结束任务.
 * onProcess是有序被回调的, 但是finish方法可以无序.
 *
 * @author yangentao@gmail.com
 * @see BaseTaskService
 */
abstract class TaskService : BaseTaskService() {
	private val set = HashSet<Int>()

	/**
	 * 没有任务后, 多长时间关闭服务,   在关闭服务前, 如果有新任务进来, 会撤销关闭服务的计划
	 *
	 * @return 毫秒
	 */
	protected val stopDelay: Int
		get() = 1000

	override fun onStart(intent: Intent, startId: Int) {
		// 添加任务, 并排队执行
		synchronized(set) {
			set.add(startId)
		}
		handler!!.back(object : RunTask() {

			@Throws(Exception::class)
			override fun onRun() {
				onProcess(intent, startId)
			}
		}).addGroup(GROUP_TASK)
	}

	override fun mayStopSelf() {
		Task.foreDelay(stopDelay.toLong()){
			if (set.isEmpty()) {
				stopSelf()
			}
		}
	}

	/**
	 * 结束一个任务, 可以多次调用.
	 *
	 * @param startId
	 */
	override fun finish(startId: Int) {
		synchronized(set) {
			set.remove(startId)
		}
		mayStopSelf()
	}
}
