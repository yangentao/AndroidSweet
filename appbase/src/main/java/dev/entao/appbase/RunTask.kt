package dev.entao.appbase

import dev.entao.log.Yog
import java.util.*


/**
 * 一种结果是执行完成, 另一个是被取消. 这两个都是结束状态
 *
 * @author yangentao@gmail.com
 */
abstract class RunTask : Runnable {
	protected var id = IdGen.gen()
	var isCanceled = false
		protected set
	var isFinished = false
		protected set
	var error: Throwable? = null
		protected set
	protected var groups: HashSet<String>? = null

	init {
		map[id] = this
	}

	fun finish() {
		isFinished = true
		map.remove(id)
		onFinish()
	}

	fun cancel() {
		isCanceled = true
		map.remove(id)
	}

	fun addGroup(group: String): RunTask {
		if (groups == null) {
			groups = HashSet()
		}
		groups!!.add(group)
		return this
	}

	fun error(): Boolean {
		return error != null
	}

	fun id(): Int {
		return id
	}

	override fun run() {
		if (isFinished || isCanceled) {
			return
		}
		try {
			onRun()
		} catch (t: Throwable) {
			Yog.e(t)
			error = t
		}

		finish()
	}

	protected fun onFinish() {

	}

	@Throws(Exception::class)
	protected abstract fun onRun()

	companion object {

		protected val map = Hashtable<Int, RunTask>(64)

		fun find(id: Int): RunTask? {
			return map[id]
		}

		fun cancel(group: String) {
			val ls = ArrayList<RunTask>()
			synchronized(map) {
				for (t in map.values) {
					if (t.groups != null && t.groups!!.contains(group)) {
						ls.add(t)
					}
				}
			}
			for (t in ls) {
				t.cancel()
			}
		}

		fun cancel(id: Int) {
			val rx = map.remove(id)
			rx?.cancel()
		}
	}

}
