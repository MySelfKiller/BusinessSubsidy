package com.kayu.utils

/**
 * @author Hom
 * @deprecated Kotlin处理中文和Unicode互转工具类
 */
object UnicodeUtils {
    /**
     * 将字符串转成Unicode编码，包括但不限于中文
     *
     * @param src 原始字符串，包括但不限于中文
     * @return Unicode编码字符串
     */
    fun decode(src: String): String {
        val builder = StringBuilder()
        for (element in src) {
            // 如果你的Kotlin版本低于1.5，这里 element.code 会报错 找不到方法,请替换成:
            // Kotlin < 1.5
            // var s = Integer.toHexString(element.toInt())
            // Kotlin >= 1.5
            var s = Integer.toHexString(element.code)

            if (s.length == 2) {// 英文转16进制后只有两位，补全4位
                s = "00$s"
            }
            builder.append("\\u$s")
        }
        return builder.toString()
    }

    /**
     * 解码Unicode字符串，得到原始字符串
     *
     * @param unicode Unicode字符串
     * @return 解码后的原始字符串
     */
    fun encode(unicode: String): String {
        val builder = StringBuilder()
        val hex = unicode.split("\\\\".toRegex())
        for (i in 1 until hex.size) {
//            LogUtil.e("unicode 分割",hex[i])
            if (hex[i].startsWith("u")) {
                val data = hex[i].substring(1,5).toInt(16)
                builder.append(data.toChar())
//                LogUtil.e("unicode","data=$data")
                if(hex[i].length>5){
//                    val data = hex[i].substring(1,5).toInt(16)
                    val data2 = hex[i].substring(5,hex[i].length)
//                    LogUtil.e("unicode","data2=$data2")
//                    builder.append(data.toChar())
                    builder.append(data2)
                }

            } else {
                builder.append("\\"+hex[i])
//                LogUtil.e("unicode","data="+hex[i])
            }
        }
//        LogUtil.e("unicode 完成",builder.toString())
        return builder.toString()
    }
}