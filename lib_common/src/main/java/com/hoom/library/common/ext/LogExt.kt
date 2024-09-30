package com.hoom.library.common.ext

import com.kayu.utils.LogUtil

const val TAG = "hm-log"


private enum class LEVEL {
    V, D, I, W, E
}

fun String.logv(tag: String = TAG) =
    log(LEVEL.V, tag, this)
fun String.logd(tag: String = TAG) =
    log(LEVEL.D, tag, this)
fun String.logi(tag: String = TAG) =
    log(LEVEL.I, tag, this)
fun String.logw(tag: String = TAG) =
    log(LEVEL.W, tag, this)
fun String.loge(tag: String = TAG) =
    log(LEVEL.E, tag, this)

private fun log(level: LEVEL, tag: String, message: String) {
    when (level) {
        LEVEL.V -> LogUtil.v(tag, message)
        LEVEL.D -> LogUtil.d(tag, message)
        LEVEL.I -> LogUtil.i(tag, message)
        LEVEL.W -> LogUtil.w(tag, message)
        LEVEL.E -> LogUtil.e(tag, message)
    }
}