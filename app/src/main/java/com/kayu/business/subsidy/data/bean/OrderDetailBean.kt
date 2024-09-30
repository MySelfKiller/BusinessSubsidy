package com.kayu.business.subsidy.data.bean

data class OrderDetailBean(
    //id		Long	主键
    //name		String	名称
    //phone		String	手机号
    //createTime		String	创建时间
    //productName		String	产品名称
    //productLogo		String	产品logo
    //settlementRule		String	结算规则
    //settlementCycle		String	结算周期
    //settlementTips		String	结算提醒
    //statusUpdateTime		String	状态更新时间
    //statusNextUpdateTime		String	状态下次更新时间
    //statusList		String	申请状态集合号
    //	title	String	状态标题
    //	isCurrent	Int	是否选中 0:否 1:是
    //	tips	String	状态提示

    val id:Long,                                //主键ID
    val name: String,                           //名称
    val phone: String,                          //手机号
    val createTime: String,                     //创建时间
    val productName: String,                    //产品名称
    val productLogo: String,                    //产品logo
    val settlementRule: String,                 //结算规则
    val settlementCycle: String,                //结算周期
    val settlementTips: String,                 //结算提醒
    val statusUpdateTime: String,               //状态更新时间
    val statusNextUpdateTime: String,           //状态下次更新时间
    val statusList:MutableList<OrderStateBean>, //申请状态集合
    var progressUrl: String?,                    //订单精度查询url


)
