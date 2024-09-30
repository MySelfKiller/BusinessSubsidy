package com.kayu.business.subsidy

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import com.daimajia.numberprogressbar.NumberProgressBar
import com.hjq.toast.ToastUtils
import com.hoom.library.base.utils.network.NetState
import com.hoom.library.common.ext.parseState
import com.hoom.library.common.ui.BaseActivity
import com.kayu.business.subsidy.data.bean.UpgradeInfo
import com.kayu.business.subsidy.databinding.ActivitySplashBinding
import com.kayu.business.subsidy.ui.login.LoginActivity
import com.kayu.business.subsidy.ui.login.SetPasswordActivity
import com.kayu.utils.*
import com.kayu.utils.location.LocationManagerUtil
import com.kayu.utils.status_bar_set.StatusBarUtil
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener
import com.kongzue.dialog.util.BaseDialog
import com.kongzue.dialog.v3.MessageDialog
import com.maning.updatelibrary.InstallUtils
import com.tencent.mmkv.MMKV
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity: BaseActivity<ActivitySplashBinding, SplashViewModel>() {

//    private var apkDownloadPath: String? = null
    private var downloadCallBack: InstallUtils.DownloadCallBack? = null
    private var progressDialog: MessageDialog? = null
    private var progressbar: NumberProgressBar? = null

    //是否强制跳转到主页面
    private var mForceGoMain: Boolean = false

    override val mViewModel: SplashViewModel by viewModels()

    /**
     * 网络变化监听 子类重写
     */
    override fun onNetworkStateChanged(netState: NetState) {
//        if (netState.type != NetworkTypeEnum.OTHER) {
//            ToastUtils.show(if (netState.isSuccess) "网络已连接" else "网络已断开")
//        }
    }

    override fun setStatusBar() {
        StatusBarUtil.setRootViewFitsSystemWindows(this, true)
        StatusBarUtil.setTranslucentStatus(this)
//        setCommonUI(this,true)
//        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, com.hoom.library.common.R.color.common_white))
    }

    override fun ActivitySplashBinding.initView() {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
//        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            if(null != this@SplashActivity && !this@SplashActivity.isFinishing){
                mViewModel.checkUpgrade(AppUtil.getVersionName(this@SplashActivity))
            }
        },1500*1)
    }

    override fun initObserve() {
        mViewModel.upgradeInfoLiveData.observe(this){ it ->
            parseState(it,{ upgradeInfo->
                val ss: Array<String> = upgradeInfo.url.split("/".toRegex()).toTypedArray()
                val apkNme: String = ss[ss.size - 1]
                var mustUpdata = false
                var hasUpdata = false
                if (upgradeInfo.updateStatus == 3) {
                    hasUpdata = true
                } else if (upgradeInfo.updateStatus == 1) {
                    mustUpdata = true
                }else if (upgradeInfo.updateStatus == 2){//不需要更新
                    goToMainActivity()
                    return@parseState
                }
                if (hasUpdata || mustUpdata) {
                    if (StringUtil.isEmpty(upgradeInfo.url)) {
                        goToMainActivity()
                        return@parseState
                    }
                    updateDialog(mustUpdata, apkNme,upgradeInfo)
                }
            },{
                LogUtil.e(" upgradeInfoLiveData failed ",it.errorMsg)
                //请求出问题也直接跳转主页
                goToMainActivity()
            })
        }
    }

    override fun initRequestData() {
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        //非默认值
        if (newConfig.fontScale != 1f) {
            getResources()
        }
        super.onConfigurationChanged(newConfig)
    }

    override fun getResources(): Resources { //还原字体大小
        val res: Resources = super.getResources()
        //非默认值
        if (res.getConfiguration().fontScale != 1f) {
            val newConfig = Configuration()
            newConfig.setToDefaults() //设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics())
        }
        return res
    }

    override fun onResume() {
        //判断是否跳转到主页面
        if (mForceGoMain) {
            goToMainActivity()
        }
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
//        mForceGoMain = true
    }

    /**
     * 跳转到主页面
     */
    private fun goToMainActivity() {
        val kv = MMKV.defaultMMKV()
        val isLogin = kv.decodeBool(Constants.isLogin,false)
//        val isSetPsd: Boolean = kv.decodeBool(Constants.isSetPsd, false)
//        val isLogin= true
//        val isSetPsd = true
        val intent: Intent? = if (isLogin) {
    //            if (isSetPsd) {
            Intent(this@SplashActivity, MainActivity::class.java)
    //            } else {
    //                intent = Intent(this@SplashActivity, SetPasswordActivity::class.java)
    //                intent.putExtra("title", "设置密码")
    //                intent.putExtra("back", "")
    //                intent.putExtra("isSetPwd", true)
    //            }
        } else {
            Intent(this@SplashActivity, LoginActivity::class.java)
        }
        startActivity(intent)
        finish()
    }


    fun updateDialog(isMustUpdate: Boolean, apkName: String,upgradeInfo: UpgradeInfo) {
        val file = File(
            SMApplication.instance.dataPath + File.separator + "apk" + File.separator + apkName
        )
        val messageDialog: MessageDialog = MessageDialog.build(this@SplashActivity)
        val filMd5: String = Md5Util.getFileMD5(file)
        val md5Eq: Boolean = upgradeInfo.md5.let { StringUtil.equals(filMd5, it) }
        val fileLength: Long = file.length()
        val lengthEq: Boolean = fileLength == upgradeInfo.length.trim().toLong()
        if (file.exists() && lengthEq && md5Eq) {
            messageDialog.setTitle("更新APP")
            messageDialog.setMessage("新版本已下载,请安装！")
            messageDialog.setOkButton("安装")
            if (!isMustUpdate) {
                messageDialog.setCancelButton("取消")
                messageDialog.setCancelButton { baseDialog: BaseDialog?, v: View? ->
                    messageDialog.doDismiss()
                    goToMainActivity()
                    false
                }
            }
            messageDialog.setCancelable(!isMustUpdate)
            messageDialog.setOkButton { baseDialog: BaseDialog?, v: View? ->
                messageDialog.doDismiss()
                SMApplication.instance.checkPermission(this@SplashActivity,arrayListOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),{
                    installApk(file.absolutePath)
                },{
                    ToastUtils.show("权限被拒绝，即将退出应用")
                    finish()
                })
                false
            }
        } else {
            if (file.exists()) file.delete()
            messageDialog.setTitle("检测到新版")
            messageDialog.setMessage(upgradeInfo.updateTips)
            messageDialog.setOkButton("立即更新")
            if (!isMustUpdate) {
                messageDialog.setCancelButton("下次再说")
                messageDialog.setCancelButton { baseDialog: BaseDialog?, v: View? ->
                    messageDialog.doDismiss()
                    false
                }
            }
            messageDialog.setCancelable(false)
            messageDialog.setOkButton { baseDialog, v ->
                messageDialog.doDismiss()
                SMApplication.instance.checkPermission(this@SplashActivity,arrayListOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),{
                    initCallBack()
                    showProgressDialog()
                    InstallUtils.with(this@SplashActivity) //必须-下载地址
                        .setApkUrl(upgradeInfo.url) //非必须-下载保存的文件的完整路径+name.apk
                        .setApkPath(
                            SMApplication.instance.dataPath + File.separator + "apk" + File.separator + apkName
                        ) //非必须-下载回调
                        .setCallBack(downloadCallBack) //开始下载
                        .startDownload()
                },{
                    ToastUtils.show("权限被拒绝，即将退出应用")
                    finish()
                })
                false
            }
        }
        messageDialog.show()
    }


    private fun showProgressDialog() {
        progressDialog = MessageDialog.build(this@SplashActivity)
        progressDialog?.title = "下载中..."
        progressDialog?.setOkButton(null as String?)
        progressDialog?.setCustomView(R.layout.progress_lay, object : MessageDialog.OnBindView {
            override fun onBind(dialog: MessageDialog, v: View) {
                progressbar = v.findViewById(R.id.progressbar)
            }
        })
        progressDialog?.cancelable = false
        progressDialog?.show()
    }

    private fun initCallBack() {
        downloadCallBack = object : InstallUtils.DownloadCallBack {
            override fun onStart() {
                progressbar!!.progress = 0
            }

            override fun onComplete(path: String) {
                progressbar!!.progress = 100
                progressDialog!!.doDismiss()

                //先判断有没有安装权限
                InstallUtils.checkInstallPermission(
                    this@SplashActivity,
                    object : InstallUtils.InstallPermissionCallBack {
                        override fun onGranted() {
                            //去安装APK
                            installApk(path)
                        }

                        override fun onDenied() {
                            //弹出弹框提醒用户
                            MessageDialog.show(
                                this@SplashActivity,
                                "安装授权提示",
                                "必须授权才能安装APK，请设置允许安装",
                                "设置"
                            )
                                .setCancelable(false)
                                .setOkButton(object : OnDialogButtonClickListener {
                                    public override fun onClick(
                                        baseDialog: BaseDialog,
                                        v: View
                                    ): Boolean {
                                        baseDialog.doDismiss()
                                        //打开设置页面
                                        InstallUtils.openInstallPermissionSetting(
                                            this@SplashActivity,
                                            object : InstallUtils.InstallPermissionCallBack {
                                                public override fun onGranted() {
                                                    //去安装APK
                                                    installApk(path)
                                                }

                                                public override fun onDenied() {
                                                    //还是不允许咋搞？
//                                                    appManager!!.finishAllActivity()
                                                    finish()
                                                }
                                            })
                                        return false
                                    }
                                })
                        }
                    })
            }

            override fun onLoading(total: Long, current: Long) {
                //内部做了处理，onLoading 进度转回progress必须是+1，防止频率过快
                val progress: Int = (current * 100 / total).toInt()
                progressbar!!.progress = progress
            }

            override fun onFail(e: Exception) {
                progressDialog!!.doDismiss()
                LogUtil.e("hm", "下载失败" + e.toString())
                ToastUtils.show("下载失败")
                finish()
            }

            override fun cancle() {
                progressDialog!!.doDismiss()
                ToastUtils.show("下载已取消")
                finish()
            }
        }
    }

    private fun installApk(path: String?) {
        InstallUtils.installAPK(this@SplashActivity, path, object : InstallUtils.InstallCallBack {
            override fun onSuccess() {
                //onSuccess：表示系统的安装界面被打开
                //防止用户取消安装，在这里可以关闭当前应用，以免出现安装被取消
//                appManager!!.finishAllActivity()
                installApk(path)
                finish()
            }

            override fun onFail(e: Exception) {
                ToastUtils.show("安装失败")
                finish()
            }
        })
    }
}