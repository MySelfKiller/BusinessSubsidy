package com.hoom.library.base.mvvm

import androidx.viewbinding.ViewBinding

/**
 * View层基类抽象
 */
interface FrameView<VB : ViewBinding> {

    /**
     * 初始化View
     */
    fun VB.initView()

    /**
     * 订阅LiveData
     */
    fun initObserve()

    /**
     * 用于在页面创建时进行请求接口
     */
    fun initRequestData()
}