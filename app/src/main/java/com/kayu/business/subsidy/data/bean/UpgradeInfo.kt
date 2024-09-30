package com.kayu.business.subsidy.data.bean

import com.google.gson.annotations.SerializedName

data class UpgradeInfo(
    //minimumVersion	String	最小版本号
    //maximumVersion	String	最大版本号
    //updateStatus	Int	更新状态 1：必须更新 2:不更新 3:选择更新
    //updateTips	String	更新提示
    //md5	String	M5d
    //length	String	大小
    //url	String	下载地址
    @field:SerializedName("minimumVersion") val minimumVersion:String,
    @field:SerializedName("maximumVersion") val maximumVersion:String,
    @field:SerializedName("updateStatus") val updateStatus:Int,
    @field:SerializedName("updateTips") val updateTips:String,
    @field:SerializedName("md5") val md5:String,
    @field:SerializedName("length") val length:String,
    @field:SerializedName("url") val url:String,
)
