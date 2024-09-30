package com.kayu.business.subsidy.data.bean

data class OrderPageData (
    var list: MutableList<ItemOrderBean>,
    var pages: Int = 0,
    var total: Int = 0
)