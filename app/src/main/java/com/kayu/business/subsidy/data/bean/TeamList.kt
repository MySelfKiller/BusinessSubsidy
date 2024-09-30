package com.kayu.business.subsidy.data.bean

data class TeamList(
    //	total		Int	总条数
    //	pages		Int	总页数
    //	list		Array	集合
    val list: MutableList<TeamUserBean>? = null,
    val pages: Int = 0,
    val total: Int = 0
)