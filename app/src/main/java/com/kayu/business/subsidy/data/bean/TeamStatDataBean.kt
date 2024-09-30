package com.kayu.business.subsidy.data.bean

/**
 * Author by killer, Email xx@xx.com, Date on 2022/12/05.
 * PS: Not easy to write code, please indicate.
 */
data class TeamStatDataBean (
    //teamInfo      团队信息
    //	total		直推人数总量
    //	ydTotal		直推昨日新增数量
    //	teamTotal		团队总数量
    //	ydTeamTotal		昨日新增数量
    //achievementInfo			团队业绩
    //	tmSettleCount		本月结算数量/张
    //	ydSettleCount		昨日结算数量/张
    //	tmTeamSettleAmt		团队本月结算金额
    //	tdTeamSettleAmt		团队昨日结算金额
    //refuserInfo			推荐人信息
    //	headPic		头像
    //	username		姓名(可能为空)
    //	phone		手机号
    //	wxnum		微信号
    val teamInfo: TeamInfo,                     //团队信息
    val achievementInfo: TeamAchiDataBean,      //团队业绩
    val refuserInfo: RecommendInfoBean?          //推荐人信息
)