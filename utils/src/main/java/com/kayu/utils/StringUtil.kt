package com.kayu.utils

import java.text.DecimalFormat

object StringUtil {
    /**
     * return if str is empty
     *
     * @param str
     * @return
     */
    fun isEmpty(str: String?): Boolean {
        return str.isNullOrEmpty() || str.equals(
                "null",
                ignoreCase = true
            ) || str.isEmpty() || str == ""
    }

    fun getTrimedString(s: String?): String {
        return trim(s)
    }

    fun trim(s: String?): String {
        return s?.trim { it <= ' ' } ?: ""
    }

    fun equals(str1: String, str2: String): Boolean {
        return equalsNotNull(str1, str2) || str1 === str2
    }

    private fun equalsNotNull(str1: String?, str2: String): Boolean {
        return str1 != null && str1 == str2
    }

    fun isBlank(str: String?): Boolean {
        return str == null || str.trim { it <= ' ' }.isEmpty()
    }

    fun getHostName(urlString: String): String {
        var urlString = urlString
        var head = ""
        var index = urlString.indexOf("://")
        if (index != -1) {
            head = urlString.substring(0, index + 3)
            urlString = urlString.substring(index + 3)
        }
        index = urlString.indexOf("/")
        if (index != -1) {
            urlString = urlString.substring(0, index + 1)
        }
        return head + urlString
    }

    fun getDataSize(var0: Long): Any {
        val var2 = DecimalFormat("###.00")
        return if (var0 < 1024L)
            var0.toString() + "bytes"
        else if (var0 < 1048576L)
            var2.format((var0.toFloat() / 1024.0f).toDouble())+"KB"
        else if (var0 < 1073741824L)
            var2.format((var0.toFloat() / 1024.0f / 1024.0f).toDouble())+"MB"
        else if (var0 < 0L)
            var2.format((var0.toFloat() / 1024.0f / 1024.0f / 1024.0f).toDouble())+"GB"
        else "error"
    }
}