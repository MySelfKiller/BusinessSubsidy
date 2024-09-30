package com.kayu.business.subsidy.data.bean

import com.google.gson.annotations.SerializedName

data class ShopList(
    @field:SerializedName("total") val total:Int = 0,
    @field:SerializedName("pages") val pages:Int = 0,
    @field:SerializedName("list") val list:MutableList<TeamItemBean>,
)
