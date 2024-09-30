package com.kayu.business.subsidy.data.bean

data class ItemActivityBean(
    //"id": 2,
    //"title": "活动1",
    //"pic": "https://www.kakayuy.net/images/activity.gif",
    //"url": "http://dms.sslm01.com/sslm/static/index.html",
    //"type": 1,
    //"sort": 1
    var id //主键
    : Long = 0,
    var title //标题
    : String = "",
    var pic //背景图
    : String = "",
    var url //跳转链接
    : String = ""
)
