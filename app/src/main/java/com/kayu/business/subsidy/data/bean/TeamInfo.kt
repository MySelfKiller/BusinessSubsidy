package com.kayu.business.subsidy.data.bean

data class TeamInfo(
    //teamInfo
    //	total		直推人数总量
    //	ydTotal		直推昨日新增数量
    //	teamTotal		团队总数量
    //	ydTeamTotal		昨日新增数量

    val total: Int,         //直推人数总量
    val ydTotal: Int,       //直推昨日新增数量
    val teamTotal: Int,     //团队总数量
    val ydTeamTotal: Int    //昨日新增数量
)