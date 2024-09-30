package com.kayu.business.subsidy.data.bean

data class StatisticData(
    //tmTeamSettleCount			团队总业绩
    //ranks			等级集合
    //	rank		等级
    //	beginCount		开始数量
    //	endCount		结束数量
    //	isCurrent		是否当前等级 0:否 1:是
    //tdAchievementStatistics			今日业绩
    //	myApplyCount		我的进件
    //	teamApplyCount		团队进件
    //	mySettleCount		我的结算
    //	teamSettleCount		团结结算
    //tmAchievementStatistics			本月业绩
    //	myApplyCount		我的进件
    //	teamApplyCount		团队进件
    //	mySettleCount		我的结算
    //	teamSettleCount		团结结算

    val tmTeamSettleCount:Int = 0,                          //团队总业绩
    val ranks:MutableList<RankData>,                        //等级集合
    val tdAchievementStatistics:StatisticTodayData,         //今日业绩
    val tmAchievementStatistics:StatisticMonthData,         //本月业绩


)