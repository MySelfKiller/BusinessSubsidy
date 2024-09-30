package com.hoom.library.common.ui

import android.content.res.Configuration
import android.content.res.Resources
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.hoom.library.base.ActivityStackManager
import com.hoom.library.base.mvvm.BaseFrameActivity
import com.hoom.library.base.mvvm.BaseViewModel
import com.hoom.library.base.utils.AndroidBugFixUtils
import com.hoom.library.base.utils.BarUtils
import com.hoom.library.common.R
import com.kayu.utils.LogUtil
import com.kayu.utils.status_bar_set.StatusBarUtil
import com.kayu.utils.status_bar_set.StatusBarUtil.setCommonUI
import com.kongzue.dialog.v3.WaitDialog

/**
 * Activity基类
 *
 */
abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel> : BaseFrameActivity<VB, VM>() {

    override fun onConfigurationChanged(newConfig: Configuration) {
        //非默认值
        if (newConfig.fontScale != 1f) {
            resources
        }
        super.onConfigurationChanged(newConfig)
    }

    override fun getResources(): Resources { //还原字体大小
        val res: Resources = super.getResources()
        //非默认值
        if (res.configuration.fontScale != 1f) {
            val newConfig = Configuration()
            newConfig.setToDefaults() //设置默认
            res.updateConfiguration(newConfig, res.displayMetrics)
        }
        return res
    }

    /**
     * 设置状态栏
     * 子类需要自定义时重写该方法即可
     * @return Unit
     */
    override fun setStatusBar() {
        StatusBarUtil.setRootViewFitsSystemWindows(this, true)
        StatusBarUtil.setTranslucentStatus(this)
        setCommonUI(this,true)
//        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.common_white))
//        BarUtils.setNavBarVisibility(this,true)
    }

    override fun onResume() {
        super.onResume()
        LogUtil.d("ActivityLifecycle", "ActivityStack: ${ActivityStackManager.activityStack}")
    }

    override fun onDestroy() {
        super.onDestroy()
        // 解决某些特定机型会触发的Android本身的Bug
        AndroidBugFixUtils().fixSoftInputLeaks(this)
    }

    /**
     * 打开等待框
     */
    override fun showLoading(message: String) {
        WaitDialog.show(this,message)
    }

    /**
     * 关闭等待框
     */
    override fun dismissLoading() {
        WaitDialog.dismiss()
    }
}