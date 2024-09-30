package com.kayu.business.subsidy.data.bean


data class IncomeStaBean (
    //tdSumAmt	Long	本日总收益
    //tmSumAmt	Long	本月总收益
    //lmSumAmt	Long	上月总收益

    var tdSumAmt: Long = 0,          //本日总收益/分
    var tmSumAmt: Long = 0,          //本月总收益/分
    var lmSumAmt: Long = 0,          //上月总收益/分

)