package com.kayu.business.subsidy.ui.login

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.hjq.toast.ToastUtils
import com.hoom.library.common.ext.parseState
import com.hoom.library.common.ui.BaseActivity
import com.kayu.business.subsidy.SMApplication
import com.kayu.business.subsidy.databinding.ActivityLogOffBinding
import com.kayu.form_verify.Form
import com.kayu.form_verify.Validate
import com.kayu.form_verify.validator.NotEmptyValidator
import com.kayu.business.subsidy.R
import com.kayu.utils.Constants
import com.kayu.utils.NoMoreClickListener
import com.kayu.utils.SMSCountDownTimer
import com.kayu.utils.status_bar_set.StatusBarUtil
import com.tencent.mmkv.MMKV
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogOffActivity : BaseActivity<ActivityLogOffBinding, LogOffViewModel>() {

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
        super.setStatusBar()
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white))
    }

    override val mViewModel: LogOffViewModel by viewModels()

    override fun ActivityLogOffBinding.initView() {
        //设置标题栏
        mBinding.logoffTitle.titleNameTv.text = "账号注销"
        mBinding.logoffTitle.titleBackBtu.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }
            override fun OnMoreErrorClick() {}
        })
//        val kv = MMKV.defaultMMKV()
//        val loginInfo = GsonHelper.fromJson(kv.decodeString(Constants.loginInfo), LoginDataBean::class.java)
//        if (null != loginInfo) {
//        }
        mBinding.logoffNumberEdt.setText(SMApplication.instance.userDetails?.phone)

        mBinding.logoffNumberEdt.isClickable = false
        mBinding.logoffNumberEdt.isEnabled = false
        val timer = SMSCountDownTimer(mBinding.logoffSendSmsTv, 60 * 1000 * 2, 1000)
        mBinding.logoffSendSmsTv.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                val form = Form()
                val phoneValiv = Validate(mBinding.logoffNumberEdt)
                phoneValiv.addValidator(NotEmptyValidator(this@LogOffActivity))
                form.addValidates(phoneValiv)
                val isOk: Boolean = form.validate()
                if (isOk) {
                    timer.start()
                    mViewModel.sendLogoffSmsCode()
                }
            }

            override fun OnMoreErrorClick() {}
        })
        mBinding.logoffAskBtn.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                val form = Form()
                val phoneValiv = Validate(mBinding.logoffNumberEdt)
                phoneValiv.addValidator(NotEmptyValidator(this@LogOffActivity))
                form.addValidates(phoneValiv)
                val smsValiv = Validate(mBinding.logoffSmsCodeEdt)
                smsValiv.addValidator(NotEmptyValidator(this@LogOffActivity))
                form.addValidates(smsValiv)
                val isOk: Boolean = form.validate()
                if (isOk) {
                    mViewModel.logoff(mBinding.logoffSmsCodeEdt.text.toString().trim())
                }
            }

            override fun OnMoreErrorClick() {}
        })
    }

    override fun initObserve() {
        mViewModel.sendSmsLiveData.observe(this){ it ->
            parseState(it,{
                ToastUtils.show("验证码获取成功！")
            },{
                ToastUtils.show(it.errorMsg)
            })
        }
        mViewModel.logoffResult.observe(this){ it ->
            parseState(it,{
//                LogUtil.e("sendSmsLiveData",it)
                ToastUtils.show("注销成功！")
                val kv = MMKV.defaultMMKV()
                kv.encode(Constants.loginInfo, "")
                kv.encode(Constants.isLogin, false)
                val inx = Intent(this@LogOffActivity, LoginActivity::class.java)
                inx.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(inx)
                finish()
            },{
                ToastUtils.show(it.errorMsg)
            })
        }
    }

    override fun initRequestData() {
    }
}