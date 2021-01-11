package dev.entao.media

import android.media.MediaRecorder
import dev.entao.appbase.Task
import dev.entao.base.FixedQueue
import java.io.File
import java.util.*

class AmrRecord {
	private var DELAY = 30//每xx毫秒记录一次
	private var QUEUE_SIZE = 50
	private var recorder: MediaRecorder? = null // 录音变量
	var isRecording = false
		private set
	private var start_time: Long = 0// 记录录音开始时间
	private var end_time: Long = 0// 记录录音开始时间
	var file: File? = null
		private set

	private val queue = FixedQueue<Int>(QUEUE_SIZE)

	interface AMRRecordCallback {
		fun onAmp(amps: ArrayList<Int>)
	}

	fun setQueueMax(max: Int) {
		QUEUE_SIZE = max
		queue.setMax(max)
	}

	fun setDelay(delay: Int) {
		DELAY = delay
	}

	fun start(file: File, callback: AMRRecordCallback? = null): Boolean {
		for (i in 0..QUEUE_SIZE - 1) {
			queue.add(0)
		}
		isRecording = true
		this.file = file
		recorder = MediaRecorder()
		recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)// 设置MediaRecorder的音频源为麦克风
		recorder!!.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)// RAW_AMR
		recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)// 设置音频编码Encoder
		recorder!!.setOutputFile(this.file!!.absolutePath)
		try {
			recorder!!.prepare()// 准备录制
			recorder!!.start()// 开始录制
			start_time = System.currentTimeMillis()
			if (callback != null) {

				Task.backRepeat(DELAY.toLong()) {
					if (isRecording) {
						val n = maxAmplitude()
						queue.add(n)
						callback.onAmp(queue.toList())
					}
					isRecording
				}
			}
			return true
		} catch (e: Exception) {
			e.printStackTrace()
			release()
		}

		return false
	}

	//毫秒
	fun duration(): Long {
		return end_time - start_time
	}

	private fun release() {
		isRecording = false
		if (recorder != null) {
			recorder!!.release()
			recorder = null
		}
	}

	private fun maxAmplitude(): Int {
		if (recorder != null) {
			return recorder!!.maxAmplitude
		}
		return 0
	}

	fun stop() {
		end_time = System.currentTimeMillis()
		if (recorder != null) {
			recorder!!.stop()
		}
		release()
		queue.clear()
	}
}
