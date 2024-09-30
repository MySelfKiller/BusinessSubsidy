package com.kayu.business.subsidy.data.bean

data class DebitCardBean (
    var id //主键
            : Long = 0,
    var bankName //银行名称
            : String = "",
    var cardNo //卡号
            : String = "",
    var logo //银行logo
            : String = ""
)