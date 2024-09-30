package com.kayu.business.subsidy.data.bean

import com.google.gson.annotations.SerializedName

data class IncomeList(
    @field:SerializedName("total") val total:Int,
    @field:SerializedName("list") val list:MutableList<Income>,
    @field:SerializedName("pages") val pages:Int
)
