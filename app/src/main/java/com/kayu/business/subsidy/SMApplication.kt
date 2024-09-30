package com.kayu.business.subsidy

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hoom.library.base.BaseApplication
import com.kayu.business.subsidy.data.bean.ProductItemBean
import com.kayu.business.subsidy.data.bean.UserDetails
import com.kayu.business.subsidy.ui.login.LoginActivity
import com.kayu.business.subsidy.ui.text_link.UrlClickableSpan
import com.kayu.utils.*
import com.kayu.utils.location.LocationManagerUtil
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener
import com.kongzue.dialog.v3.MessageDialog
import com.lzy.ninegrid.NineGridView
import com.permissionx.guolindev.PermissionX
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import kotlin.properties.Delegates


@HiltAndroidApp
class SMApplication : BaseApplication() {

//    var localBroadcastManager: LocalBroadcastManager? = null
//    private val mActivityCount = AtomicInteger(0)
//    private var mAppStopTimeMillis = 0L

    var token: String? = null   //登录成功后返回的token
    var userDetails:UserDetails? = null
    var itemData: ProductItemBean? = null

    override fun onCreate() {
        super.onCreate()
        instance = this

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        initSDKs()
        val kv = MMKV.defaultMMKV()
        token = kv.decodeString(Constants.token, "")
        NineGridView.imageLoader = GlideImageLoader() //
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.kayu.business.subsidy.JUMP")
        val localReceiver = LocalReceiver()
        registerReceiver(localReceiver, intentFilter) // 注册本地广播监听器

        //监听是所有activity生命周期
//        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
//            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
//            override fun onActivityStarted(activity: Activity) {
////                val isLogin = sp.getBoolean(Constants.isLogin, false) //判断用户是否登录
//                //热启动 && 应用退到后台时间超过10s
//                //todo 添加启动屏配置
//                if (((mActivityCount.get() == 0)
//                            && (System.currentTimeMillis() - mAppStopTimeMillis > 10 * 1000
//                            ) && activity !is SplashActivity)
//                ) {
//                    activity.startActivity(Intent(activity, SplashHotActivity::class.java))
//                }
//
//                //+1
//                mActivityCount.getAndAdd(1)
//            }
//
//            override fun onActivityResumed(activity: Activity) {}
//            override fun onActivityPaused(activity: Activity) {}
//            override fun onActivityStopped(activity: Activity) {
////-1
//                mActivityCount.getAndDecrement()
//
//                //退到后台，记录时间
//                if (mActivityCount.get() == 0) {
//                    mAppStopTimeMillis = System.currentTimeMillis()
//                }
//            }
//
//            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
//            override fun onActivityDestroyed(activity: Activity) {}
//        })
    }

    //记录首次异常时间
    private var firstTime: Long = 0
    private var xxx = 1
    private var yyy = 1

    internal inner class LocalReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
//            ToastUtils.show( "received local broadcast${intent.action}")
//            LogUtil.e("接收退出广告", "received local broadcast$yyy")
            yyy++
            val secondTime = System.currentTimeMillis()
            if (firstTime == 0L || secondTime - firstTime > 1000 * 30) {
//                LogUtil.e("强制退出","退出次数"+xxx);
                xxx += 1
                firstTime = secondTime
                // 用户未登录
                val kv = MMKV.defaultMMKV()
                kv.encode(Constants.isLogin, false)
                kv.encode(Constants.loginInfo, "")
//                PersistentCookieStore(applicationContext).removeAll()
//                OkHttpManager.resetHttpClient()
//                appManager!!.finishAllActivity()
//                LocationManagerUtil.self?.stopLocation()
                // TODO: 添加清楚Activity栈
                val inx = Intent(context, LoginActivity::class.java)
                inx.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

                context.startActivity(inx)
            }
        }
    }

    private fun setFornts() {
        val typeface = Typeface.createFromAsset(assets, "fonts/Quicksand-Medium.ttf")
        try {
            val field = Typeface::class.java.getDeclaredField("MONOSPACE")
            field.isAccessible = true
            field[null] = typeface
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        //非默认值
        if (newConfig.fontScale != 1f) {
            resources
        }
        super.onConfigurationChanged(newConfig)
    }

    override fun getResources(): Resources { //还原字体大小
        val res = super.getResources()
        //非默认值
        if (res.configuration.fontScale != 1f) {
            val newConfig = Configuration()
            newConfig.setToDefaults() //设置默认
            res.updateConfiguration(newConfig, res.displayMetrics)
        }
        return res
    }

    /**
     * 必须弹出隐私对话框之后才能初始化
     */
    fun initSDKs() {
        LocationManagerUtil.init(this)
    }

//    //加载本地资源，可裁剪
//    fun loadImg(ids: Int, view: ImageView?, transformation: BitmapTransformation?) {
//        Glide.with(this).load(ids).transform(transformation).into((view)!!)
//    }
//
//    //加载本地资源
//    fun loadImg(ids: Int, view: ImageView?) {
//        Glide.with(this).load(ids).into((view)!!)
//    }

    //带回调的图片加载
//    fun loadImg(url: String?, view: ImageView?, callback: ImageCallback?) {
//        val mUrl: String?
//        if (StringUtil.isEmpty(url)) {
//            return
//        }
//        if (url!!.startsWith("http")) {
//            mUrl = url
//        } else {
//            mUrl = HttpConfig.HOST + url
//        }
//        Glide.with(this).asBitmap().load(mUrl).into(object : CustomTarget<Bitmap?>() {
//            override fun onLoadCleared(placeholder: Drawable?) {}
//            override fun onLoadFailed(errorDrawable: Drawable?) {
//                super.onLoadFailed(errorDrawable)
//                callback!!.onError()
//            }
//
//            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
//                view?.setImageBitmap(resource)
//                callback?.onSuccess(resource)            }
//        })
//    }


    val dataPath: String
        get() = Utils.getEnaviBaseStorage(this)


    private class GlideImageLoader : NineGridView.ImageLoader {
        override fun onDisplayImage(context: Context, imageView: ImageView, url: String?) {
            if (null != url) {
                val options: RequestOptions = RequestOptions()
                    .placeholder(R.mipmap.ic_loading)
                    .error(R.mipmap.ic_emptyimage)
                Glide.with(context)
                    .load(url)
                    .apply(options)
                    .into(imageView)
            }

        }

        override fun onDisplayImage(context: Context, imageView: ImageView, src: Int?) {
            if (src != null) {
                val options: RequestOptions = RequestOptions()
                    .placeholder(R.mipmap.ic_loading)
                    .error(R.mipmap.ic_emptyimage)
                Glide.with(context)
                    .load(src)
                    .apply(options)
                    .into(imageView)
            }
        }

        override fun getCacheImage(url: String?): Bitmap? {
            return null
        }
    }

    /**
     *
     */
    fun checkPermission(activity: FragmentActivity, permissionList:ArrayList<String>, onSuccess:(()->Unit),onFailed:(()->Unit)) {
        PermissionX.init(activity)
            .permissions(permissionList)
            .explainReasonBeforeRequest()
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(deniedList, "需要您同意以下权限才能正常使用", "确定", "取消")
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, "您需要在“设置”中手动授予必要的权限", "确定", "取消")
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    onSuccess()
                } else {
                    onFailed()

                }
            }
    }

    /**
     * 拨打电话（直接拨打电话）
     *
     * @param phoneNum 电话号码
     */
    fun callPhone(context: Activity, phoneNum: String) {
        MessageDialog.show((context as AppCompatActivity), "拨打电话", phoneNum, "呼叫", "取消")
            .setOkButton(
                OnDialogButtonClickListener { baseDialog, v ->
                    val intent = Intent(Intent.ACTION_CALL)
                    val data = Uri.parse("tel:$phoneNum")
                    intent.data = data
                    context.startActivity(intent)
                    false
                })
    }

    /**
     * 获取可点击的SpannableString
     *
     * @return
     */
    fun getClickableSpan(
        context: Context,
        titles: Array<String>,
        urls: Array<String>
    ): SpannableString {
        val messSB = StringBuilder()
        messSB.append("感谢您选择省省联盟APP！\n我们非常重视您的个信息和隐私安全。为了更好的保障您的个人权益，在您使用我们的产品前，请务必审慎阅读《")
        val title1Index = messSB.length - 1
        messSB.append(titles[1])
        val title1End = messSB.length + 1
        messSB.append("》与《")
        val title2Index = messSB.length - 1
        messSB.append(titles[0])
        val title2End = messSB.length + 1
        messSB.append(
            "》内的全部内容，同意并接受全部条款后开始使用我们的产品和服务。我们深知个人信息对您的重要性，我们将严格遵守相关法律法规，并采取相应的重要保护技术措施，" +
                    "尽力保护您的个人信息安全。在使用APP过程中，我们会基于您的授权获取您的以下权限，您有权拒绝和取消授权：\n"
        )
        messSB.append(
            ("1、定位权限：用于获取周边的特权信息，如优惠加油站，洗车门店等；\n" +
                    "2、设备信息权限：用于账号信息的验证，以保障交易安全；\n" +
                    "3、存储权限：以实现保存联系客服二维码图片功能；\n" +
                    "4、拨打电话权限：用于一键拨打客服电话功能。")
        )
        val spannableString = SpannableString(messSB.toString())

        //设置下划线文字
//        spannableString.setSpan(new NoUnderlineSpan(), title1Index, title1End, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的单击事件
        spannableString.setSpan(
            UrlClickableSpan((context), (urls[1])),
            title1Index,
            title1End,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        //设置文字的前景色
        spannableString.setSpan(
            ForegroundColorSpan(Color.BLUE),
            title1Index,
            title1End,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        //设置下划线文字
//        spannableString.setSpan(new NoUnderlineSpan(), title2Index, title2End, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置文字的单击事件
        spannableString.setSpan(
            UrlClickableSpan((context)!!, (urls[0])!!),
            title2Index,
            title2End,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        //设置文字的前景色
        spannableString.setSpan(
            ForegroundColorSpan(Color.BLUE),
            title2Index,
            title2End,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

    companion object {
        var instance: SMApplication by Delegates.notNull()
    }
}