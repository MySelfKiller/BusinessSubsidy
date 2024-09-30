package com.kayu.business.subsidy.data.bean

import com.google.gson.annotations.SerializedName
import com.kayu.business.subsidy.data.bean.CashOutBean

data class CashOutListBean(
    @field:SerializedName("total") val total:Int,
    @field:SerializedName("list") val list:MutableList<CashOutBean>,
    @field:SerializedName("pages") val pages:Int
)
