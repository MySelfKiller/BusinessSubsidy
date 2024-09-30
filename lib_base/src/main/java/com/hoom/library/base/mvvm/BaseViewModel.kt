package com.hoom.library.base.mvvm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hoom.library.base.utils.StateLayoutEnum
import com.hoom.library.base.utils.network.EventLiveData
import kotlin.jvm.Throws

/**
 * ViewModel 基类
 */
abstract class BaseViewModel : ViewModel() {

    val loadingChange: UiLoadingChange by lazy { UiLoadingChange() }

    /**
     * 内置封装好的可通知Activity/fragment 显示隐藏加载框 因为需要跟网络请求显示隐藏loading配套才加的，不然我加他个鸡儿加
     */
    inner class UiLoadingChange {
        //显示加载框
        val showDialog by lazy { EventLiveData<String>().create() }
        //隐藏
        val dismissDialog by lazy { EventLiveData<Boolean>().create() }
    }

    /**
     * 控制状态视图的LiveData
     */
    val stateViewLD = MutableLiveData<StateLayoutEnum>()

    /**
     * 更改状态视图的状态
     *
     * @param hide Boolean 是否进行隐藏状态视图
     * @param loading Boolean 是否显示加载中视图
     * @param error Boolean 是否显示错误视图
     * @param noData Boolean 是否显示没有数据视图
     * @return Unit
     * @throws IllegalArgumentException 如果入参没有传入任何参数或者为true的参数 >1 时，会抛出[IllegalArgumentException]
     */
    @Throws(IllegalArgumentException::class)
    protected fun changeStateView(
        hide: Boolean = false,
        loading: Boolean = false,
        error: Boolean = false,
        noData: Boolean = false
    ) {
        // 对参数进行校验
        var count = 0
        if (hide) count++
        if (loading) count++
        if (error) count++
        if (noData) count++
        when {
            count == 0 -> throw IllegalArgumentException("必须设置一个参数为true")
            count > 1 -> throw IllegalArgumentException("只能有一个参数为true")
        }

        // 修改状态
        when {
            hide -> stateViewLD.postValue(StateLayoutEnum.HIDE)
            loading -> stateViewLD.postValue(StateLayoutEnum.LOADING)
            error -> stateViewLD.postValue(StateLayoutEnum.ERROR)
            noData -> stateViewLD.postValue(StateLayoutEnum.NO_DATA)
        }
    }
}