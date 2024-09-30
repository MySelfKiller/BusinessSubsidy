package com.kayu.business.subsidy.data.bean

data class MessageBean(
    //id	Long	主键
    //title	String	消息标题(可为空)
    //first	String	首行批注(可为空)
    //content	String	消息内容(不为空)
    //remark	String	备注(可为空)
    //type	Int	消息类型0:系统消息 1:平台消息(不为空)
    //redirectUrl	String	跳转地址(可为空)
    //createTime	String	创建时间(不为空)


    //                "id": 21,
    //                "title": {
    //                    "color": "#dd1717",
    //                    "value": "佣金发放通知"
    //                },
    //                "first": null,
    //                "content": [
    //                    {
    //                        "color": "#000",
    //                        "title": "奖励金额啊",
    //                        "value": "240元"
    //                    },
    //                    {
    //                        "color": "#000",
    //                        "title": "客户名称",
    //                        "value": "积极发动机卡拉房间看电视了技法卢卡斯的发动机离开洒家分厘卡的撒尽快发来的数据 发的艰苦拉萨JFK的拉萨解放开绿灯撒就发"
    //                    },
    //                    {
    //                        "color": "#000",
    //                        "title": "银行名称",
    //                        "value": "交通银行"
    //                    }
    //                ],
    //                "remark": {"color":"#47b52d","value":"备注：关注公众号获取消息实时推送"},
    //                "type": 0,
    //                "redirectUrl": null,
    //                "createTime": "2023-08-28 15:40:58"

    val id: Long,    //主键
    var title: MsgTitleBean?,        //消息标题(可为空)
    var first: MsgTitleBean?,         //首行批注(可为空)
    var content: MutableList<MsgContentBean>?,   //消息内容(不为空)
    var remark: MsgTitleBean?,   //备注(可为空)
    var type: Int,   //消息类型0:系统消息 1:平台消息(不为空)
    var redirectUrl: String?,   //跳转地址(可为空)
    var createTime: String?,   //创建时间(不为空)
)
