package com.hoom.library.common

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.view.Gravity
import com.google.auto.service.AutoService
import com.hjq.toast.ToastUtils
import com.hjq.toast.style.WhiteToastStyle
import com.kayu.utils.LogUtil
import com.hoom.library.base.app.ApplicationLifecycle
import com.hoom.library.base.BaseApplication
import com.hoom.library.base.constant.VersionStatus
import com.hoom.library.base.utils.ProcessUtils
import com.hoom.library.base.utils.SpUtils
import com.hoom.library.base.utils.network.NetworkStateClient
import com.kongzue.dialog.util.DialogSettings
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk

/**
 * 项目相关的Application
 */
@AutoService(ApplicationLifecycle::class)
class CommonApplication : ApplicationLifecycle {

    companion object {
        // 全局CommonApplication
        @SuppressLint("StaticFieldLeak")
        lateinit var mCommonApplication: CommonApplication
    }

    /**
     * 同[Application.attachBaseContext]
     * @param context Context
     */
    override fun onAttachBaseContext(context: Context) {
        mCommonApplication = this
    }

    /**
     * 同[Application.onCreate]
     * @param application Application
     */
    override fun onCreate(application: Application) {}

    /**
     * 同[Application.onTerminate]
     * @param application Application
     */
    override fun onTerminate(application: Application) {}

    /**
     * 主线程前台初始化
     * @return MutableList<() -> String> 初始化方法集合
     */
    override fun initByFrontDesk(): MutableList<() -> String> {
        val list = mutableListOf<() -> String>()
        // 以下只需要在主进程当中初始化 按需要调整
        if (ProcessUtils.isMainProcess(BaseApplication.context)) {
            list.add { initMMKV() }
//            list.add { initARouter() }
            list.add { initNetworkStateClient() }
            list.add { initDialog() }
        }
        list.add { initTencentBugly() }
        LogUtil.e("hm","CommonApplication initByFrontDesk()----end")
        return list
    }

    /**
     * 不需要立即初始化的放在这里进行后台初始化
     */
    override fun initByBackstage() {
        initX5WebViewCore()
    }

    /**
     * 初始化网络状态监听客户端
     * @return Unit
     */
    private fun initNetworkStateClient(): String {
        NetworkStateClient.register()
        LogUtil.e("hm","CommonApplication initNetworkStateClient()----end")
        return "NetworkStateClient -->> init complete"
    }

    private fun initDialog() : String{
        initDialogSetting()
//        LocationManagerUtil.init(BaseApplication.context)//定位初始化
        LogUtil.setIsDebug(BuildConfig.VERSION_TYPE != VersionStatus.RELEASE)
        return "DialogSetting -->> init complete"
    }
    private fun initDialogSetting() {
        ToastUtils.init(BaseApplication.application, WhiteToastStyle())
        ToastUtils.setGravity(Gravity.BOTTOM, 0, 60)
        DialogSettings.isUseBlur = true //是否开启模糊效果，默认关闭
        DialogSettings.modalDialog = false //是否开启模态窗口模式，一次显示多个对话框将以队列形式一个一个显示，默认关闭
        DialogSettings.style =
            DialogSettings.STYLE.STYLE_IOS //全局主题风格，提供三种可选风格，STYLE_MATERIAL, STYLE_KONGZUE, STYLE_IOS
        DialogSettings.theme = DialogSettings.THEME.LIGHT //全局对话框明暗风格，提供两种可选主题，LIGHT, DARK
        DialogSettings.tipTheme = (DialogSettings.THEME.LIGHT) //全局提示框明暗风格，提供两种可选主题，LIGHT, DARK
        //        DialogSettings.titleTextInfo = (TextInfo);              //全局对话框标题文字样式
//        DialogSettings.menuTitleInfo = (TextInfo);              //全局菜单标题文字样式
//        DialogSettings.menuTextInfo = (TextInfo);               //全局菜单列表文字样式
//        DialogSettings.contentTextInfo = (TextInfo);            //全局正文文字样式
//        DialogSettings.buttonTextInfo = (TextInfo);             //全局默认按钮文字样式
//        DialogSettings.buttonPositiveTextInfo = (TextInfo);     //全局焦点按钮文字样式（一般指确定按钮）
//        DialogSettings.inputInfo = (InputInfo);                 //全局输入框文本样式
//        DialogSettings.backgroundColor = (ColorInt);            //全局对话框背景颜色，值0时不生效
//        DialogSettings.cancelable = (boolean);                  //全局对话框默认是否可以点击外围遮罩区域或返回键关闭，此开关不影响提示框（TipGifDialog）以及等待框（TipDialog）
//        DialogSettings.cancelableTipDialog = (boolean);         //全局提示框及等待框（WaitDialog、TipDialog）默认是否可以关闭
//        DialogSettings.DEBUGMODE = (boolean);                   //是否允许打印日志
//        DialogSettings.blurAlpha = (int);                       //开启模糊后的透明度（0~255）
//        DialogSettings.systemDialogStyle = (styleResId);        //自定义系统对话框style，注意设置此功能会导致原对话框风格和动画失效
//        DialogSettings.dialogLifeCycleListener = (DialogLifeCycleListener);  //全局Dialog生命周期监听器
//        DialogSettings.defaultCancelButtonText = (String);      //设置 BottomMenu 和 ShareDialog 默认“取消”按钮的文字
//        DialogSettings.tipBackgroundResId = (drawableResId);    //设置 TipDialog 和 WaitDialog 的背景资源
//        DialogSettings.tipTextInfo = (InputInfo);               //设置 TipDialog 和 WaitDialog 文字样式
//        DialogSettings.autoShowInputKeyboard = (boolean);       //设置 InputDialog 是否自动弹出输入法
//        DialogSettings.okButtonDrawable = (drawable);           //设置确定按钮背景资源
//        DialogSettings.cancelButtonDrawable = (drawable);       //设置取消按钮背景资源
//        DialogSettings.otherButtonDrawable = (drawable);        //设置其他按钮背景资源
//        Notification.mode = Notification.Mode.FLOATING_WINDOW;  //通知实现方式。可选 TOAST 使用自定义吐司实现以及 FLOATING_WINDOW 悬浮窗实现方式

//检查 Renderscript 兼容性，若设备不支持，DialogSettings.isUseBlur 会自动关闭；
        val renderscriptSupport = DialogSettings.checkRenderscriptSupport(BaseApplication.context)
        DialogSettings.init() //初始化清空 BaseDialog 队列
    }

    /**
     * 腾讯TBS WebView X5 内核初始化
     */
    private fun initX5WebViewCore() {
        // dex2oat优化方案
        val map = HashMap<String, Any>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
        QbSdk.initTbsSettings(map)

        // 允许使用非wifi网络进行下载
        QbSdk.setDownloadWithoutWifi(true)

        // 初始化
        QbSdk.initX5Environment(BaseApplication.context, object : QbSdk.PreInitCallback {

            override fun onCoreInitFinished() {
                LogUtil.e("ApplicationInit", " TBS X5 init finished")
            }

            override fun onViewInitFinished(p0: Boolean) {
                // 初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核
                LogUtil.e("ApplicationInit", " TBS X5 init is $p0")
            }
        })
    }

    /**
     * 腾讯 MMKV 初始化
     */
    private fun initMMKV(): String {
        val result = SpUtils.initMMKV(BaseApplication.context)
        return "MMKV -->> $result"
    }

    /**
     * 阿里路由 ARouter 初始化
     */
//    private fun initARouter(): String {
//        // 测试环境下打开ARouter的日志和调试模式 正式环境需要关闭
//        if (BuildConfig.VERSION_TYPE != VersionStatus.RELEASE) {
//            ARouter.openLog()     // 打印日志
//            ARouter.openDebug()   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
//        }
//        ARouter.init(BaseApplication.application)
//        return "ARouter -->> init complete"
//    }

    /**
     * 初始化 腾讯Bugly
     * 测试环境应该与正式环境的日志收集渠道分隔开
     * 目前有两个渠道 测试版本/正式版本
     */
    private fun initTencentBugly(): String {
        // 第三个参数为SDK调试模式开关
        CrashReport.initCrashReport(
            BaseApplication.context,
            BaseApplication.context.getString(R.string.BUGLY_APP_ID),
            BuildConfig.VERSION_TYPE != VersionStatus.RELEASE
        )
        return "Bugly -->> init complete"
    }
}