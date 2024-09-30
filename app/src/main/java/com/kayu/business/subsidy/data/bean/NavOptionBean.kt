package com.kayu.business.subsidy.data.bean

data class NavOptionBean(
    //id	Long	主键
    //title	String	标题
    //redirect	String	重定向(类型为 0 此值固定减 redirect 映射列表)
    //type	Int	类型 0:app、1:url、2:img、3:tokenUrl
    //icon	String	图标
    //bgImg	String	背景图(V1.0.19)
    //
    //redirect固定值	说明
    //myOrder	我的订单
    //reward	收益明细
    //withdraw	体现
    //teamStatistics	团队统计
    //teamList	团队列表
    //product	产品列表
    //message	消息列表
    //teamSubsidy	团队补贴
    //teamPublicize	团队推广
    //orderStatistics	订单统计
    //settings	设置

    //            "id": 1,
    //            "title": "我的订单",
    //            "icon": "https://www.kakayuy.net/images/myorder.png",
    //            "redirect": "myOrder",
    //            "type": 0
    val title: String,          //标题
    val icon: String,           //图标
    val bgImg: String,          //背景图
    val redirect: String,       //重定向(类型为 0 此值固定减 redirect 映射列表)
    val type: Int,              //类型 0:app、1:url、2:img、3:tokenUrl
    val id: Long                //主键
)
