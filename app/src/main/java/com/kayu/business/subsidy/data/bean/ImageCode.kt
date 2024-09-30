package com.kayu.business.subsidy.data.bean

import com.google.gson.annotations.SerializedName

data class ImageCode(
    //isCaptcha	String	是否需要验证码 true:需要 false:不需要
    //imageBase64	String	图片base64
    @field:SerializedName("isCaptcha") val isCaptcha:Boolean,
    @field:SerializedName("imageBase64") val imageBase64:String
)
