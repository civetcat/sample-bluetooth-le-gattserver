package com.example.androidthings.gattserver

import android.text.TextUtils

object ConvertData {
    fun stringToByteArray(string: String): ByteArray {
        val charset = Charsets.UTF_8
        return string.toByteArray(charset)
    }

    fun bytesToHex(bytes: ByteArray): String {
        val hexArray = "0123456789ABCDEF".toCharArray()
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v = bytes[j].toInt() and 0xFF

            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }

    fun transferForPrint(bytes: ByteArray): String {
        return transferForPrint(String(bytes))
    }

    fun transferForPrint(src: String): String {
        var str = src
        if (TextUtils.isEmpty(str)) return str
        str = str.replace('\r', ' ')
        str = str.replace('\n', ' ')
        if (str.endsWith(">")) {
            str = str.substring(0, str.length - 1)
        }
        return str
    }

}