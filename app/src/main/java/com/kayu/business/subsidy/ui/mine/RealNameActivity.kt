package com.kayu.business.subsidy.ui.mine

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.hjq.toast.ToastUtils
import com.hoom.library.common.ext.parseState
import com.hoom.library.common.ui.BaseActivity
import com.kayu.business.subsidy.databinding.ActivityRealNameBinding
import com.kayu.form_verify.Form
import com.kayu.form_verify.Validate
import com.kayu.form_verify.validator.NotEmptyValidator
import com.kayu.form_verify.validator.NumberValidator
import com.kayu.business.subsidy.R
import com.kayu.utils.NoMoreClickListener
import com.kayu.utils.StringUtil.isEmpty
import com.kayu.utils.status_bar_set.StatusBarUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RealNameActivity : BaseActivity<ActivityRealNameBinding, RealNameViewModel>() {

    override val mViewModel: RealNameViewModel by viewModels()

    override fun setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white))
    }


    override fun ActivityRealNameBinding.initView() {

        mViewModel.title = intent.getStringExtra("title").toString()
        mViewModel.back = intent.getStringExtra("back").toString()
        mViewModel.username = intent.getStringExtra("username")
        mViewModel.idNo = intent.getStringExtra("idNo")

        //标题栏
        mBinding.RealTitle.titleNameTv.text = mViewModel.title
        mBinding.RealTitle.titleBackBtu.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }

            override fun OnMoreErrorClick() {}
        })
        mBinding.RealTitle.titleBackTv.text = mViewModel.back

        if (isEmpty(mViewModel.idNo)) {
            mBinding.realNameEdt.isEnabled = true
            mBinding.realIdNumberEdt.isEnabled = true
        } else {
            mBinding.realNameEdt.isEnabled = false
            mBinding.realNameEdt.setText(mViewModel.username)
            mBinding.realIdNumberEdt.isEnabled = false
            mBinding.realIdNumberEdt.setText(mViewModel.idNo)
        }


        mBinding.realAskBtn.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                val form = Form()
                val phoneVali = Validate(mBinding.realPhoneEdt)
                phoneVali.addValidator(NumberValidator(this@RealNameActivity))
                form.addValidates(phoneVali)
                val amountVali = Validate(mBinding.realCardnumEdt)
                amountVali.addValidator(NumberValidator(this@RealNameActivity))
                form.addValidates(amountVali)
                val nameVal = Validate(mBinding.realNameEdt)
                nameVal.addValidator(NotEmptyValidator(this@RealNameActivity))
                form.addValidates(nameVal)
                val id_numberVal = Validate(mBinding.realIdNumberEdt)
                id_numberVal.addValidator(NotEmptyValidator(this@RealNameActivity))
                form.addValidates(id_numberVal)
                if (form.validate()) {
                    val reqDateMap = HashMap<String, Any>()
                    reqDateMap["cardNo"] = mBinding.realCardnumEdt.text.toString()
                    reqDateMap["mobileNo"] = mBinding.realPhoneEdt.text.toString()
                    if (isEmpty(mViewModel.idNo)) {
                        reqDateMap["username"] = mBinding.realNameEdt.text.toString()
                        reqDateMap["idNo"] = mBinding.realIdNumberEdt.text.toString()
                        mViewModel.setUserVerified(reqDateMap)
                    } else {
                        mViewModel.addDebitCard(reqDateMap)
                    }
                }
            }

            override fun OnMoreErrorClick() {}
        })

    }

    override fun initObserve() {
        mViewModel.userVerifiedResult.observe(this) { it ->
            parseState(it, {
                if (isEmpty(mViewModel.idNo)) {
                    ToastUtils.show("认证成功")
                } else {
                    ToastUtils.show("添加成功")
                }
                val intent = Intent()
                setResult(2, intent)
                finish()
            }, {
                ToastUtils.show(it.errorMsg)
            })
        }
    }

    override fun initRequestData() {
    }

    override fun onBackPressed() {
        val intent = Intent()
        this@RealNameActivity.setResult(2, intent)
        this@RealNameActivity.finish()
    }
}