@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package dev.entao.http


import android.content.Context
import android.net.Uri
import android.os.NetworkOnMainThreadException
import android.util.Base64
import dev.entao.json.YsonObject
import dev.entao.appbase.App
import dev.entao.base.*
import dev.entao.log.logd
import dev.entao.log.loge
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.ProtocolException
import java.net.URL
import java.util.*
import java.util.zip.GZIPInputStream

/**
 * Created by entaoyang@163.com on 2015-11-20.
 */
fun httpGet(url: String, block: HttpGet.() -> Unit): HttpResult {
    val h = HttpGet(url)
    h.block()
    return h.request()
}

fun httpPost(url: String, block: HttpPost.() -> Unit): HttpResult {
    val h = HttpPost(url)
    h.block()
    return h.request()
}

fun httpRaw(url: String, block: HttpRaw.() -> Unit): HttpResult {
    val h = HttpRaw(url)
    h.block()
    return h.request()
}

fun httpMultipart(context: Context, url: String, block: HttpMultipart.() -> Unit): HttpResult {
    val h = HttpMultipart(context, url)
    h.block()
    return h.request()
}

class HttpGet(url: String) : HttpReq(url) {
    init {
        method = "GET"
    }

    override fun onSend(connection: HttpURLConnection) {
    }
}

class HttpPost(url: String) : HttpReq(url) {

    init {
        method = "POST"
        header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
    }

    override fun onSend(connection: HttpURLConnection) {
        val os = connection.outputStream
        try {
            val s = buildArgs()
            if (s.isNotEmpty()) {
                write(os, s)
                if (dumpReq) {
                    logd("--body:", s)
                }
            }
            os.flush()
        } finally {
            os.closeSafe()
        }
    }
}

class HttpRaw(url: String) : HttpReq(url) {
    private lateinit var rawData: ByteArray

    init {
        method = "POST"
    }

    fun data(contentType: String, data: ByteArray): HttpRaw {
        header("Content-Type", contentType)
        this.rawData = data
        return this
    }

    fun jsonObject(block: YsonObject.() -> Unit): HttpRaw {
        val yo = YsonObject()
        yo.block()
        return this.json(yo.toString())
    }

    fun json(json: String): HttpRaw {
        return data("application/json;charset=utf-8", json.toByteArray(charsetUTF8))
    }

    fun xml(xml: String): HttpRaw {
        return data("application/xml;charset=utf-8", xml.toByteArray(charsetUTF8))
    }

    override fun onSend(connection: HttpURLConnection) {
        val os = connection.outputStream
        try {
            os.write(rawData)
            if (dumpReq && allowDump(this.headerMap["Content-Type"])) {
                logd("--body:", String(rawData, Charsets.UTF_8))
            }
            os.flush()
        } finally {
            os.closeSafe()
        }
    }
}

class HttpMultipart(val context: Context, url: String) : HttpReq(url) {
    private val BOUNDARY = UUID.randomUUID().toString()
    private val BOUNDARY_START = "--$BOUNDARY\r\n"
    private val BOUNDARY_END = "--$BOUNDARY--\r\n"

    private val fileList = ArrayList<FileParam>()

    init {
        method = "POST"
        header("Content-Type", "multipart/form-data; boundary=$BOUNDARY")
    }

    fun file(fileParam: FileParam): HttpMultipart {
        fileList.add(fileParam)
        return this
    }

    fun file(key: String, file: Uri): HttpMultipart {
        val p = FileParam(key, file)
        return file(p)
    }

    fun file(key: String, file: Uri, block: FileParam.() -> Unit): HttpMultipart {
        val p = FileParam(key, file)
        p.block()
        return file(p)
    }


    fun file(key: String, file: File): HttpMultipart {
        val p = FileParam(key, file)
        return file(p)
    }


    fun file(key: String, file: File, block: FileParam.() -> Unit): HttpMultipart {
        val p = FileParam(key, file)
        p.block()
        return file(p)
    }

    override fun onSend(connection: HttpURLConnection) {
        val os = connection.outputStream
        try {
            sendMultipart(os)
            os.flush()
        } finally {
            os.closeSafe()
        }
    }

    override fun dumpReq() {
        super.dumpReq()
        for (fp in fileList) {
            logd("--file:", fp)
        }
    }

    override fun preConnect(connection: HttpURLConnection) {
        super.preConnect(connection)
        if (fileList.size > 0) {
            val os = SizeStream()
            sendMultipart(os)
            connection.setFixedLengthStreamingMode(os.size)
        }
    }

    @Throws(IOException::class)
    private fun sendMultipart(os: OutputStream) {

        if (argMap.size > 0) {
            for (e in argMap.entries) {
                write(os, BOUNDARY_START)
                write(os, "Content-Disposition: form-data; name=\"", e.key, "\"\r\n")
                write(os, "Content-Type:text/plain;charset=utf-8\r\n")
                write(os, "\r\n")
                write(os, e.value, "\r\n")
            }
        }
        if (fileList.size > 0) {
            for (fp in fileList) {
                val fis = context.contentResolver.openInputStream(fp.file) ?: continue
                write(os, BOUNDARY_START)
                write(os, "Content-Disposition:form-data;name=\"${fp.key}\";filename=\"${fp.filename}\"\r\n")
                write(os, "Content-Type:${fp.mime}\r\n")
                write(os, "Content-Transfer-Encoding: binary\r\n")
                write(os, "\r\n")

                val total = fis.available()
                if (os is SizeStream) {
                    os.incSize(total)
                    fis.closeSafe()
                } else {
                    fis.copyToProgress(os, fp.progress)
                    fis.closeSafe()

                }
                write(os, "\r\n")
            }
        }
        os.write(BOUNDARY_END.toByteArray())
    }
}

abstract class HttpReq(val url: String) {
    val UTF8 = "UTF-8"
    val charsetUTF8 = Charsets.UTF_8
    protected var method: String = "GET"

    protected val headerMap = HashMap<String, String>()
    val argMap = HashMap<String, String>()

    private var timeoutConnect = 20000
    private var timeoutRead = 20000

    private var saveToFile: File? = null
    private var progress: Progress? = null

    var dumpReq: Boolean = App.debug
    var dumpResp: Boolean = App.debug

    init {
        userAgent("android")
        accept("application/json,text/plain,text/html,*/*")
//        acceptLanguage("zh-CN,en-US;q=0.8,en;q=0.6")
        headerMap["Accept-Charset"] = "UTF-8,*"
        headerMap["Connection"] = "close"

    }

    fun saveTo(file: File): HttpReq {
        this.saveToFile = file
        return this
    }

    //recv progress
    fun progress(p: Progress?): HttpReq {
        this.progress = p
        return this
    }

    fun header(vararg pairs: Pair<String, String>): HttpReq {
        for ((k, v) in pairs) {
            headerMap[k] = v
        }
        return this
    }

    fun header(key: String, value: String): HttpReq {
        headerMap[key] = value
        return this
    }

    fun headers(map: Map<String, String>): HttpReq {
        headerMap.putAll(map)
        return this
    }

    fun timeoutConnect(millSeconds: Int): HttpReq {
        this.timeoutConnect = millSeconds
        return this
    }

    fun timeoutRead(millSeconds: Int): HttpReq {
        this.timeoutRead = millSeconds
        return this
    }

    fun accept(accept: String): HttpReq {
        headerMap["Accept"] = accept
        return this
    }

    fun acceptLanguage(acceptLanguage: String): HttpReq {
        headerMap["Accept-Language"] = acceptLanguage
        return this
    }

    fun auth(user: String, pwd: String): HttpReq {
        val usernamePassword = "$user:$pwd"
        val encodedUsernamePassword = Base64.encodeToString(usernamePassword.toByteArray(charsetUTF8), Base64.NO_WRAP)
        headerMap["Authorization"] = "Basic $encodedUsernamePassword"
        return this
    }

    fun authBearer(token: String): HttpReq {
        headerMap["Authorization"] = "Bearer $token"
        return this
    }

    fun userAgent(userAgent: String): HttpReq {
        return header("User-Agent", userAgent)
    }

    infix fun String.TO(v: String) {
        arg(this, v)
    }

    infix fun String.TO(v: Int) {
        arg(this, v.toString())
    }

    infix fun String.TO(v: Long) {
        arg(this, v.toString())
    }

    infix fun String.TO(v: Double) {
        arg(this, v.toString())
    }

    infix fun String.TO(v: Boolean) {
        arg(this, v.toString())
    }

    fun arg(key: String, value: String): HttpReq {
        argMap[key] = value
        return this
    }

    fun arg(key: String, value: Long): HttpReq {
        argMap[key] = "" + value
        return this
    }

    fun arg(key: String, value: Int): HttpReq {
        argMap[key] = "" + value
        return this
    }

    fun arg(key: String, value: Double): HttpReq {
        argMap[key] = "" + value
        return this
    }

    fun arg(key: String, value: Boolean): HttpReq {
        argMap[key] = "" + value
        return this
    }

    fun args(vararg args: Pair<String, String>): HttpReq {
        for ((k, v) in args) {
            argMap[k] = v
        }
        return this
    }

    fun args(map: Map<String, String>): HttpReq {
        argMap.putAll(map)
        return this
    }

    fun args(yo: YsonObject): HttpReq {
        yo.keys.forEach {
            argMap[it] = yo.str(it) ?: ""
        }
        return this
    }


    //[from, to]
    fun range(from: Int, to: Int): HttpReq {
        headerMap["Range"] = "bytes=$from-$to"
        return this
    }

    fun range(from: Int): HttpReq {
        headerMap["Range"] = "bytes=$from-"
        return this
    }

    protected fun buildArgs(): String {
        return argMap.map {
            it.key.urlEncoded + "=" + it.value.urlEncoded
        }.joinToString("&")
    }

    @Throws(MalformedURLException::class)
    fun buildGetUrl(): String {
        val sArgs = buildArgs()
        var u: String = url
        if (sArgs.isNotEmpty()) {
            val n = u.indexOf('?')
            if (n < 0) {
                u += "?"
            }
            if ('?' != u[u.length - 1]) {
                u += "&"
            }
            u += sArgs
        }
        return u
    }

    open fun dumpReq() {
        if (!dumpReq) {
            return
        }
        logd("Http Request:", url)
        for ((k, v) in headerMap) {
            logd("--head:", k, "=", v)
        }
        for ((k, v) in argMap) {
            logd("--arg:", k, "=", v)
        }
    }

    @Throws(ProtocolException::class, UnsupportedEncodingException::class)
    protected open fun preConnect(connection: HttpURLConnection) {
        HttpURLConnection.setFollowRedirects(true)
        connection.doOutput = method != "GET"
        connection.doInput = true
        connection.connectTimeout = timeoutConnect
        connection.readTimeout = timeoutRead
        connection.requestMethod = method
        connection.useCaches = false

        for (e in headerMap.entries) {
            connection.setRequestProperty(e.key, e.value)
        }
    }

    @Throws(IOException::class)
    protected fun write(os: OutputStream, vararg arr: String) {
        for (s in arr) {
            os.write(s.toByteArray(charsetUTF8))
        }
    }

    @Throws(IOException::class)
    private fun onResponse(connection: HttpURLConnection): HttpResult {
        val result = HttpResult(this.url).apply {
            responseCode = connection.responseCode
            responseMsg = connection.responseMessage
            contentType = connection.contentType
            headerMap = connection.headerFields
            contentLength = connection.contentLength
        }
        val total = result.contentLength
        try {
            val os: OutputStream = if (this.saveToFile != null) {
                val dir = this.saveToFile!!.parentFile
                if (dir != null) {
                    if (!dir.exists()) {
                        if (!dir.mkdirs()) {
                            loge("创建目录失败")
                            throw IOException("创建目录失败!")
                        }
                    }
                }
                FileOutputStream(saveToFile!!)
            } else {
                ByteArrayOutputStream(if (total > 0) total else 64)
            }
            //TODO  4xx, 5xx时直接返回
            var input = connection.inputStream
            val mayGzip = connection.contentEncoding
            if (mayGzip != null && mayGzip.contains("gzip")) {
                input = GZIPInputStream(input)
            }
            input.copyToProgress(os, progress, total)
            input.closeSafe()
            os.closeSafe()
            if (os is ByteArrayOutputStream) {
                result.response = os.toByteArray()
            }
        } catch (ex: Exception) {
            result.exception = ex
            ex.printStackTrace()
        }
        return result
    }

    @Throws(IOException::class)
    protected abstract fun onSend(connection: HttpURLConnection)

    fun request(): HttpResult {
        var connection: HttpURLConnection? = null
        try {
            logd("request------------------------")
            if (dumpReq) {
                logd("request----------dumpReq--------------")
                dumpReq()
            }
            connection = if (this is HttpGet || this is HttpRaw) {
                URL(buildGetUrl()).openConnection() as HttpURLConnection
            } else {
                URL(url).openConnection() as HttpURLConnection
            }

            preConnect(connection)
            connection.connect()
            onSend(connection)
            val r = onResponse(connection)
            if (dumpResp) {
                r.dump()
            }
            return r
        } catch (ex: Exception) {
            if (ex is NetworkOnMainThreadException) {
                loge("主线程中使用了网络请求!")
            }
            ex.printStackTrace()
            loge(ex)
            val result = HttpResult(this.url)
            result.exception = ex
            return result
        } finally {
            connection?.disconnect()
        }
    }

    fun download(saveto: File, progress: Progress?): HttpResult {
        return saveTo(saveto).progress(progress).request()
    }


}

