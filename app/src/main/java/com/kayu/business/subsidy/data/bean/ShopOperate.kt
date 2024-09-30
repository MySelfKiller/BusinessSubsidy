package com.kayu.business.subsidy.data.bean

import com.google.gson.annotations.SerializedName

data class ShopOperate(
    @field:SerializedName("shopTotal") val shopTotal:Int = 0,      //门店总数
    @field:SerializedName("orderTotal") val orderTotal:Int = 0     //订单总数
)
