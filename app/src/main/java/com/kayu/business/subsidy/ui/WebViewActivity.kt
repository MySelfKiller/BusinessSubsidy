package com.kayu.business.subsidy.ui

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.ClipData
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.hjq.toast.ToastUtils
import com.hoom.library.common.http.HttpConfig
import com.hoom.library.common.ui.BaseActivity
import com.kayu.business.subsidy.R
import com.kayu.business.subsidy.SMApplication
import com.kayu.business.subsidy.databinding.ActivityWebViewBinding
import com.kayu.utils.*
import com.kayu.utils.status_bar_set.StatusBarUtil
import com.king.zxing.CameraScan
import com.kongzue.dialog.interfaces.OnDismissListener
import com.kongzue.dialog.interfaces.OnMenuItemClickListener
import com.kongzue.dialog.v3.BottomMenu
import com.kongzue.dialog.v3.WaitDialog
import com.permissionx.guolindev.PermissionX
import com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.export.external.interfaces.WebResourceRequest
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import com.tencent.smtt.sdk.ValueCallback
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class WebViewActivity : BaseActivity<ActivityWebViewBinding, WebViewModel>() {
    @SuppressLint("HandlerLeak")
    private val jsHandler: Handler = object : Handler() {
        public override fun handleMessage(msg: Message) {
            when (msg.what) {
                2 -> {   //关闭加载框
                    isOpenDialog = msg.obj as String
                    when (isOpenDialog) {
                        "1" -> {
                            WaitDialog.show(
                                this@WebViewActivity,
                                "加载中..."
                            )
                        }
                        "0" -> {
                            WaitDialog.dismiss()
                        }
                    }

                }
                3 -> {   //返回按键是否需要全部关闭
                    jsCloseStatus = msg.obj as String
                }
                4 -> {  //js调用本地定位点并回传
                    // TODO: 是否需要添加定位
//                    val location = LocationManagerUtil.self?.loccation
//                    LogUtil.e(
//                        "js调用方法",
//                        " getLocation==" + location?.longitude + "," + location?.latitude
//                    )
//                    if (null != location) {
//                        mBinding.wvWebView.evaluateJavascript(
//                            "window.CurrentLocation(" + location.latitude + "," + location.longitude + ")",
//                            null
//                        )
//                    }

                }
                5->{
                    SMApplication.instance.checkPermission(this@WebViewActivity,arrayListOf(Manifest.permission.CAMERA),{
//                        startScan()
                    },{
                        ToastUtils.show("权限被拒绝，无法开启扫描二维码功能")
                    })
                }
                6 -> {
                    val urlStr = msg.obj as String
                    LogUtil.e(
                        "js调用方法 ",
                        " ScanAndroidInfo==$urlStr"
                    )
                    mBinding.wvWebView.evaluateJavascript(
                        "window.ScanAndroidInfo('$urlStr')",
                        null
                    )
                }
            }
            super.handleMessage(msg)
        }
    }

    private var url: String? = null

    private var lastOpenUrl: String? = null
    var headMap: MutableMap<String, String?> = HashMap()
    private var jsCloseStatus: String = ""
    private var isOpenDialog: String = ""
    private var data //需要用到的加密数据
            : String? = null
    private var channel //加油平台渠道 团油:ty ，淘油宝:tyb 青桔:qj
            : String? = null
    private var gasId: String? = null
    private var isDownload: Boolean = true

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

    override fun setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white))
    }


    override val mViewModel: WebViewModel by viewModels()

    override fun ActivityWebViewBinding.initView() {
//        StatusBarUtil.setStatusBarColor(this@WebViewActivity, getResources().getColor(R.color.white))
        AndroidBug5497Workaround.assistActivity(this@WebViewActivity)
        if (AppUtil.getNavigationBarHeight(this@WebViewActivity)>0) {
            val bottom: Int = AppUtil.getNavigationBarHeight(this@WebViewActivity)
            val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(mBinding.llWebView.layoutParams)
            if (bottom < 50) {
                lp.setMargins(0, 0, 0, bottom + DisplayUtils.dip2px(this@WebViewActivity,25.0f))
            } else {
                lp.setMargins(0, 0, 0, bottom)
            }
            mBinding.llWebView.layoutParams = lp
        } else {
            val lp: LinearLayout.LayoutParams = LinearLayout.LayoutParams(mBinding.llWebView.layoutParams)
            lp.setMargins(0, 0, 0, DisplayUtils.dip2px(this@WebViewActivity,40.0f))
            mBinding.llWebView.layoutParams = lp
        }
        val intent: Intent = intent
        url = intent.getStringExtra("url")

        mBinding.wvWebView.clearCache(true)
//        data = intent.getStringExtra("data")
//        channel = intent.getStringExtra("channel")
//        gasId = intent.getStringExtra("gasId")
        mBinding.webTitle.titleBackBtu.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }

            override fun OnMoreErrorClick() {}
        })
        mBinding.webTitle.titleCloseBtn.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                finish()
            }

            override fun OnMoreErrorClick() {}
        })
        mBinding.webTitle.titleNameTv.text = ""
        //获取后台判断是否需要开启关闭按钮
//        loadSysParameter(this, 51)
        initData()
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun initData() {
        if (StringUtil.isEmpty(url)) {
            ToastUtils.show("访问链接不存在！")
            finish()
            return
        }
        val webSettings: WebSettings = mBinding.wvWebView.settings
        webSettings.javaScriptEnabled = true
        webSettings.blockNetworkImage = false

        //支持插件
//        webSettings.setPluginsEnabled(true);

//设置自适应屏幕，两者合用
        webSettings.textZoom = 100
        webSettings.useWideViewPort = true //将图片调整到适合webview的大小
        webSettings.loadWithOverviewMode = true // //和setUseWideViewPort(true)一起解决网页自适应问题

//缩放操作
        webSettings.setSupportZoom(true) //支持缩放，默认为true。是下面那个的前提。
        webSettings.builtInZoomControls = true //设置内置的缩放控件。若为false，则该WebView不可缩放

        webSettings.displayZoomControls = false //隐藏原生的缩放控件
        webSettings.domStorageEnabled = true
        webSettings.setSupportMultipleWindows(false)


        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL

        val screenDensity = resources.displayMetrics.densityDpi
        var zoomDensity = WebSettings.ZoomDensity.MEDIUM
        when (screenDensity) {
            DisplayMetrics.DENSITY_LOW -> zoomDensity = WebSettings.ZoomDensity.CLOSE
            DisplayMetrics.DENSITY_MEDIUM -> zoomDensity = WebSettings.ZoomDensity.MEDIUM
            DisplayMetrics.DENSITY_HIGH -> zoomDensity = WebSettings.ZoomDensity.FAR
        }
        webSettings.defaultZoom = zoomDensity

//其他细节操作
        webSettings.allowFileAccess = true //设置可以访问文件
        webSettings.javaScriptCanOpenWindowsAutomatically = true //支持通过JS打开新窗口
        webSettings.loadsImagesAutomatically = true //支持自动加载图片
        webSettings.defaultTextEncodingName = "utf-8" //设置编码格式
        webSettings.domStorageEnabled = true

//        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setAppCacheEnabled(false) //是否使用缓存

        //启用数据库
        webSettings.setDatabaseEnabled(true)

//设置定位的数据库路径
        val dir: String = getApplicationContext().getDir("database", MODE_PRIVATE).path
        webSettings.setGeolocationDatabasePath(dir)

//启用地理定位
        webSettings.setGeolocationEnabled(true)
//        webSettings.setSupportMultipleWindows(true)
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        //开启DomStorage缓存
//        LogUtil.e("WebView","UserAgent: "+webSettings.getUserAgentString());

        // android 5.0及以上默认不支持Mixed Content
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.LOAD_NORMAL
        }
        mBinding.wvWebView.addJavascriptInterface(
            LocalJavascriptInterface(this, jsHandler),
            "androidMethod"
        )
        mBinding.wvWebView.requestFocus()
        mBinding.wvWebView.clearHistory()
        /**
         * 当下载文件时打开系统自带的浏览器进行下载，当然也可以对捕获到的 url 进行处理在应用内下载。
         */
//        wvWebView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:54.0) Gecko/20100101 Firefox/54.0");
//        mBinding.wvWebView.setDownloadListener(FileDownLoadListener())
        mBinding.wvWebView.setWebChromeClient(object : WebChromeClient() {
//            public override fun onJsPrompt(
//                view: WebView,
//                url: String,
//                message: String,
//                defaultValue: String,
//                result: JsPromptResult
//            ): Boolean {
//                return super.onJsPrompt(view, url, message, defaultValue, result)
//            }
//
//            public override fun onJsConfirm(
//                view: WebView,
//                url: String,
//                message: String,
//                result: JsResult
//            ): Boolean {
//                val builder: AlertDialog.Builder = AlertDialog.Builder(view.getContext())
//                builder.setTitle("提示").setMessage(message)
//                    .setPositiveButton("确定", object : DialogInterface.OnClickListener {
//                        public override fun onClick(dialog: DialogInterface, which: Int) {
////                                if (!StringUtil.isEmpty(url)){
////                                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(url));
////                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                                    intent.setComponent(new ComponentName("com.android.browser","com.android.browser.BrowserActivity"));
////                                    startActivity(intent);
////                                }
//                            result.confirm()
//                        }
//                    }).setNeutralButton("取消", object : DialogInterface.OnClickListener {
//                        public override fun onClick(dialog: DialogInterface, which: Int) {
//                            result.cancel()
//                        }
//                    }).create().show()
//                return true
//            }

//            public override fun onJsAlert(
//                view: WebView,
//                url: String,
//                message: String,
//                result: JsResult
//            ): Boolean {
//                val b2: AlertDialog.Builder = AlertDialog.Builder(this@WebViewActivity)
//                    .setTitle("提示")
//                    .setMessage(message)
//                    .setPositiveButton("确定",
//                        object : DialogInterface.OnClickListener {
//                            public override fun onClick(dialog: DialogInterface, which: Int) {
//                                result.confirm()
//                            }
//                        }).setNeutralButton("取消", object : DialogInterface.OnClickListener {
//                        public override fun onClick(dialog: DialogInterface, which: Int) {
//                            result.cancel()
//                        }
//                    })
//                b2.setCancelable(true)
//                b2.create()
//                b2.show()
//                return super.onJsAlert(view,url,message,result)
//            }

            public override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
//                title_name!!.setText(titleName)
            }

            public override fun onProgressChanged(view: WebView, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                //                pbWebView.setProgress(newProgress);
                if (newProgress == 100) {
//                    LogUtil.e(
//                        "WebView",
//                        "onProgressChanged: url=" + view.url + "------- newProgress=" + newProgress
//                    )
                    if (isOpenDialog == "") {
                        WaitDialog.dismiss()
                    }
//                    else if (isOpenDialog == "0"){
//                        TipGifDialog.dismiss()
//                    }
                }

            }

            public override fun onGeolocationPermissionsShowPrompt(
                origin: String,
                callback: GeolocationPermissionsCallback
            ) {
                callback.invoke(origin, true, false)
                super.onGeolocationPermissionsShowPrompt(origin, callback)
            }

            // For Android 3.0+
            fun openFileChooser(uploadMsg: ValueCallback<Uri?>?) {
                mUploadMessage = uploadMsg
                openImageChooserActivity()
            }

            // For Android 3.0+
            fun openFileChooser(uploadMsg: ValueCallback<*>?, acceptType: String?) {
                mUploadMessage = uploadMsg as ValueCallback<Uri?>?
                openImageChooserActivity()
            }

            // For Android 4.1
            override fun openFileChooser(
                uploadMsg: ValueCallback<Uri?>?,
                acceptType: String?,
                capture: String?
            ) {
                mUploadMessage = uploadMsg
                openImageChooserActivity()
            }

            // For Android 5.0+
            public override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri?>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                mUploadCallbackAboveL = filePathCallback
                openImageChooserActivity()
                return true
            }
        })
        mBinding.wvWebView.webViewClient = object : WebViewClient() {
            override fun onReceivedHttpError(
                view: WebView?,
                request: WebResourceRequest?,
                errorResponse: WebResourceResponse?
            ) {
                super.onReceivedHttpError(view, request, errorResponse)
//                LogUtil.e("webview", "errorResponse=" + errorResponse.toString())
            }

            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
//                LogUtil.e("webview", "description=" + description + "  failingUrl=" + failingUrl)
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//                LogUtil.e("WebView", "shouldOverrideUrlLoading: view："+view.url + "----------- url="+url)

                //                view.loadUrl(url);
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    return if ((url == HttpConfig.CLOSE_WEB_VIEW) || url.endsWith("close") || url == HttpConfig.CLOSE_WEB_VIEW1) {
                        this@WebViewActivity.finish()
                        true
                    } else {
                        headMap["Referer"] = lastOpenUrl
                        view.loadUrl(url, headMap)
                        lastOpenUrl = url
                        false
                    }
                } else {
                    try {
                        // 以下固定写法,表示跳转到第三方应用
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        startActivity(intent)
                        isDownload = false //该字段是用于判断是否需要跳转浏览器下载
                    } catch (e: Exception) {
                        // 防止没有安装的情况
                        e.printStackTrace()
                        ToastUtils.show("未安装相应的客户端")
                    }
                    return true
                }
            }

            //            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
//                super.doUpdateVisitedHistory(view, url, isReload)
//                LogUtil.e("WebView", "doUpdateVisitedHistory: url="+view?.url +"-------"+ url+",isReload="+isReload)
//            }
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
//                title_name?.text = titleName
                if (!isBacking) {
                    WaitDialog.show(
                        this@WebViewActivity,
                        "加载中..."
                    )
//                    if (isOpenDialog == "") {
//                        TipGifDialog.show(
//                            this@WebViewActivity,
//                            "加载中...",
//                            TipGifDialog.TYPE.OTHER,
//                            R.drawable.loading_gif
//                        )
//                    } else if (isOpenDialog == "1"){
//                        TipGifDialog.show(
//                            this@WebViewActivity,
//                            "加载中...",
//                            TipGifDialog.TYPE.OTHER,
//                            R.drawable.loading_gif
//                        )
//                    }

                }
                if ((isBacking)) {
                    if (jsCloseStatus == "1"){
                        finish()
                        isBacking = false
                    }else if (url.contains("#/login")) {
                        onBackPressed()
                        isBacking = false
                    }

                }
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView, url: String) {
                view.getSettings().setLoadsImagesAutomatically(true)
                mBinding.webTitle.titleNameTv.postDelayed({ mBinding.webTitle.titleNameTv.text = view.title }, 1)
                lastOpenUrl = url
                LogUtil.e("WebView", "onPageFinished: " + url)
//                LogUtil.e("WebView", "Cookies = " + CookieStr)
                super.onPageFinished(view, url)
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: com.tencent.smtt.export.external.interfaces.SslError?
            ) {
                handler?.proceed()
            }
        }
        mBinding.wvWebView.loadUrl((url)!!)
        mBinding.wvWebView.loadUrl("javascript:window.location.reload( true )")
    }

    internal inner class FileDownLoadListener : DownloadListener {
        public override fun onDownloadStart(
            url: String,
            userAgent: String,
            contentDisposition: String,
            mimetype: String,
            contentLength: Long
        ) {
            if (isDownload) {
                val intent = Intent(Intent.ACTION_VIEW)
                val uri: Uri = Uri.parse(url)
                intent.addCategory(Intent.CATEGORY_BROWSABLE)
                intent.data = uri
                startActivity(intent)
            }
            isDownload = true //重置为初始状态
        }
    }

    private var cameraFielPath: String? = null
    private val FILE_CHOOSER_RESULT_CODE: Int = 1
    private val FILE_CAMERA_RESULT_CODE: Int = 0
    private var mUploadCallbackAboveL: ValueCallback<Array<Uri?>>? = null
    private var mUploadMessage: ValueCallback<Uri?>? = null

    //选择拍照还是相册
    fun openImageChooserActivity() {
        showCustomDialog()
    }


    //拍照
    private fun takeCamera() {
        SMApplication.instance.checkPermission(this@WebViewActivity,arrayListOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE),{
//            installApk(file.absolutePath)
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraFielPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                .toString() + "//" + System.currentTimeMillis() + ".jpg"
            val outputImage: File = File(cameraFielPath)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //7.0及以上
                val photoUri: Uri = FileProvider.getUriForFile(
                    this@WebViewActivity, Constants.authority,
                    outputImage
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                intent.putExtra("return-data", true)
            } else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputImage))
            }
            startActivityForResult(intent, FILE_CAMERA_RESULT_CODE)
        },{
            ToastUtils.show("权限被拒绝，无法拍照")
        })
    }

    //选择图片
    private fun takePhoto() {

        val permisList = arrayListOf( Manifest.permission.WRITE_EXTERNAL_STORAGE)
        PermissionX.init(this@WebViewActivity)
            .permissions(permisList)
            .explainReasonBeforeRequest()
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(deniedList, "需要您同意以下权限才能正常使用", "确定", "取消")
            }
            .onForwardToSettings { scope, deniedList ->
                scope.showForwardToSettingsDialog(deniedList, "您需要在“设置”中手动授予必要的权限", "确定", "取消")
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    val i = Intent(Intent.ACTION_GET_CONTENT)
                    i.addCategory(Intent.CATEGORY_OPENABLE)
                    i.setType("image/*")
                    startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE)
                } else {
//                    ToastUtils.show("权限被拒绝：$deniedList")
                }
            }
    }

    var isClickBottomMenu = false
    private fun showCustomDialog() {
        BottomMenu.show(
            this@WebViewActivity,
            arrayOf("拍照", "从相册选择"),
            object : OnMenuItemClickListener {
                public override fun onClick(text: String, index: Int) {
                    if (index == 0) {
                        // 2018/12/10 拍照
//                    requestCode = FILE_CAMERA_RESULT_CODE;
                        takeCamera()
                        isClickBottomMenu = true
                    } else if (index == 1) {
//                    requestCode = FILE_CHOOSER_RESULT_CODE;
                        // 2018/12/10 从相册选择
                        takePhoto()
                        isClickBottomMenu = true
                    } else {
//                    mUploadCallbackAboveL = null;
//                    mUploadMessage = null;
                        isClickBottomMenu = false
                    }
                }
            }).setOnDismissListener(object : OnDismissListener {
            public override fun onDismiss() {
                if (isClickBottomMenu) return
                if (mUploadCallbackAboveL != null) {
                    mUploadCallbackAboveL!!.onReceiveValue(null)
                    mUploadCallbackAboveL = null
                }
                if (mUploadMessage != null) {
                    mUploadMessage!!.onReceiveValue(null)
                    mUploadMessage = null
                }
            }
        })
    }

    //    private int requestCode = -2;
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        isClickBottomMenu = false
        if (resultCode != RESULT_OK) { //同上所说需要回调onReceiveValue方法防止下次无法响应js方法
            if (null == mUploadMessage && null == mUploadCallbackAboveL) return
            if (mUploadCallbackAboveL != null) {
                mUploadCallbackAboveL!!.onReceiveValue(null)
                mUploadCallbackAboveL = null
            }
            if (mUploadMessage != null) {
                mUploadMessage!!.onReceiveValue(null)
                mUploadMessage = null
            }
            return
        }else {
            if ( requestCode == 6){
                if (data != null){
                    val result = CameraScan.parseScanResult(data)
                    jsHandler.sendMessage(jsHandler.obtainMessage(6 ,result))
//                    ToastUtils.show(result)
                }
                return
            }
            var result: Uri? = null
            if (requestCode == FILE_CAMERA_RESULT_CODE) {
                if (null != data && null != data.getData()) {
                    result = data.getData()
                }
                if (result == null && hasFile(cameraFielPath)) {
                    result = Uri.fromFile(File(cameraFielPath))
                }
                if (mUploadCallbackAboveL != null) {
                    mUploadCallbackAboveL!!.onReceiveValue(arrayOf(result))
                    mUploadCallbackAboveL = null
                } else if (mUploadMessage != null) {
                    mUploadMessage!!.onReceiveValue(result)
                    mUploadMessage = null
                }
            } else if (requestCode == FILE_CHOOSER_RESULT_CODE) {
                if (data != null) {
                    result = data.getData()
                }
                if (mUploadCallbackAboveL != null) {
                    onActivityResultAboveL(requestCode, resultCode, data)
                } else if (mUploadMessage != null) {
                    mUploadMessage!!.onReceiveValue(result)
                    mUploadMessage = null
                }
            }
        }

    }

    /**
     * 判断文件是否存在
     */
    fun hasFile(path: String?): Boolean {
        try {
            val f: File = File(path)
            if (!f.exists()) {
                return false
            }
        } catch (e: Exception) {
            Log.i("error", e.toString())
            return false
        }
        return true
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun onActivityResultAboveL(requestCode: Int, resultCode: Int, intent: Intent?) {
        if ((requestCode != FILE_CAMERA_RESULT_CODE && requestCode != FILE_CHOOSER_RESULT_CODE
                    || mUploadCallbackAboveL == null)
        ) {
            return
        }
        var results: Array<Uri?>? = null
        if (intent != null) {
            val dataString: String? = intent.dataString
            val clipData: ClipData? = intent.clipData
            if (clipData != null) {
                results = arrayOfNulls(clipData.itemCount)
                for (i in 0 until clipData.itemCount) {
                    val item: ClipData.Item = clipData.getItemAt(i)
                    results[i] = item.uri
                }
            }
            if (dataString != null) results = arrayOf(Uri.parse(dataString))
        }
        mUploadCallbackAboveL!!.onReceiveValue(results)
        mUploadCallbackAboveL = null
    }

    var isBacking: Boolean = false

    //系统自带监听方法
    public override fun onBackPressed() {
        if (mBinding.wvWebView.canGoBack()) {
            mBinding.wvWebView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
            mBinding.wvWebView.goBack()
            isBacking = true
            return
        } else {

//            finish();
            setResult(111)
        }
        super.onBackPressed()
    }

    //类相关监听
    override fun onPause() {
        super.onPause()
//        mBinding.wvWebView.onPause()
    }

    override fun onResume() {
//        mBinding.wvWebView.onResume()
        super.onResume()
    }

    override fun onDestroy() {
//        if (wvWebView != null) {
//            mBinding.wvWebView.destroy()
//
//
//            wvWebView = null
//        }
        // 要首先移除webview
//            removeView(webView);

        // 清理缓存
        mBinding.wvWebView.stopLoading()
//            wvWebView?.onPause()
//            wvWebView?.clearHistory()
//            wvWebView?.clearCache(true)
//            wvWebView?.clearFormData()
//            wvWebView?.clearSslPreferences()
//            WebStorage.getInstance().deleteAllData()
//            wvWebView?.destroyDrawingCache()
//            wvWebView?.removeAllViews()

        // 最后再去webView.destroy();
        mBinding.wvWebView.destroy()
//            wvWebView = null

        // 清理cookie
//        val cookieManager: CookieManager = CookieManager.getInstance()
//        cookieManager.removeAllCookies {  }
        super.onDestroy()
    }

    override fun initObserve() {
    }

    override fun initRequestData() {
    }

//    /**
//     * 扫码
//     * @param cls
//     * @param title
//     */
//    private fun startScan() {
//        val optionsCompat = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.in_in, R.anim.out_out)
//        val intent = Intent(this, EasyCaptureActivity::class.java)
//        ActivityCompat.startActivityForResult(
//            this,
//            intent,
//            6,
//            optionsCompat.toBundle()
//        )
//    }
}