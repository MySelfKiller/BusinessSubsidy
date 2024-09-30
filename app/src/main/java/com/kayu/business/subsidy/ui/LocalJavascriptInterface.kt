package com.kayu.business.subsidy.ui

import android.content.Context
import android.os.Handler
import android.webkit.JavascriptInterface
import com.hjq.toast.ToastUtils
import com.hoom.library.base.utils.NaviUtil
import com.kayu.utils.*
import org.json.JSONException
import org.json.JSONObject


class LocalJavascriptInterface constructor(
    private val mContext: Context,
    private val mHandler: Handler
) {

    @JavascriptInterface
    fun ScanCamera(){
        LogUtil.e("js调用方法", "Scan-------")
        mHandler.sendMessage(mHandler.obtainMessage(5 ))
    }

    @JavascriptInterface
    fun GetLocation( ) {//获取本地定位点
        mHandler.sendMessage(mHandler.obtainMessage(4 ))

    }

    @JavascriptInterface
    fun OpenMap(args: String) {
        LogUtil.e("js调用方法", "OpenMap-------$args")
        mHandler.post {
            //{"fromLng":118.180237,"fromLat":39.623863,"toLng":"118.02162","toLat":"39.7285","toName":"红利加油站"}
            try {
                val jsonObject = JSONObject(args)
                NaviUtil.toNavi(
                    mContext,
                    jsonObject.getString("toLat"),
                    jsonObject.getString("toLng"),
                    jsonObject.getString("toName"), "GCJ02"
                )
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }


    @JavascriptInterface
    fun Loading(s: String) {
        //关闭加载框
        LogUtil.e("LocalJavascriptInterface", "Loading----s:" + s)
        mHandler.sendMessage(mHandler.obtainMessage(2, s))
    }
    @JavascriptInterface
    fun CloseStatus(s: String) {
        //返回按键是否需要全部关闭
        LogUtil.e("LocalJavascriptInterface", "CloseStatus----s:" + s)
        mHandler.sendMessage(mHandler.obtainMessage(3, s))
    }

    @JavascriptInterface
    fun advert(s: String) {
        LogUtil.e("LocalJavascriptInterface", "advert----path:" + s)
        if (StringUtil.isEmpty(s)) return
        var jsonObject: JSONObject? = null
        try {
            jsonObject = JSONObject(s)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (null == jsonObject) return
        val id: Long = jsonObject.optLong("id")
        mHandler.sendMessage(mHandler.obtainMessage(1, id))
    }

    @JavascriptInterface
    fun saveImage(s: String?) {
//        MessageDialog.show((AppCompatActivity) mContext, "保存图片", "确定保存图片到相册吗？\n"+s, "确定", "取消")
//                .setCancelable(false)
//                .setOkButton(new OnDialogButtonClickListener() {
//            @Override
//            public boolean onClick(BaseDialog baseDialog, View v) {
//                baseDialog.doDismiss();
//
//                return false;
//            }
//        });
//        LogUtil.e("LocalJavascriptInterface","saveImage----path:"+s);
        // TODO: 需要验证js调用保存图片逻辑
        if (StringUtil.isEmpty(s)) {
            ToastUtils.show("数据错误，无法保存！")
            return
        }

//        SMApplication.instance.checkPermission(mContext as WebViewActivity,arrayListOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),{
//            mHandler.post{
//                SMApplication.instance.loadImg(s, null, object : ImageCallback {
//                    override fun onSuccess(resource: Bitmap) {
//                        val fileName: String = "qr_" + System.currentTimeMillis() + ".jpg"
//                        if (null != resource.saveToAlbum(mContext,fileName)) {
//                            TipDialog.show(
//                                mContext as AppCompatActivity?,
//                                "保存成功",
//                                TipDialog.TYPE.SUCCESS
//                            )
//                        } else {
//                            TipDialog.show(
//                                mContext as AppCompatActivity?,
//                                "保存失败",
//                                TipDialog.TYPE.ERROR
//                            )
//                        }
//                    }
//
//                    public override fun onError() {}
//                })
//            }
//        },{
//            ToastUtils.show("权限被拒绝,无法保存图片")
//        })
    }

    @JavascriptInterface
    fun openAndMiniProgram(userName: String?,path:String?) {
//        if (userName.isNullOrEmpty() || path.isNullOrEmpty())
//            return
//        val wxShare =  WXShare(mContext)
//        wxShare.openMiniProgram(userName,path)
    }

    @JavascriptInterface
    fun sharedWechat(jsonStr: String?) {
//        if (null == jsonStr) {
//            TipGifDialog.show(mContext as AppCompatActivity?, "分享数据错误！", TipGifDialog.TYPE.ERROR)
//            return
//        }
//        val sharedBean: WXSharedBean? = GsonHelper.fromJson(jsonStr, WXSharedBean::class.java)
//        if (null == sharedBean) {
//            TipGifDialog.show(mContext as AppCompatActivity?, "分享数据错误！", TipGifDialog.TYPE.ERROR)
//            return
//        }
//        val wxShare: WXShare = WXShare(mContext)
//        wxShare.register()
//        when (sharedBean.`object`) {
//            0 -> {
//                if (StringUtil.isEmpty(sharedBean.qrCode)) {
//                    TipGifDialog.show(
//                        mContext as AppCompatActivity?,
//                        "分享的图片地址不存在！",
//                        TipGifDialog.TYPE.ERROR
//                    )
//                }
//                KWApplication.instance.loadImg(sharedBean.qrCode, null, object : ImageCallback {
//                        override fun onSuccess(resource: Bitmap) {
//                            val sss: Bitmap = resource
//                            wxShare.shareImg(
//                                (sharedBean.type)!!,
//                                sss,
//                                sharedBean.title,
//                                sharedBean.desc
//                            )
//                        }
//
//                        public override fun onError() {}
//                    })
//            }
//            1 -> {
//                if (StringUtil.isEmpty(sharedBean.url)) {
//                    TipGifDialog.show(
//                        mContext as AppCompatActivity?,
//                        "分享的音频地址不存在！",
//                        TipGifDialog.TYPE.ERROR
//                    )
//                }
//                wxShare.shareMusic(
//                    (sharedBean.type)!!,
//                    sharedBean.url,
//                    sharedBean.title,
//                    sharedBean.desc
//                )
//            }
//            2 -> {
//                if (StringUtil.isEmpty(sharedBean.url)) {
//                    TipGifDialog.show(
//                        mContext as AppCompatActivity?,
//                        "分享的视频地址不存在！",
//                        TipGifDialog.TYPE.ERROR
//                    )
//                }
//                wxShare.shareVideo(
//                    (sharedBean.type)!!,
//                    sharedBean.url,
//                    sharedBean.title,
//                    sharedBean.desc
//                )
//            }
//            3 -> {
//                if (StringUtil.isEmpty(sharedBean.url)) {
//                    TipGifDialog.show(
//                        mContext as AppCompatActivity?,
//                        "分享的网页地址不存在！",
//                        TipGifDialog.TYPE.ERROR
//                    )
//                }
//                wxShare.shareUrl(
//                    (sharedBean.type)!!,
//                    sharedBean.url,
//                    sharedBean.title,
//                    sharedBean.desc,
//                    null
//                )
//            }
//        }
    }
}