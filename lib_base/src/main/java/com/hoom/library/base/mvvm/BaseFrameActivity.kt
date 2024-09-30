package com.hoom.library.base.mvvm

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import com.hjq.toast.ToastUtils
import com.hoom.library.base.utils.BindingReflex
import com.hoom.library.base.utils.EventBusRegister
import com.hoom.library.base.utils.EventBusUtils
import com.hoom.library.base.utils.network.NetState
import com.hoom.library.base.utils.network.NetworkStateClient
import com.hoom.library.base.utils.network.NetworkTypeEnum
import com.kayu.utils.LogUtil

/**
 * Activity基类
 */
abstract class BaseFrameActivity<VB : ViewBinding, VM : BaseViewModel> : AppCompatActivity(),
    FrameView<VB> {

    protected val mBinding: VB by lazy(mode = LazyThreadSafetyMode.NONE) {
        BindingReflex.reflexViewBinding(javaClass, layoutInflater)
    }

    protected abstract val mViewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBar()
        setContentView(mBinding.root)
        // ARouter 依赖注入
//        ARouter.getInstance().inject(this)
        // 注册EventBus
        if (javaClass.isAnnotationPresent(EventBusRegister::class.java)) EventBusUtils.register(this)
        mBinding.initView()
        initNetworkListener()
        initObserve()
        initRequestData()
    }

    abstract fun showLoading(message: String = "请求中...")

    abstract fun dismissLoading()

    /**
     * 注册 UI 事件
     */
    private fun registorDefUIChange() {
        mViewModel.loadingChange.showDialog.observe(this, Observer {
            showLoading(it)
        })
        mViewModel.loadingChange.dismissDialog.observe(this, Observer {
            dismissLoading()
        })
    }
    /**
     * 网络变化监听 子类重写
     */
    open fun onNetworkStateChanged(netState: NetState) {
        if (netState.type != NetworkTypeEnum.OTHER) {
            ToastUtils.show(if (netState.isSuccess) "网络已连接" else "网络已断开")
        }
    }

    /**
     * 初始化网络状态监听
     * @return Unit
     */
    private fun initNetworkListener() {
        LogUtil.e("hm","BaseFrameActivity initNetworkListener")

        NetworkStateClient.mNetworkState?.observe(this) {
            onNetworkStateChanged(it)
        }
    }

    /**
     * 设置状态栏
     * 子类需要自定义时重写该方法即可
     * @return Unit
     */
    open fun setStatusBar() {}

    override fun onDestroy() {
        if (javaClass.isAnnotationPresent(EventBusRegister::class.java)) EventBusUtils.unRegister(
            this
        )
        super.onDestroy()
    }

    public override fun onConfigurationChanged(newConfig: Configuration) {
        //非默认值
        if (newConfig.fontScale != 1f) {
            getResources()
        }
        super.onConfigurationChanged(newConfig)
    }

    public override fun getResources(): Resources { //还原字体大小
        val res: Resources = super.getResources()
        //非默认值
        if (res.getConfiguration().fontScale != 1f) {
            val newConfig: Configuration = Configuration()
            newConfig.setToDefaults() //设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics())
        }
        return res
    }

//    override fun getResources(): Resources {
//        // 主要是为了解决 AndroidAutoSize 在横屏切换时导致适配失效的问题
//        // 但是 AutoSizeCompat.autoConvertDensity() 对线程做了判断 导致Coil等图片加载框架在子线程访问的时候会异常
//        // 所以在这里加了线程的判断 如果是非主线程 就取消单独的适配
//        if (Looper.myLooper() == Looper.getMainLooper()) {
//            AutoSizeCompat.autoConvertDensityOfGlobal((super.getResources()))
//        }
//        return super.getResources()
//    }
}