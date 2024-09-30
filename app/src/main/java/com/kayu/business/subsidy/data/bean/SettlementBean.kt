package com.kayu.business.subsidy.data.bean

data class SettlementBean (
    //id	Long	主键
    //content	String	奖励内容(可能为空)
    //productName	String	产品名称(可能为空)
    //name	String	客户姓名(可能为空)
    //amount	Long	金额/分
    //type	Int	类型(暂定)
    //createTime	String	创建时间
    var id: Long = 0,       //主键
    var content: String = "",       //奖励内容(可能为空)
    var productName: String = "",   //产品名称(可能为空)
    var name: String = "",          //客户姓名(可能为空)
    var amount: Long = 0,            //金额/分
    var type: Int = 0,        //类型(暂定)
    var createTime: String = ""     //创建时间
)