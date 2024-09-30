package com.hoom.library.common.network

/**
 * 高德天气查web询服务返回数据的基类
 */
data class ApiWeatherResponse<T>(
    //  "status": "1",
    //	"count": "1",
    //	"info": "OK",
    //	"infocode": "10000",
    //	"lives": [
    val status: String,
    val count: String,
    val info:String,
    val infocode:String,
    val msg: String,
    val lives: T)