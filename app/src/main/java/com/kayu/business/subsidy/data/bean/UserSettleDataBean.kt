package com.kayu.business.subsidy.data.bean

/**
 * Author by killer, Email xx@xx.com, Date on 2022/12/05.
 * PS: Not easy to write code, please indicate.
 */
data class UserSettleDataBean (
    //
    //tdCount	Int	当日结算量
    //ydCount	Int	昨日结算量
    //tmCount	Int	本月结算量
    //tdTeamCount	Int	当日团队结算量
    //ydTeamCount	Int	昨日团队结算量
    //tmTeamCount	Int	本月团队结算量
    var tdCount //直推总人数
    : Int = 0,
    var ydCount //团队总人数
    : Int = 0,
    var tmCount //本月结算总数
    : Int = 0,
    var tdTeamCount //直推总人数
    : Int = 0,
    var ydTeamCount //团队总人数
    : Int = 0,
    var tmTeamCount //本月结算总数
    : Int = 0
)