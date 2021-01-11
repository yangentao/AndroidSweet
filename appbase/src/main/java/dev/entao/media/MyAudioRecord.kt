package dev.entao.media

import android.content.Context
import android.media.MediaRecorder
import dev.entao.appbase.Task
import dev.entao.log.Yog
import java.io.File

class MyAudioRecord(private val context: Context) {
    private var tickMillSeconds = 100

    private var recorder: MediaRecorder? = null // 录音变量
    var isRecording = false
        private set
    /**
     * @return 毫秒
     */
    var startTime: Long = 0
        private set// 记录录音开始时间
    /**
     * @return 毫秒
     */
    var endTime: Long = 0
        private set// 记录录音开始时间
    private var listener: ProcessListener? = null

    var file: File? = null
        private set

    val totalTime: Long
        get() = endTime - startTime

    private val maxAmplitude: Int
        get() {
            if (recorder != null) {
                val am = recorder!!.maxAmplitude
                Yog.d("AM:$am")
                return am
            }
            return 0
        }


    private fun runTick() {
        if (isRecording) {
            if (listener != null) {
                listener!!.onProgress(maxAmplitude, System.currentTimeMillis() - startTime)
            }
        }
        if (isRecording) {
            Task.foreDelay(tickMillSeconds.toLong()) {
                runTick()
            }
        }
    }

    interface ProcessListener {
        fun onProgress(maxAmplitude: Int, millSeconds: Long)
    }

    init {
        isRecording = false
        // AudioManager audio = (AudioManager)
        // c.getSystemService(Context.AUDIO_SERVICE);
        // int current = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        // audio.setStreamVolume(AudioManager.STREAM_MUSIC, current,
        // AudioManager.FLAG_PLAY_SOUND);

    }

    private fun release() {
        isRecording = false
        if (recorder != null) {
            recorder!!.release()
            recorder = null
        }
    }

    fun setRecordListener(tickMillSeconds: Int, listener: ProcessListener) {
        this.listener = listener
        this.tickMillSeconds = tickMillSeconds
    }

    /**
     * 使用默认的文件开始录音
     * @return
     */
    fun start(): Boolean {
        val file = context.getFileStreamPath("audio_record_default.amr")
        return start(file)
    }

    fun start(file: File): Boolean {
        if (isRecording) {
            Yog.e("already recoding")
            return false
        }
        this.file = file
        recorder = MediaRecorder()
        Yog.d("begin recording...")
        recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)// 设置MediaRecorder的音频源为麦克风
        recorder!!.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
        // 设置MediaRecorder录制的音频格式
        recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)// 设置音频编码Encoder
        recorder!!.setOutputFile(this.file!!.absolutePath)
        try {
            recorder!!.prepare()// 准备录制
            recorder!!.start()// 开始录制

            isRecording = true
            startTime = System.currentTimeMillis()
            Task.fore {
                runTick()
            }
            return true
        } catch (e: Exception) {
            Yog.e(e)
            release()
        }

        return false
    }

    fun stop(): Boolean {
        Yog.d("end recording...")
        if (!isRecording) {
            Yog.e("no not recording!!")
            return false
        }
        stopInner()
        return true
    }

    fun cancel() {
        Yog.d("cancel recording...")
        stopInner()
        if (file != null && file!!.exists()) {
            file!!.delete()
        }
    }

    private fun stopInner() {
        if (recorder != null) {
            endTime = System.currentTimeMillis()
            recorder!!.stop()
            recorder!!.reset()
            release()
        }
    }
}
