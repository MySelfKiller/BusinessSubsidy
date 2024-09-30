package com.kayu.business.subsidy.ui.login

import android.content.res.Configuration
import android.content.res.Resources
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.hjq.toast.ToastUtils
import com.hoom.library.common.ext.parseState
import com.hoom.library.common.ui.BaseActivity
import com.kayu.business.subsidy.R
import com.kayu.business.subsidy.databinding.ActivityForgetBinding
import com.kayu.form_verify.Form
import com.kayu.form_verify.Validate
import com.kayu.form_verify.validate.ConfirmValidate
import com.kayu.form_verify.validator.NotEmptyValidator
import com.kayu.form_verify.validator.PhoneValidator
import com.kayu.utils.NoMoreClickListener
import com.kayu.utils.SMSCountDownTimer
import com.kayu.utils.status_bar_set.StatusBarUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgetActivity : BaseActivity<ActivityForgetBinding, ForgetViewModel>() {

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

    override val mViewModel: ForgetViewModel by viewModels()

    override fun ActivityForgetBinding.initView() {
        mBinding.forgetTitle.titleNameTv.text = "忘记密码"
        mBinding.forgetTitle.titleBackBtu.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }
            override fun OnMoreErrorClick() { }
        })

        val timer = SMSCountDownTimer(mBinding.forgetSendSmsTv , 60 * 1000, 1000)
        mBinding.forgetSendSmsTv.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                val form = Form()
                val phoneValiv = Validate(mBinding.forgetNumberEdt)
                phoneValiv.addValidator(PhoneValidator(this@ForgetActivity))
                form.addValidates(phoneValiv)
                val isOk: Boolean = form.validate()
                if (isOk) {
                    timer.start()
                    mViewModel.sendSetPwdSmsCode(mBinding.forgetNumberEdt.text.toString())
                }
            }

            override fun OnMoreErrorClick() {}
        })
        mBinding.forgetAskBtn.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                val form = Form()
                val phoneValidate = Validate(mBinding.forgetNumberEdt)
                phoneValidate.addValidator(PhoneValidator(this@ForgetActivity))
                form.addValidates(phoneValidate)
                val codeValidate = Validate(mBinding.forgetCode)
                codeValidate.addValidator(NotEmptyValidator(this@ForgetActivity))
                form.addValidates(codeValidate)
                val newPassValidate = ConfirmValidate(mBinding.forgetNewPassword, mBinding.forgetNewPassword2)
                newPassValidate.addValidator(NotEmptyValidator(this@ForgetActivity))
                form.addValidates(newPassValidate)
                if (form.validate()) {
                    val reqDateMap = HashMap<String, Any>()
                    reqDateMap["phone"] = mBinding.forgetNumberEdt.text.toString().trim()
                    reqDateMap["code"] = mBinding.forgetCode.text.toString().trim()
                    reqDateMap["newPwd"] = mBinding.forgetNewPassword2.text.toString().trim()
                    mViewModel.setForgetPassword( reqDateMap )
                }
            }

            override fun OnMoreErrorClick() {}
        })
    }

    override fun initObserve() {
        mViewModel.sendSmsLiveData.observe(this){ it ->
            parseState(it,{
//                LogUtil.e("sendSmsLiveData",it)
                ToastUtils.show("验证码获取成功！")
            },{
                ToastUtils.show(it.errorMsg)
            })
        }
        mViewModel.setPwdLiveData.observe(this){ it ->
            parseState(it,{
//                LogUtil.e("sendSmsLiveData",it)
                ToastUtils.show("密码修改成功！")
            },{
                ToastUtils.show(it.errorMsg)
            })
        }
    }

    override fun initRequestData() {
    }
}