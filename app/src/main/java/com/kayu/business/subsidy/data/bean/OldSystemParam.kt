package com.kayu.business.subsidy.data.bean

import com.google.gson.annotations.SerializedName

data class OldSystemParam(
//{"id":10,"title":"公众号","content":"https://www.kakayuy.net/images/consum.jpg","url":"","state":1,"type":1,"pathMd5":null,"pathLength":0,"force":null}
    @field:SerializedName("id") val id:Long = 0,
    @field:SerializedName("title") val title:String = "",
    @field:SerializedName("content") val content:String = "",
    @field:SerializedName("url") val url:String = "",
    @field:SerializedName("state") val state:Int = 0,
    @field:SerializedName("type") val type:Int = 0,
    @field:SerializedName("pathMd5") val pathMd5:String = "",
    @field:SerializedName("pathLength") val pathLength:Long = 0,
    @field:SerializedName("force") val force:String = "",
)
