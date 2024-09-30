package com.kayu.business.subsidy.data.bean

data class OrderInfo (
    var total: Int = 0,//全部数据总条数
    var unexecuted: Int = 0,//未完成数据
    var passed: Int = 0,//待激活面签
    var execute: Int = 0,//已完成数据
    var cancel: Int = 0,//已作废数据
    var pageResult: OrderPageData? = null
)