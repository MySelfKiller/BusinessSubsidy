package com.kayu.business.subsidy.ui.login

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Typeface
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.hjq.toast.ToastUtils
import com.hoom.library.common.ext.parseState
import com.hoom.library.common.ui.BaseActivity
import com.kayu.form_verify.Form
import com.kayu.form_verify.Validate
import com.kayu.form_verify.validator.PhoneValidator
import com.kayu.business.subsidy.MainActivity
import com.kayu.business.subsidy.R
import com.kayu.business.subsidy.SMApplication
import com.kayu.business.subsidy.databinding.ActivityLoginBinding
import com.kayu.business.subsidy.wxapi.WXShare
import com.kayu.utils.*
import com.kayu.utils.StringUtil.isEmpty
import com.kayu.utils.status_bar_set.StatusBarUtil
import com.tencent.mmkv.MMKV
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>() {
//    private var imgCode : ImageCode? = null
    private var isSMSLogin = true
    private var wxShare: WXShare? = null

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

    override fun onDestroy() {
        if (null != wxShare) wxShare!!.unregister()
        super.onDestroy()

    }


    override val mViewModel: LoginViewModel by viewModels()
    override fun setStatusBar() {
        super.setStatusBar()
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white))
    }
    override fun ActivityLoginBinding.initView() {

        mBinding.loginSmsCodeEdt.inputType = InputType.TYPE_CLASS_NUMBER
        val filters = arrayOf<InputFilter>(LengthFilter(4))
        mBinding.loginSmsCodeEdt.filters = filters
        mBinding.loginSmsCodeEdt.setHint("请输入验证码")
        mBinding.loginSendSmsTv.isClickable = false
        val timer = SMSCountDownTimer(mBinding.loginSendSmsTv, 60 * 1000 * 2, 1000)

        mBinding.loginSmsTarget.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                mBinding.loginSmsTargetLay.visibility = View.GONE
                mBinding.loginPasswordTarget.visibility = View.VISIBLE
                mBinding.loginSendSmsTv.visibility = View.VISIBLE
                mBinding.loginSmsCodeEdt.setText("")
                mBinding.loginSmsCodeEdt.setHint("请输入验证码")
                mBinding.loginSmsCodeEdt.inputType = InputType.TYPE_CLASS_NUMBER
                val filters = arrayOf<InputFilter>(LengthFilter(4))
                mBinding.loginSmsCodeEdt.filters = filters
                isSMSLogin = true
            }

            override fun OnMoreErrorClick() {}
        })
        mBinding.loginForgetPassword.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                startActivity(Intent(this@LoginActivity, ForgetActivity::class.java))
            }

            override fun OnMoreErrorClick() {}
        })

        mBinding.loginPasswordTarget.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                mBinding.loginSendSmsTv.visibility = View.GONE
                mBinding.loginSmsTargetLay.visibility = View.VISIBLE
                mBinding.loginPasswordTarget.visibility = View.GONE
                mBinding.loginSmsCodeEdt.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
                mBinding.loginSmsCodeEdt.setTypeface(Typeface.DEFAULT)
                mBinding.loginSmsCodeEdt.transformationMethod = PasswordTransformationMethod()
                mBinding.loginSmsCodeEdt.setText("")
                mBinding.loginSmsCodeEdt.setHint("请输入登录密码")
                val filters = arrayOf<InputFilter>(LengthFilter(25))
                mBinding.loginSmsCodeEdt.filters = filters
                isSMSLogin = false
            }

            override fun OnMoreErrorClick() {}
        })

        mBinding.loginWechatLogin.setOnClickListener(object : NoMoreClickListener(){
            override fun OnMoreClick(view: View) {
                wxShare = WXShare(this@LoginActivity)
                wxShare!!.register()
                wxShare!!.getAuth(object : ItemCallback {
                    override fun onItemCallback(position: Int, obj: Any?) {
                        if (null == obj ) {
                            ToastUtils.show("授权失败！")
                            return
                        }
                        val reqDateMap = HashMap<String, Any>()
                        reqDateMap["way"] = 3
                        reqDateMap["code"] = obj
                        mViewModel.login(reqDateMap)
                    }

                    override fun onDetailCallBack(position: Int, obj: Any) {}
                })
            }

            override fun OnMoreErrorClick() {
            }

        })


        mBinding.loginSendSmsTv.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                val form = Form()
                val phoneValiv = Validate(mBinding.loginNumberEdt)
                phoneValiv.addValidator(PhoneValidator(this@LoginActivity))
                form.addValidates(phoneValiv)
                val isOk = form.validate()
                if (isOk) {
                    timer.start()
                    mViewModel.sendSMS(mBinding.loginNumberEdt.text.toString())
                }
            }

            override fun OnMoreErrorClick() {}
        })
        mBinding.loginAskBtn.isClickable = false
        mBinding.loginAskBtn.isEnabled = false
        mBinding.loginAskBtn.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                val form = Form()
                val phoneValiv = Validate(mBinding.loginNumberEdt)
                phoneValiv.addValidator(PhoneValidator(this@LoginActivity))
                form.addValidates(phoneValiv)
                val smsValiv = Validate(mBinding.loginSmsCodeEdt)
                form.addValidates(smsValiv)

                val isOk = form.validate()
                if (isOk) {
                    val reqDateMap = HashMap<String, Any>()
                    reqDateMap["phone"] = mBinding.loginNumberEdt.text.toString().trim { it <= ' ' }
                    val loginType: Int
                    if (isSMSLogin) {
                        loginType =2
                        reqDateMap["code"] = mBinding.loginSmsCodeEdt.text.toString().trim { it <= ' ' }
                    } else {
                        loginType =1
                        reqDateMap["password"] = mBinding.loginSmsCodeEdt.text.toString().trim { it <= ' ' }
                    }
                    reqDateMap["way"] = loginType
                    mViewModel.login(reqDateMap)
                }
            }

            override fun OnMoreErrorClick() {}
        })

        mBinding.loginSmsCodeEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (StringUtil.isEmpty(s.toString().trim { it <= ' ' })) {
                    mBinding.loginAskBtn.isEnabled = false
                    mBinding.loginAskBtn.isClickable = false
                    mBinding.loginAskBtn.background = ResourcesCompat.getDrawable(resources,R.drawable.gray_bg_shape,null)
                }
                val pattern = Pattern.compile("^1[0-9]{10}$")
                if (isSMSLogin) {
                    val pasPattern = Pattern.compile("[0-9]{4}$")
                    if (pattern.matcher(mBinding.loginNumberEdt.text.toString().trim { it <= ' ' })
                            .matches() && pasPattern.matcher(s.toString().trim { it <= ' ' })
                            .matches()
                    ) {
                        mBinding.loginAskBtn.isClickable = true
                        mBinding.loginAskBtn.isEnabled = true
                        mBinding.loginAskBtn.background = ResourcesCompat.getDrawable(resources,R.drawable.deep_blue_bg_shape,null)
                    } else {
                        mBinding.loginAskBtn.isEnabled = false
                        mBinding.loginAskBtn.isClickable = false
                        mBinding.loginAskBtn.background = ResourcesCompat.getDrawable(resources,R.drawable.gray_bg_shape,null)
                    }
                } else {
                    if (pattern.matcher(mBinding.loginNumberEdt.text.toString().trim { it <= ' ' })
                            .matches() && !StringUtil.isEmpty(mBinding.loginSmsCodeEdt.text.toString().trim { it <= ' ' })
                    ) {
                        mBinding.loginAskBtn.isClickable = true
                        mBinding.loginAskBtn.isEnabled = true
                        mBinding.loginAskBtn.background = ResourcesCompat.getDrawable(resources,R.drawable.deep_blue_bg_shape,null)
                    } else {
                        mBinding.loginAskBtn.isEnabled = false
                        mBinding.loginAskBtn.isClickable = false
                        mBinding.loginAskBtn.background = ResourcesCompat.getDrawable(resources,R.drawable.gray_bg_shape,null)
                    }
                }
            }
        })

        mBinding.loginNumberEdt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (StringUtil.isEmpty(s.toString().trim { it <= ' ' })) return
                val pattern = Pattern.compile("^1[0-9]{10}$")
                if (pattern.matcher(s.toString().trim { it <= ' ' }).matches()) {
                    mBinding.loginSendSmsTv.isClickable = true
                    mBinding.loginSendSmsTv.setTextColor(ResourcesCompat.getColor(resources,R.color.colorAccent,null))
                } else {
                    mBinding.loginSendSmsTv.isClickable = false
                    mBinding.loginSendSmsTv.setTextColor(ResourcesCompat.getColor(resources,R.color.grayText1,null))
                }
                if (isSMSLogin) {
                    val pasPattern = Pattern.compile("[0-9]{4}$")
                    if (pattern.matcher(s.toString().trim { it <= ' ' })
                            .matches() && pasPattern.matcher(
                            mBinding.loginSmsCodeEdt.text.toString().trim { it <= ' ' }).matches()
                    ) {
                        mBinding.loginAskBtn.isClickable = true
                        mBinding.loginAskBtn.isEnabled = true
                        mBinding.loginAskBtn.background = ResourcesCompat.getDrawable(resources,R.drawable.deep_blue_bg_shape,null)
                    } else {
                        mBinding.loginAskBtn.isEnabled = false
                        mBinding.loginAskBtn.isClickable = false
                        mBinding.loginAskBtn.background = ResourcesCompat.getDrawable(resources,R.drawable.gray_bg_shape,null)
                    }
                } else {
                    if (pattern.matcher(s.toString().trim { it <= ' ' }).matches() && !StringUtil.isEmpty(
                            mBinding.loginSmsCodeEdt.text.toString().trim { it <= ' ' })
                    ) {
                        mBinding.loginAskBtn.isClickable = true
                        mBinding.loginAskBtn.isEnabled = true
                        mBinding.loginAskBtn.background = ResourcesCompat.getDrawable(resources,R.drawable.deep_blue_bg_shape,null)
                    } else {
                        mBinding.loginAskBtn.isEnabled = false
                        mBinding.loginAskBtn.isClickable = false
                        mBinding.loginAskBtn.background = ResourcesCompat.getDrawable(resources,R.drawable.gray_bg_shape,null)
                    }
                }
            }
        })

    }

//    private fun jumpWeb(title: String, url: String) {
//        val intent = Intent(this@LoginActivity, WebViewActivity::class.java)
//        intent.putExtra("url", url)
//        intent.putExtra("from", title)
//        startActivity(intent)
//    }

    override fun initObserve() {
        mViewModel.loginResult.observe(this@LoginActivity) { it ->
            parseState(it, {
                SMApplication.instance.token = it.token
                val kv = MMKV.defaultMMKV()
                val sp = getSharedPreferences(Constants.SharedPreferences_name, MODE_PRIVATE)
                val editor = sp.edit()
//                if (it.initPwd == 1) { //是否需要设定密码 0:否 1:是
//                    kv.encode(Constants.isLogin,true)
//                    kv.encode(Constants.token, it.token)
//                    kv.encode(Constants.isSetPsd, false)
//                    kv.encode(Constants.loginInfo, GsonHelper.toJsonString(it))
//
//                    editor.putString(Constants.loginInfo, GsonHelper.toJsonString(it))
//                    editor.apply()
//                    editor.commit()
//
//                    val intent = Intent(this@LoginActivity, SetPasswordActivity::class.java)
//                    intent.putExtra("title", "设置密码")
//                    intent.putExtra("back", "")
//                    intent.putExtra("isSetPwd", true)
//                    startActivity(intent)
//                    finish()
//                } else {
                    kv.encode(Constants.isLogin, true)
                    kv.encode(Constants.token, it.token)
//                    kv.encode(Constants.isSetPsd, true)
                    kv.encode(Constants.loginInfo, GsonHelper.toJsonString(it))

                    editor.putString(Constants.loginInfo, GsonHelper.toJsonString(it))
                    editor.apply()
                    editor.commit()

                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
//                }
            }, {
                ToastUtils.show(it.errorMsg)
            })
        }
        mViewModel.sendSmsLiveData.observe(this){ it ->
            parseState(it,{
                ToastUtils.show("验证码获取成功！")
            },{
                ToastUtils.show(it.errorMsg)
            })
        }
    }

    override fun initRequestData() {
//        mViewModel.getUserAgreementData()//获取用户协议
//        mViewModel.getImageCode()//获取图片验证码信息
    }

    //记录用户首次点击返回键的时间
    private var firstTime: Long = 0
    public override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val secondTime: Long = System.currentTimeMillis()
            if (secondTime - firstTime > 2000) {
                ToastUtils.show("再按一次退出应用")
                firstTime = secondTime
                return true
            } else {
                System.exit(0)
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}