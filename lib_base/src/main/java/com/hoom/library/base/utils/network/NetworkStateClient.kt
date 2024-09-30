package com.hoom.library.base.utils.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkRequest
import androidx.annotation.RequiresPermission
import com.kayu.utils.LogUtil
import com.kunminx.architecture.ui.callback.UnPeekLiveData
import com.hoom.library.base.BaseApplication

/**
 * 网络状态监听
 */
object NetworkStateClient {
    var mNetworkState : UnPeekLiveData<NetState>? = null
    private val mNetworkCallback = NetworkCallbackImpl()

    /**
     * 注册网络监听客户端
     * @return Unit
     */
    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    fun register() {
        val build = NetworkRequest.Builder().build()
        val cm =
            BaseApplication.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        cm.registerNetworkCallback(build, mNetworkCallback)
        LogUtil.e("hm","NetworkStateClient register")

    }
    init {
        mNetworkState = mNetworkCallback.changeLiveData
    }
}