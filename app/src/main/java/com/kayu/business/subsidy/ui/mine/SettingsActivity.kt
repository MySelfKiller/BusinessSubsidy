package com.kayu.business.subsidy.ui.mine

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.hoom.library.common.ui.BaseActivity
import com.kayu.business.subsidy.R
import com.kayu.business.subsidy.data.bean.LoginDataBean
import com.kayu.business.subsidy.databinding.ActivitySettingsBinding
import com.kayu.business.subsidy.ui.login.LogOffActivity
import com.kayu.business.subsidy.ui.login.LoginActivity
import com.kayu.business.subsidy.ui.login.SetPasswordActivity
import com.kayu.utils.*
import com.kayu.utils.AppUtil.getVersionName
import com.kayu.utils.status_bar_set.StatusBarUtil
import com.kongzue.dialog.util.BaseDialog
import com.kongzue.dialog.v3.MessageDialog
import com.tencent.mmkv.MMKV
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : BaseActivity<ActivitySettingsBinding, SettingsViewModel>(){
    private var loginInfo:LoginDataBean? = null
    private var title = "标题"
    private var back = "返回"

    override val mViewModel: SettingsViewModel by viewModels()

    override fun setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white))
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimary))
//    }

    override fun ActivitySettingsBinding.initView() {
        val kv = MMKV.defaultMMKV()
        val jsonUser = kv.decodeString(Constants.loginInfo, "")
        loginInfo = GsonHelper.fromJson(jsonUser, LoginDataBean::class.java)
//        val intent = intent
        title = intent.getStringExtra("title").toString()
        back = intent.getStringExtra("back").toString()

        //标题栏
        mBinding.idTitle.titleNameTv.text = title
        mBinding.idTitle.titleBackBtu.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }
            override fun OnMoreErrorClick() {}
        })
        mBinding.idTitle.titleBackTv.text = back

        val version = getVersionName(this@SettingsActivity)
        mBinding.settingsAppVersionTv.text = version

        mBinding.settingsModifyPwd.setOnClickListener(object : NoMoreClickListener(){
            override fun OnMoreClick(view: View) {
                val intent = Intent(this@SettingsActivity, SetPasswordActivity::class.java)
                intent.putExtra("title", "修改密码")
                intent.putExtra("back", "设置")
                intent.putExtra("isSetPwd", false)
                startActivity(intent)
            }

            override fun OnMoreErrorClick() {
            }

        })

        mBinding.settingsUserNameTv.text = loginInfo?.username

        mBinding.settingSignOutBtn.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                MessageDialog.show(
                    this@SettingsActivity,
                    getString(R.string.app_name),
                    "确定需要退出登录？",
                    "确定",
                    "取消"
                )
                    .setOkButton { baseDialog: BaseDialog?, v: View? ->
                        val kv = MMKV.defaultMMKV()
                        kv.encode(Constants.loginInfo, "")
                        kv.encode(Constants.isLogin, false)
                        val inx = Intent(this@SettingsActivity, LoginActivity::class.java)
                        inx.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(inx)
                        finish()
                        false
                    }
            }

            override fun OnMoreErrorClick() {}
        })
        mBinding.settingsLogOff.setOnClickListener(object: NoMoreClickListener(){
            override fun OnMoreClick(view: View) {
                val intent = Intent(this@SettingsActivity, LogOffActivity::class.java)
                startActivity(intent)
            }

            override fun OnMoreErrorClick() {}

        })

//        mBinding.settingsCustomerTv.setOnClickListener(object: NoMoreClickListener(){
//            override fun OnMoreClick(view: View) {
//                showDialog(true)
//            }
//
//            override fun OnMoreErrorClick() {}
//
//        })
//        mBinding.settingsAppNewVersionTv.setOnClickListener(object: NoMoreClickListener(){
//            override fun OnMoreClick(view: View) {
//                showDialog(false)
//            }
//
//            override fun OnMoreErrorClick() {}
//
//        })

    }


//    var reqWechatSuccess = false
//    var reqCustomerSuccess = false
    override fun initObserve() {
//        mViewModel.weChatResult.observe(this){ it ->
//            parseState(it,{
//                reqWechatSuccess = true
//                mViewModel.weChatLiveData.value = it
//                if (isShowDialog){
//                    showDialog(false)
//                }
//            },{
//                ToastUtils.show(it)
//            })
//        }
//        mViewModel.customerResult.observe(this){ it ->
//            parseState(it,{
//                reqCustomerSuccess = true
//                mViewModel.customerLiveData.value = it
//                if (isShowDialog)
//                    showDialog(true)
//            },{
//                ToastUtils.show(it)
//            })
//        }

    }

    override fun initRequestData() {
//        mViewModel.getWeChat()
//        mViewModel.getCustomer()
    }

//    var isShowDialog = false
//    private fun showDialog(isCustom: Boolean) {
//        val customer: OldSystemParam?
//        if (isCustom) {
//            if (!reqCustomerSuccess){
//                isShowDialog = true
//                mViewModel.getCustomer()
//                return
//            }
//            customer = mViewModel.customerLiveData.value
//        } else {
//            if (!reqWechatSuccess){
//                isShowDialog = true
//                mViewModel.getWeChat()
//                return
//            }
//            customer = mViewModel.weChatLiveData.value
//        }
//        if (null == customer || !reqCustomerSuccess || !reqWechatSuccess) return
//        CustomDialog.show(this@SettingsActivity, R.layout.dialog_qrcode_lay
//        ) { dialog, v ->
//            val qrcode_iv = v.findViewById<ImageView>(R.id.shared_qrcode_iv)
//            val save_btn = v.findViewById<Button>(R.id.shared_call_btn)
//
//            val compay_tv1 = v.findViewById<TextView>(R.id.shared_compay_tv1)
//
//            qrcode_iv.load(customer.content){
//                this.allowHardware(false)
////                qrcodeBitmap = createBitmap(qrcode_iv.width, qrcode_iv.height, Bitmap.Config.ARGB_8888)
//                listener(
////                    onStart = { request ->
////                        LogUtil.d("coil-", "onstart")
////                    },
////                    onError = { request, throwable ->
////                        LogUtil.d("coil-", "onerror")
////                    },
////                    onCancel = { request ->
////                        LogUtil.d("coil-", "oncancel")
////                    },
//                    onSuccess = { request, metadata ->
////                        qrcodeBitmap = ((this.build().fallback) as BitmapDrawable).bitmap
////                        (imageLoader.execute(request) as SuccessResult).drawable
////                        LogUtil.e("coil-", "onsuccess")
//                    }
//
//                )
////                this.target(onSuccess = {resultImg->
////                    qrcodeBitmap = resultImg.toBitmap()
////                    LogUtil.d("coil-", "onsuccess")
////                })
//            }
////            val result = (res as SuccessResult).drawable.toBitmap()
//            save_btn.setOnClickListener(object : NoMoreClickListener(){
//                override fun OnMoreClick(view: View) {
//                    SMApplication.instance.checkPermission(this@SettingsActivity,
//                        arrayListOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), {
//                            if (null == qrcode_iv.drawable) {
//                                ToastUtils.show("保存图片不存在")
//                                return@checkPermission
//                            }
//                            val fileName: String =
//                                "qr_" + System.currentTimeMillis() + ".jpg"
//                            if (null != ((qrcode_iv.drawable)as BitmapDrawable).bitmap.saveToAlbum(this@SettingsActivity,fileName)) {
//                                ToastUtils.show("保存成功")
//                            } else {
//                                ToastUtils.show("保存失败")
//                            }
//                        },{
//                            ToastUtils.show("权限被拒绝，无法保存图片")
//                        })
//                }
//
//                override fun OnMoreErrorClick() {
//                }
//
//            })
//            if (TextUtils.isEmpty(customer.title)) {
//                compay_tv1.visibility = View.GONE
//            } else {
//                compay_tv1.text = customer.title
//                compay_tv1.visibility = View.VISIBLE
//            }
//            isShowDialog = false
//        }
//            .setCancelable(true)
//            .setFullScreen(false)
//            .setCustomLayoutParams( RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
//
//    }
}