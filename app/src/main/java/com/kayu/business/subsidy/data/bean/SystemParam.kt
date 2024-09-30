package com.kayu.business.subsidy.data.bean

import com.google.gson.annotations.SerializedName

data class SystemParam(
    @field:SerializedName("id") val id:Long = 0,
    @field:SerializedName("title") val title:String = "",
    @field:SerializedName("h1") val h1:String = "",
    @field:SerializedName("h2") val h2:String = "",
    @field:SerializedName("data0") val data0:String = "",
    @field:SerializedName("data1") val data1:String = "",
    @field:SerializedName("data2") val data2:String = "",
    @field:SerializedName("data3") val data3:String = "",
    @field:SerializedName("data4") val data4:String = "",
    @field:SerializedName("url0") val url0:String = "",
    @field:SerializedName("url1") val url1:String = "",
    @field:SerializedName("groupNo") val groupNo:Int = 0,
    @field:SerializedName("isExposed") val isExposed:Int = 0
)
