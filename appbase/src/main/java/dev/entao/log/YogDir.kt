package dev.entao.log

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by entaoyang@163.com on 2016-10-28.
 */

class YogDir(val logdir: File, val keepDays: Int = 30) : YogPrinter {


    private var writer: BufferedWriter? = null
    private var dateStr: String = ""

    init {
        if (!logdir.exists()) {
            logdir.mkdirs()
        }
    }

    private val out: BufferedWriter?
        get() {
            val ff = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val ds = ff.format(Date(System.currentTimeMillis()))
            if (ds == dateStr) {
                return makeSureWriter()
            }
            writer?.flush()
            writer?.close()
            writer = null

            dateStr = ds
            deleteOldLogs(logdir, keepDays)
            return makeSureWriter()
        }

    private fun makeSureWriter(): BufferedWriter? {
        if (writer == null) {
            try {
                writer = BufferedWriter(FileWriter(File(logdir, "y$dateStr.log"), true), 20 * 1024)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
        return writer
    }


    private fun deleteOldLogs(logdir: File, days: Int) {
        val ls = logdir.listFiles()?.filter { it.name.endsWith(".log") }?.sortedByDescending { it.name } ?: emptyList()
        val n = days + 1
        if (n > 0 && ls.size > n) {
            for (i in n until ls.size) {
                ls[i].delete()
            }
        }

    }

    override fun uninstall() {
        writer?.flush()
        writer?.close()
        writer = null
    }

    override fun flush() {
        try {
            out?.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun printLine(level: LogLevel, msg: String) {
        val w = out ?: return
        val s = Yog.formatMsg(level, msg)
        try {
            w.write(s)
            w.write("\n")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        flush()

    }

}