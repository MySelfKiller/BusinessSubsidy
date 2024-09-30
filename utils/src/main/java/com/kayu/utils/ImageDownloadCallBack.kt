package com.kayu.utils

interface ImageDownloadCallBack {
    fun onFinished(fileName: Array<String?>?)
    fun onFailed(message: String?)
}