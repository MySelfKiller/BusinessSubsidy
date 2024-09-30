package com.hoom.library.base.utils.network

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.kayu.utils.LogUtil
import com.kunminx.architecture.ui.callback.UnPeekLiveData


/**
 * 实时监听网络状态变化的[ConnectivityManager.NetworkCallback]实现类
 */
class NetworkCallbackImpl : ConnectivityManager.NetworkCallback() {

    /**
     * 当前网络类型
     */
    var currentNetworkType: NetworkTypeEnum = NetworkTypeEnum.OTHER

    /**
     * 当前网络是否已连接
     */
    var isConnected = false

    /**
     * 注册的监听
     */
//    var changeCall: NetworkStateChangeListener? = null
    var changeLiveData: UnPeekLiveData<NetState> = EventLiveData<NetState>().setAllowNullValue(false).create()
    private var netState: NetState = NetState()

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        isConnected = true
        netState.isSuccess = isConnected
        changeLiveData.postValue(netState)
        LogUtil.e("hm","NetworkCallbackImpl onAvailable")
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        isConnected = false
        netState.isSuccess = isConnected
        changeLiveData.postValue(netState)
        LogUtil.e("hm","NetworkCallbackImpl onLost")
    }

    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            currentNetworkType = networkTypeConvert(networkCapabilities)
            netState.type = currentNetworkType
//            changeLiveData?.postValue(netState)
            LogUtil.e("hm","NetworkCallbackImpl onCapabilitiesChanged------"+currentNetworkType)
        }
    }

    /**
     * 网络类型转换
     */
    private fun networkTypeConvert(networkCapabilities: NetworkCapabilities): NetworkTypeEnum {
        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                NetworkTypeEnum.TRANSPORT_CELLULAR
            }
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                NetworkTypeEnum.TRANSPORT_WIFI
            }
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> {
                NetworkTypeEnum.TRANSPORT_BLUETOOTH
            }
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                NetworkTypeEnum.TRANSPORT_ETHERNET
            }
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> {
                NetworkTypeEnum.TRANSPORT_VPN
            }
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE) -> {
                NetworkTypeEnum.TRANSPORT_WIFI_AWARE
            }
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_LOWPAN) -> {
                NetworkTypeEnum.TRANSPORT_LOWPAN
            }
            else -> NetworkTypeEnum.OTHER
        }
    }
}