package com.kayu.business.subsidy.data.bean

data class ActivityList(
    var fixedPopups //活动小图标
    : ArrayList<ItemActivityBean> = arrayListOf(),
    var tipPopups //活动弹窗
    : ArrayList<ItemActivityBean> = arrayListOf(),
    var navPopups //首页点击按钮
    : ArrayList<ItemActivityBean> = arrayListOf()
)
