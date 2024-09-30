package com.kayu.business.subsidy.data.bean

import com.google.gson.annotations.SerializedName

data class Income(
    //title	String	奖励标题
    //amount	String	金额/元
    //createTime	String	创建时间
    @field:SerializedName("title") val title:String = "",
    @field:SerializedName("amount") val amount:String = "",
    @field:SerializedName("createTime") val createTime:String = ""
)
