package dev.entao.other

import android.content.Intent
import dev.entao.appbase.Task
import dev.entao.log.Yog
import java.util.*

/**
 * 直到一个任务处理完, 才进行下一个任务的处理.
 * 调用finish方法来结束一个任务
 *
 * @see BaseTaskService
 *
 * @author yangentao@gmail.com
 */
abstract class SeqTaskService : BaseTaskService() {

	private val taskQueue = LinkedList<TaskInfo>()
	private var lastRunningTask: TaskInfo? = null

	internal class TaskInfo(val intent: Intent, val startId: Int) {

		override fun hashCode(): Int {
			return startId
		}

		override fun equals(other: Any?): Boolean {
			return if (other is TaskInfo) {
				startId == other.startId
			} else false
		}
	}

	override fun onStart(intent: Intent, startId: Int) {
		// 进队列
		val task = TaskInfo(intent, startId)
		synchronized(taskQueue) {
			taskQueue.offerLast(task)
		}
		dispatchTask()
	}

	private fun pollTask(): TaskInfo? {
		synchronized(taskQueue) {
			return taskQueue.pollFirst()
		}
	}

	private fun dispatchTask() {
		if (lastRunningTask != null) {
			// xlog.d("已经有任务在运行");
			return
		}
		val task = pollTask()
		if (task != null) {
			lastRunningTask = task
			Task.back {
				onProcess(task.intent, task.startId)
			}
		} else {
			mayStopSelf()
		}
	}

	override fun finish(startId: Int) {
		if (lastRunningTask == null) {
			Yog.e("没有正在执行的任务")
			return
		}
		Yog.e(lastRunningTask!!.startId != startId, "要结束的task和正在运行的不一样!")

		lastRunningTask = null
		dispatchTask()
	}

	override fun mayStopSelf() {
		Task.foreDelay(1000){
			if (lastRunningTask == null && taskQueue.isEmpty()) {
				stopSelf()
			}
		}
	}

}
