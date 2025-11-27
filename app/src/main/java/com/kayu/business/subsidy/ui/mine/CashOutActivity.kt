package com.kayu.business.subsidy.ui.mine

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import coil.load
import com.hjq.toast.ToastUtils
import com.hoom.library.base.ktx.visible
import com.hoom.library.common.ext.parseState
import com.hoom.library.common.ui.BaseActivity
import com.kayu.business.subsidy.R
import com.kayu.business.subsidy.databinding.ActivityCashOutBinding
import com.kayu.form_verify.Form
import com.kayu.form_verify.Validate
import com.kayu.form_verify.validator.NumberValidator
import com.kayu.utils.AppUtil
import com.kayu.utils.AppUtil.getStringAmount
import com.kayu.utils.AppUtil.getStringIntAmount
import com.kayu.utils.NoMoreClickListener
import com.kayu.utils.StringUtil.isEmpty
import com.kayu.utils.status_bar_set.StatusBarUtil
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener
import com.kongzue.dialog.util.BaseDialog
import com.kongzue.dialog.v3.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CashOutActivity : BaseActivity<ActivityCashOutBinding,CashOutViewModel>() {

    var startActivityLaunch: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode== 2) {
            mViewModel.getUserDetail()
        } else if (it.resultCode == 3) {
            onBackPressed()
        }
    }

    override fun setStatusBar() {
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.white))
    }

    override fun onDestroy() {
        super.onDestroy()
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimary))
    }


    override val mViewModel: CashOutViewModel by viewModels()

    override fun ActivityCashOutBinding.initView() {
        mViewModel.title = intent.getStringExtra("title").toString()
        mViewModel.back = intent.getStringExtra("back").toString()

        //标题栏
        mBinding.cashTitle.titleNameTv.text = mViewModel.title
        mBinding.cashTitle.titleBackBtu.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                onBackPressed()
            }
            override fun OnMoreErrorClick() {}
        })
        mBinding.cashTitle.titleBackTv.text = mViewModel.back


        mBinding.cashHavAmountEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (isEmpty(s.toString().trim { it <= ' ' })
                    || s.toString() == "0.00"
                    || s.toString() == "0.0"
                    || s.toString() == "0") {
                    mBinding.cashAskBtn.setOnClickListener(null)
                    mBinding.cashAskBtn.setBackgroundResource(R.drawable.small_blue_bg_shape_def)
                    mBinding.cashAskBtn.setTextColor(ResourcesCompat.getColor(resources,R.color.grayText,null))
                    mBinding.cashOutAmount.setTextColor(ResourcesCompat.getColor(resources,R.color.grayText,null))
                    if (null != mViewModel.userDetails) {
                        var strAmo =  "当前可提现余额:" + getStringAmount(mViewModel.userDetails!!.surplusAmt) + "元。"
                        val spanStr = SpannableString(strAmo)
                        val colorSpan = ForegroundColorSpan(ResourcesCompat.getColor(resources,R.color.yellow,null))
                        spanStr.setSpan(colorSpan,8,strAmo.length-2,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        mBinding.cashOutAmount.text =spanStr
                        
                    }
                    mBinding.cashAllCashOutTv.visibility = View.VISIBLE
                } else {
                    if (null != mViewModel.userDetails) {
                        if (s.toString().trim { it <= ' ' }.toFloat()>
                            getStringAmount(mViewModel.userDetails!!.surplusAmt).toFloat() ) {
                            mBinding.cashOutAmount.setText("输入金额超过可提现余额")
                            mBinding.cashOutAmount.setTextColor(Color.RED)
                            mBinding.cashAllCashOutTv.visibility = View.GONE
                        } else {
                            mBinding.cashOutAmount.setTextColor(resources.getColor(R.color.grayText))
                            var strAmo =  "当前可提现余额:" + getStringAmount(mViewModel.userDetails!!.surplusAmt) + "元。"
                            val spanStr = SpannableString(strAmo)
                            val colorSpan = ForegroundColorSpan(ResourcesCompat.getColor(resources,R.color.yellow,null))
                            spanStr.setSpan(colorSpan,8,strAmo.length-2,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                            mBinding.cashOutAmount.text =spanStr
                            mBinding.cashAllCashOutTv.visibility = View.VISIBLE
                        }
                        mBinding.cashAskBtn.setBackgroundResource(R.drawable.small_blue_bg_shape)
                        mBinding.cashAskBtn.setTextColor(resources.getColor(R.color.white))
                        mBinding.cashAskBtn.setOnClickListener(object : NoMoreClickListener(){
                            override fun OnMoreClick(view: View) {
                                if (null == mViewModel.selectedCard) {
                                    TipDialog.show(this@CashOutActivity,"请先绑定银行卡",TipDialog.TYPE.WARNING)
                                    return
                                }
                                if (mBinding.cashHavAmountEt.getText().toString().trim { it <= ' ' }
                                        .toFloat() > getStringAmount(mViewModel.userDetails!!.surplusAmt).toFloat()) {
                                    ToastUtils.show("超过本次可提现余额")
                                    return
                                }
                                val amountVali = Validate(mBinding.cashHavAmountEt)
                                amountVali.addValidator(NumberValidator(this@CashOutActivity))
                                val form = Form()
                                form.addValidates(amountVali)
                                if (form.validate()) {
                                    showConfirmDialog()
//                                    val cashAmount: Int = mBinding.cashHavAmountEt.text.toString().trim { it <= ' ' }.toInt()
//                                    val amount = cashAmount * 100L
//                                    val reqDateMap = HashMap<String, Any>()
//                                    reqDateMap["amount"] = amount
//                                    reqDateMap["settleCardId"] = mViewModel.selectedCard!!.id
//                                    mViewModel.applyCashOut(reqDateMap)
                                }
                            }

                            override fun OnMoreErrorClick() {}
                        })
                    }
                }
            }
        })
        mBinding.cashAskBtn.setOnClickListener(null)

        mBinding.cashAllCashOutTv.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                if (null != mViewModel.userDetails) {
                    mBinding.cashHavAmountEt.setText(getStringIntAmount(mViewModel.userDetails!!.surplusAmt))
//                    mBinding.cashHavAmountEt.setSelection(getStringAmount(mViewModel.userDetails!!.surplusAmt).length)
                }
            }

            override fun OnMoreErrorClick() {}
        })

        mBinding.cashChangeCard.setOnClickListener(object : NoMoreClickListener() {
            override fun OnMoreClick(view: View) {
                showCustomDialog()
            }

            override fun OnMoreErrorClick() {}
        })
    }

    var cardDialog :CustomSelectListDialog? = null
    private fun showCustomDialog() {
        val names: MutableList<ItemData> = ArrayList<ItemData>()
        if (null != mViewModel.cardsList && mViewModel.cardsList!!.isNotEmpty()) {
            for (card in mViewModel.cardsList!!) {
                val itemData = ItemData()
                itemData.title =
                    card.bankName + "(" + card.cardNo.substring(card.cardNo.length - 4) + ")"
                itemData.logoUrl = card.logo
                itemData.id = card.id
                itemData.isShowDel = true
                names.add(itemData)
            }
        }
        val itemData = ItemData()
        itemData.title = "添加新卡提现"
        itemData.logoUrl = ""
        itemData.isShowDel = false
        names.add(itemData)
        cardDialog = showDialog({ parent, view, position, id ->
                if (position == names.size - 1) { //是最后一项
                    val intent = Intent(this@CashOutActivity, RealNameActivity::class.java)
                    intent.putExtra("title", "添加银行卡")
                    intent.putExtra("back", title)
                    intent.putExtra("username", mViewModel.userDetails!!.username)
                    intent.putExtra("idNo", mViewModel.userDetails!!.idNo)
                    startActivityLaunch.launch(intent)
                }
                else {
                    if (null != mViewModel.cardsList && mViewModel.cardsList!!.isNotEmpty()) {
                        mViewModel.selectedCard = mViewModel.cardsList!![position]
                        if (null != mViewModel.selectedCard) {
                            mBinding.cashBankName.text = mViewModel.selectedCard!!.bankName
                            mBinding.cashBankAccount.text = mViewModel.selectedCard!!.cardNo
                            mBinding.cashBankUsername.text = mViewModel.userDetails!!.username
                            mBinding.cashBankLogo.load(mViewModel.selectedCard!!.logo){
                                this.allowHardware(false)
                            }
                        }
                    }
                }
            },
            { view, itemData1 ->
                (view as ImageView).load(itemData1.logoUrl){
                    this.allowHardware(false)
                }
            },{  item ->
                val reqDateMap = HashMap<String, Any>()
                reqDateMap["id"] = item.id
                mViewModel.delDebitCard(reqDateMap)
            }, names
        )
    }

    /**
     * 展示对话框视图，构造方法创建对象
     */
    private fun showDialog(
        listener: CustomSelectListDialog.SelectDialogListener,
        addLogoListener: CustomSelectListDialog.AddLogoListener,
        deleteItemListener: CustomSelectListDialog.DeleteItemListener,
        names: List<ItemData>
    ): CustomSelectListDialog? {
        val dialog = CustomSelectListDialog(
            this,
            R.style.transparentFrameWindowStyle,
            listener,
            addLogoListener,
            deleteItemListener,
            names,
            "选择到账银行卡",
            "请留意各银行到账时间"
        )
        //        dialog.setItemColor(R.color.colorAccent,R.color.colorAccent);
        //判断activity是否finish
        if (!this.isFinishing) {
            dialog.show()
        }
        return dialog
    }

    private fun showConfirmDialog( ) {
        MessageDialog.show((this), "提示", "提现金额：" + AppUtil.getDoubleAmount(mBinding.cashHavAmountEt.getText().toString()), "确认")
            .setOkButton(
                OnDialogButtonClickListener { baseDialog, v ->
                    val cashAmount: Int = mBinding.cashHavAmountEt.text.toString().trim { it <= ' ' }.toInt()
                    val amount = cashAmount * 100L
                    val reqDateMap = HashMap<String, Any>()
                    reqDateMap["amount"] = amount
                    reqDateMap["settleCardId"] = mViewModel.selectedCard!!.id
                    mViewModel.applyCashOut(reqDateMap)
                    false
                })
    }


    @SuppressLint("SetTextI18n")
    override fun initObserve() {
        mViewModel.applyCashOutResult.observe(this@CashOutActivity){ it->
            parseState(it,{
                ToastUtils.show("操作成功！")
            },{
                ToastUtils.show(it.errorMsg)
            })
        }
        mViewModel.delDebitCardResult.observe(this@CashOutActivity){ it->
            parseState(it,{
                mViewModel.getUserDetail()
                ToastUtils.show("解绑成功！")
                cardDialog?.dismiss()
            },{
                ToastUtils.show(it.errorMsg)
            })
        }

        mViewModel.systemParamResult.observe(this){ it->
            parseState(it,{
                mViewModel.tips1 = it?.data1.toString()
                mViewModel.cash_tips = it?.data0.toString()
                mBinding.cashOutTips.text = it?.data1
            },{

            })

        }
        mViewModel.cardListResult.observe(this){ it->
            parseState(it,{
                if (null != it && it.isNotEmpty()) {
                    mViewModel.selectedCard = it[0]
                    mViewModel.cardsList = it
                    mBinding.cashBankName.text = mViewModel.selectedCard!!.bankName
                    mBinding.cashBankAccount.text = mViewModel.selectedCard!!.cardNo
                    mBinding.cashBankUsername.text = mViewModel.userDetails!!.username
                    mBinding.cashBankLogo.load(mViewModel.selectedCard!!.logo){
                        this.allowHardware(false)
                    }
                    mBinding.cashBankInfoLay.visibility = View.VISIBLE
                    mBinding.cashBindCardLay.visibility = View.GONE
                } else {
                    mBinding.cashBankInfoLay.visibility = View.GONE
                    mBinding.cashBindCardLay.visibility = View.VISIBLE
                    mBinding.cashBindCardBtn.setOnClickListener(object :NoMoreClickListener(){
                        override fun OnMoreClick(view: View) {
                            if (null != mViewModel.userDetails) {
                                val intent = Intent(this@CashOutActivity, RealNameActivity::class.java)
                                intent.putExtra("title", "添加银行卡")
                                intent.putExtra("back", title)
                                intent.putExtra("username", if (isEmpty(mViewModel.userDetails!!.username)) "" else mViewModel.userDetails!!.username)
                                intent.putExtra("idNo", if (isEmpty(mViewModel.userDetails!!.idNo)) null else mViewModel.userDetails!!.idNo)
                                startActivityLaunch.launch(intent)
                            }
                        }

                        override fun OnMoreErrorClick() {}
                    })
                }
            },{

            })
        }
        mViewModel.userDetailsResult.observe(this) { it ->
            parseState(it, {
                mViewModel.userDetails = it
                var strAmo =  "当前可提现余额:" + getStringAmount(mViewModel.userDetails!!.surplusAmt) + "元。"
                val spanStr = SpannableString(strAmo)
                val colorSpan = ForegroundColorSpan(ResourcesCompat.getColor(resources,R.color.yellow,null))
                spanStr.setSpan(colorSpan,8,strAmo.length-2,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                mBinding.cashOutAmount.text =spanStr
                if (isEmpty(it.idNo)) {
                    mBinding.cashBindCardBtn.setOnClickListener(object :NoMoreClickListener(){
                        override fun OnMoreClick(view: View) {
                            val intent = Intent(this@CashOutActivity, RealNameActivity::class.java )
                            intent.putExtra("title", "实名认证")
                            intent.putExtra("back", title)
                            startActivityLaunch.launch(intent)
                        }

                        override fun OnMoreErrorClick() {}
                    })
                    mBinding.cashBankInfoLay.visibility = View.GONE
                    mBinding.cashBindCardLay.visibility = View.VISIBLE

                } else {
                    mViewModel.getCardList()
                }
            }, {
                ToastUtils.show(it)
                finish()
            })
        }
    }

    override fun initRequestData() {
        mViewModel.getSysParam()
        mViewModel.getUserDetail()
//        mViewModel.getSysParam()
    }

}