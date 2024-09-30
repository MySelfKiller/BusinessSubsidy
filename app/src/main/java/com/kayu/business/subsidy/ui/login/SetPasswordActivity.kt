package com.kayu.business.subsidy.ui.login

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.hjq.toast.ToastUtils
import com.hoom.library.common.ext.parseState
import com.hoom.library.common.ui.BaseActivity
import com.kayu.business.subsidy.MainActivity
import com.kayu.business.subsidy.data.bean.LoginDataBean
import com.kayu.business.subsidy.databinding.ActivitySetPasswordBinding
import com.kayu.form_verify.Form
import com.kayu.form_verify.Validate
import com.kayu.form_verify.validate.ConfirmValidate
import com.kayu.form_verify.validator.NotEmptyValidator
import com.kayu.business.subsidy.R
import com.kayu.utils.Constants
import com.kayu.utils.GsonHelper
import com.kayu.utils.GsonHelper.toJsonString
import com.kayu.utils.NoMoreClickListener
import com.kayu.utils.StringUtil
import com.kayu.utils.status_bar_set.StatusBarUtil
import com.tencent.mmkv.MMKV
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetPasswordActivity : BaseActivity<ActivitySetPasswordBinding, ForgetViewModel>() {
    private var isSetPwd = false
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

    override fun ActivitySetPasswordBinding.initView() {
        val title = intent.getStringExtra("title")
        val back = intent.getStringExtra("back")
        isSetPwd = intent.getBooleanExtra("isSetPwd", false)

        if (StringUtil.isEmpty(back)) {
            mBinding.setPasswordTitle.titleBackBtu.visibility = View.INVISIBLE
        } else {
            mBinding.setPasswordTitle.titleBackBtu.visibility = View.VISIBLE
        }
        mBinding.setPasswordTitle.titleBackTv.text = back
        mBinding.setPasswordTitle.titleNameTv.text = title
        mBinding.setPasswordTitle.titleBackBtu.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }
            override fun OnMoreErrorClick() { }
        })

        if (isSetPwd) {
            mBinding.setOldPwdLay.visibility = View.GONE
        } else {
            mBinding.setOldPwdLay.visibility = View.VISIBLE
        }

        mBinding.setAskBtn.setOnClickListener(object : NoMoreClickListener(){
            override fun OnMoreClick(view: View) {
                val form = Form()
                if (!isSetPwd) {
                    val codeValidate = Validate(mBinding.setOldPassword)
                    codeValidate.addValidator(NotEmptyValidator(this@SetPasswordActivity))
                    form.addValidates(codeValidate)
                }
                val newPassValidate = ConfirmValidate(mBinding.setNewPassword, mBinding.setNewPassword2)
                newPassValidate.addValidator(NotEmptyValidator(this@SetPasswordActivity))
                form.addValidates(newPassValidate)
                if (form.validate()) {
                    val reqDateMap = HashMap<String, Any>()
                    if (!isSetPwd) {
                        reqDateMap["oldPwd"] = mBinding.setOldPassword.text.toString().trim { it <= ' ' }
                    }
                    reqDateMap["newPwd"] = mBinding.setNewPassword2.text.toString().trim { it <= ' ' }
                    mViewModel.setPassword(reqDateMap)
                }
            }

            override fun OnMoreErrorClick() {}

        })

    }

    override fun initObserve() {
//        mViewModel.sendSmsLiveData.observe(this){ it ->
//            parseState(it,{
////                LogUtil.e("sendSmsLiveData",it)
//                ToastUtils.show(it.message)
//            },{
//                ToastUtils.show(it.errorMsg)
//            })
//        }
        mViewModel.setPwdLiveData.observe(this){ it ->
            parseState(it,{
                if (isSetPwd) {
                    ToastUtils.show("密码设置成功！")
                    val kv = MMKV.defaultMMKV()
                    val jsonUser = kv.decodeString(Constants.loginInfo, "")
                    val mUser: LoginDataBean? = GsonHelper.fromJson(jsonUser, LoginDataBean::class.java)
                    if (null != mUser){
                        mUser.initPwd = 0
                        kv.encode(Constants.isSetPsd, true)
                        kv.encode(Constants.loginInfo, toJsonString(mUser))
                    }
                    startActivity(Intent(this@SetPasswordActivity, MainActivity::class.java))
                } else {
                    ToastUtils.show("密码修改成功！")
                }
                finish()
            },{
                ToastUtils.show(it.errorMsg)
            })
        }
    }

    override fun initRequestData() {
    }

    private var firstTime: Long = 0
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isSetPwd) {
                val secondTime = System.currentTimeMillis()
                if (secondTime - firstTime > 2000) {
                    ToastUtils.show("再按一次退出应用")
                    firstTime = secondTime
                    return true
                } else {
                    System.exit(0)
                }
            } else {
                onBackPressed()
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}