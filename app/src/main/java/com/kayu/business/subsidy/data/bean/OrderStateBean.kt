package com.kayu.business.subsidy.data.bean

data class OrderStateBean(
    //title	String	状态标题
    //isCurrent	Int	是否选中 0:否 1:是
    //tips	String	状态提示

    //"title": "已进件",
    //"num": 2,
    //"status": 7,
    //"isCurrent": 1,
    //"tips": "等待银行数据更新\n注:部分银行只更新初审或激活或首刷等状态，如订单长时间待确认，则有可能未通过或者未完成实际申请等原因"
    val title: String?,  //状态标题
    val tips: String?,   //状态提示
    val num: Int,       //排序
    val status: Int,    //状态
    val isCurrent: Int, //是否选中 0:否 1:是
)
