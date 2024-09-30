package com.kayu.business.subsidy.data.bean

/**
 * 2个数据公用一个模型
 */
data class TeamAchiDataBean (
    // count	Int	团队总人数
    //tmSettleCount	Int	本月结算数量/张
    //ydTeamSettleCount	Int	昨日结算数量/张
    //tmTeamSettleAmt	Long	本月结算金额/分

    //	tmSettleCount		本月结算数量/张
    //	ydSettleCount		昨日结算数量/张
    //	tmTeamSettleAmt		团队本月结算金额
    //	tdTeamSettleAmt		团队昨日结算金额

    val count: Int,                 //团队总人数
    val tmSettleCount: Int,         //本月结算数量/张
    val ydSettleCount: Int,         //昨日结算数量/张
    val ydTeamSettleCount: Int,     //昨日结算数量/张
    val tmTeamSettleAmt: Long,      //团队本月结算金额
    val tdTeamSettleAmt: Long,      //团队昨日结算金额
)