package com.kayu.business.subsidy.data.bean

data class NoticeList(
    //	total		Int	总条数
    //	pages		Int	总页数
    //	list		Array	集合
    var list: MutableList<NoticeBean>? = null,
    var pages: Int = 0,
    var total: Int = 0
)