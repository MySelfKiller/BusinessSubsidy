package com.kayu.business.subsidy.data.bean

import com.google.gson.annotations.SerializedName

/**
 * Author by killer, Email xx@xx.com, Date on 2022/11/09.
 * PS: Not easy to write code, please indicate.
 */
data class RecommendInfoBean (
    //refuserInfo			推荐人信息
    //	headPic		头像
    //	username		姓名(可能为空)
    //	phone		手机号
    //	wxnum		微信号
    var headPic:String = "",
    var username:String = "",
    val phone:String ="",
    val wxnum:String =""

)