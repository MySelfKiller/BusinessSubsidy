package com.kayu.business.subsidy.data.bean

data class StatisticMonthData(
    //tmAchievementStatistics			本月业绩
    //	myApplyCount		我的进件
    //	teamApplyCount		团队进件
    //	mySettleCount		我的结算
    //	teamSettleCount		团结结算

    val myApplyCount:Int = 0,           //我的进件
    val teamApplyCount:Int = 0,         //团队进件
    val mySettleCount:Int = 0,          //我的结算
    val teamSettleCount:Int = 0         //团结结算


)