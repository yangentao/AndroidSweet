package dev.entao.http

import dev.entao.base.lowerCased
import java.io.IOException
import java.io.OutputStream

class SizeStream : OutputStream() {
    var size = 0
        private set

    @Throws(IOException::class)
    override fun write(oneByte: Int) {
        ++size
    }

    fun incSize(size: Int) {
        this.size += size
    }

}

fun allowDump(ct: String?): Boolean {
    val a = ct?.lowerCased ?: return false
    return "json" in a || "xml" in a || "html" in a || "text" in a
}
