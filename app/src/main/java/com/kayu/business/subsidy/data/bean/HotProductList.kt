package com.kayu.business.subsidy.data.bean

data class HotProductList(
    //title			标题
    //tips			提示
    //productList		List	产品列表
    val productList: MutableList<ProductItemBean>? = null,
    val tips: String = "",
    val title: String = ""
)