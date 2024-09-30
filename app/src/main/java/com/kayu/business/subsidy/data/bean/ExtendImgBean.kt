package com.kayu.business.subsidy.data.bean

data class ExtendImgBean(
    //	img	String	图片
    //	isQrcode	String	是否为画码 0:否(img为链接) 1:是(img为base64)
    var id : Long = 0,
    var img: String ="",
    var isQrcode: Int = -1
)