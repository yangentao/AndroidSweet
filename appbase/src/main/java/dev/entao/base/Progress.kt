package dev.entao.base

/**
 * Created by entaoyang@163.com on 16/6/17.
 */

interface Progress {
	fun onProgressStart(total: Int)

	fun onProgress(current: Int, total: Int, percent: Int)

	fun onProgressFinish()
}
