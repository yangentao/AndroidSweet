package dev.entao.base

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


object Md5 {
    fun encode(value: String): String {
        if (value.isEmpty()) {
            return value
        }
        try {
            val md5 = MessageDigest.getInstance("MD5")
            md5.update(value.toByteArray())
            val m = md5.digest()
            return Hex.encode(m)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        error("MD5失败")
    }
}

object Hex {
    private const val DICT = "0123456789ABCDEF"

    // 8->08,  17->1f
    fun encodeByte(b: Int): String {
        val arr = CharArray(2)
        arr[0] = DICT[0x0f and (b ushr 4)]
        arr[1] = DICT[0x0f and b]
        return String(arr)
    }

    fun encodeString(s: String): String {
        return encode(s.toByteArray(Charsets.UTF_8))
    }

    fun decodeString(s: String): String? {
        val bs = decode(s) ?: return null
        return String(bs, Charsets.UTF_8)
    }

    fun encode(bytes: ByteArray): String {
        val ret = StringBuilder(2 * bytes.size)
        for (a in bytes) {
            val n = a.toInt()
            ret.append(DICT[0x0f and (n ushr 4)])
            ret.append(DICT[0x0f and n])
        }
        return ret.toString()
    }

    fun decode(hexString: String?): ByteArray? {
        if (hexString == null) {
            return null
        }
        val strLen = hexString.length
        if (strLen == 0) {
            return ByteArray(0)
        }
        if (strLen % 2 != 0) {
            throw IllegalArgumentException("字符串长度必须是2的倍数")
        }
        val s = hexString.upperCased

        val bytes = ByteArray(strLen / 2)
        var i = 0
        while (i < strLen) {
            val hi = toByte(s[i])
            val lo = toByte(s[i + 1])
            val n = ((hi shl 4) and 0xf0) or (lo and 0x0f)
            bytes[i / 2] = n.toByte()
            i += 2
        }
        return bytes
    }

    fun toByte(ch: Char): Int {
        if (ch in '0'..'9') {
            return ch - '0'
        }
        if (ch in 'A'..'F') {
            return ch - 'A' + 10
        }
        if (ch in 'a'..'f') {
            return ch - 'a' + 10
        }
        throw IllegalArgumentException("不合法的字符$ch")
    }
}

fun String.decodeHex(): ByteArray? {
    return Hex.decode(this)
}


object Base64X {
    fun encode(data: ByteArray, flag: Int = Base64.DEFAULT): String {
        val arr = Base64.encode(data, flag)
        return String(arr)
    }

    fun encodeSafe(data: ByteArray): String {
        val arr = Base64.encode(data, Base64.URL_SAFE)
        return String(arr)
    }

    fun decode(data: ByteArray, flag: Int = Base64.DEFAULT): ByteArray {
        return Base64.decode(data, flag)
    }

    fun decodeSafe(data: ByteArray): ByteArray {
        return Base64.decode(data, Base64.URL_SAFE)
    }

    fun decode(s: String, flag: Int = Base64.DEFAULT): ByteArray {
        return Base64.decode(s.toByteArray(Charsets.ISO_8859_1), flag)
    }

    fun decodeSafe(s: String): ByteArray {
        return Base64.decode(s.toByteArray(Charsets.ISO_8859_1), Base64.URL_SAFE)
    }
}


/**
 * Created by entaoyang@163.com on 16/4/27.
 * https://my.oschina.net/Jacker/blog/86383
 * cipher:  AES/ECB/NoPadding等
 *
 * 算法/模式/填充                16字节加密后数据长度        不满16字节加密后长度
 * AES/CBC/NoPadding             16                          不支持
 * AES/CBC/PKCS5Padding          32                          16
 * AES/CBC/ISO10126Padding       32                          16
 * AES/CFB/NoPadding             16                          原始数据长度
 * AES/CFB/PKCS5Padding          32                          16
 * AES/CFB/ISO10126Padding       32                          16
 * AES/ECB/NoPadding             16                          不支持
 * AES/ECB/PKCS5Padding          32                          16
 * AES/ECB/ISO10126Padding       32                          16
 * AES/OFB/NoPadding             16                          原始数据长度
 * AES/OFB/PKCS5Padding          32                          16
 * AES/OFB/ISO10126Padding       32                          16
 * AES/PCBC/NoPadding            16                          不支持
 * AES/PCBC/PKCS5Padding         32                          16
 * AES/PCBC/ISO10126Padding      32                          16
 */
class AES(val cipher: String) {

    fun encode(key: ByteArray, data: ByteArray): ByteArray {
        return doAES(Cipher.ENCRYPT_MODE, key, data)
    }


    fun decode(key: ByteArray, data: ByteArray): ByteArray {
        return doAES(Cipher.DECRYPT_MODE, key, data)
    }

    private fun doAES(mode: Int, key: ByteArray, data: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(cipher)
        cipher.init(mode, SecretKeySpec(key, "AES"))
        return cipher.doFinal(data)
    }

    companion object {
        val ECB = AES("AES/ECB/NoPadding")
        val ECB_PKCS5 = AES("AES/ECB/PKCS5Padding")
        val ECB_ISO10126 = AES("AES/ECB/ISO10126Padding")

        val CBC = AES("AES/CBC/NoPadding")
        val CBC_PKCS5 = AES("AES/CBC/PKCS5Padding")
        val CBC_ISO10126 = AES("AES/CBC/ISO10126Padding")

        val CFB = AES("AES/CFB/NoPadding")
        val CFB_PKCS5 = AES("AES/CFB/PKCS5Padding")
        val CFB_ISO10126 = AES("AES/CFB/ISO10126Padding")


        val OFB = AES("AES/OFB/NoPadding")
        val OFB_PKCS5 = AES("AES/OFB/PKCS5Padding")
        val OFB_ISO10126 = AES("AES/OFB/ISO10126Padding")

        val PCBC = AES("AES/PCBC/NoPadding")
        val PCBC_PKCS5 = AES("AES/PCBC/PKCS5Padding")
        val PCBC_ISO10126 = AES("AES/PCBC/ISO10126Padding")


    }

}
