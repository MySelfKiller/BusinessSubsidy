package com.kayu.business.subsidy.data.bean

import com.google.gson.annotations.SerializedName

data class TeamItemBean(
    // "id": 31,
    //                "username": "小明",
    //                "phone": "17612345678",
    //                "wxnum": null,
    //                "createTime": "2019-10-30 11:40:11",
    //                "rewardAmt": 10000

    @field:SerializedName("id") val id:Long = 0,//主键
    @field:SerializedName("username") val username:String = "",//用户名
    @field:SerializedName("phone") val phone:String = "",//手机号
    @field:SerializedName("wxnum") val wxnum:String = "",//微信号
    @field:SerializedName("createTime") val createTime:String = "",//创建时间
    @field:SerializedName("rewardAmt") val rewardAmt:Int = 0,//累计佣金/分
)
