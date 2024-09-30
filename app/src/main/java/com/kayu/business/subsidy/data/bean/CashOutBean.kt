package com.kayu.business.subsidy.data.bean

data class CashOutBean(
    //id	Long	主键
    //bankNo	String	银行卡号
    //amount	Long	提现金额
    //settleFee	Long	手续费
    //actualAmount	Long	实到金额
    //state	Int	状态(见stateTitle)
    //remarks	String	备注
    //createTime	String	创建时间
    //settleTime	String	打款时间
    //stateTitle	String	状态标题
    //bankName	String	银行名
    //username	String	用户名
    val id : Long,              //主键
    val bankNo : String,            //主键
    val remarks : String,            //备注
    val createTime : String,            //创建时间
    val settleTime : String,            //打款时间
    val stateTitle : String,            //状态标题
    val bankName : String,            //银行名
    val username : String,            //用户名
    val amount : Long,              //提现金额
    val settleFee : Long,              //手续费
    val actualAmount : Long,              //实到金额
    val state : Int,              //状态(见stateTitle)
)
