package com.kayu.business.subsidy.ui.recruit

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.flyco.tablayout.TabEntity
import com.flyco.tablayout.listener.CustomTabEntity
import com.flyco.tablayout.listener.OnTabSelectListener
import com.hoom.library.common.ext.parseState
import com.hoom.library.common.ui.BaseFragment
import com.kayu.business.subsidy.R
import com.kayu.business.subsidy.data.bean.ExtendCopyBean
import com.kayu.business.subsidy.data.bean.ItemOrderBean
import com.kayu.business.subsidy.databinding.FragmentPromoteBinding
import com.kayu.business.subsidy.databinding.ItemExtensionLayoutBinding
import com.kayu.business.subsidy.databinding.ItemOrderLayBinding
import com.kayu.business.subsidy.ui.order.OrderDetailActivity
import com.kayu.utils.LogUtil
import com.kayu.utils.NoMoreClickListener
import com.kayu.utils.StringUtil
import com.kongzue.dialog.v3.WaitDialog
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.HashMap

@AndroidEntryPoint
class PromoteFragment : BaseFragment<FragmentPromoteBinding, PromoteViewModel>(){
    override val mViewModel: PromoteViewModel by viewModels()

    override fun onResume() {
        super.onResume()
        if (!mViewModel.mHasLoadedOnce) {
//            mBinding.orderRefreshLayout.autoRefresh()
            WaitDialog.show(
                requireActivity() as AppCompatActivity,
                "加载中..."
            )
            mViewModel.isRefresh = true
            mViewModel.pageIndex = 1
            if (null != mViewModel.adapter) {
                mViewModel.adapter!!.data.clear()
                mViewModel.adapter!!.notifyDataSetChanged()
            }
            reqListData(mViewModel.pageIndex)
            mViewModel.mHasLoadedOnce = true
        }
    }

    override fun showLoading(message: String) {
    }

    override fun dismissLoading() {
    }

    override fun initObserve() {
        mViewModel.extensionPageResult.observe(this){ it ->
            parseState(it,{
                val listNewData = mutableListOf<ExtendCopyBean>()
                it.list?.let { it1 -> listNewData.addAll(it1) }
                if (null == mViewModel.extensionListData.value ) {
                    mViewModel.extensionListData.value = mutableListOf()
                } else {
                    if (mViewModel.isRefresh) {
                        mViewModel.extensionListData.value?.clear()
                    }
                }
                mViewModel.extensionListData.value?.addAll(listNewData)
                loadListData(mViewModel.extensionListData.value as MutableList<ExtendCopyBean>)
            },{
                LogUtil.e("PromoteFragment",it.errorMsg)
            })
            if (mViewModel.isRefresh) {
                mBinding.orderRefreshLayout.finishRefresh()
                mViewModel.isRefresh = false
            }
            if (mViewModel.isLoadMore) {
                mBinding.orderRefreshLayout.finishLoadMore()
                mViewModel.isLoadMore = false
            }
            WaitDialog.dismiss()
        }
    }

    override fun initRequestData() {
    }

    override fun FragmentPromoteBinding.initView() {
        mBinding.orderRefreshLayout.setEnableAutoLoadMore(false)
        mBinding.orderRefreshLayout.setEnableLoadMore(true)
        mBinding.orderRefreshLayout.setEnableLoadMoreWhenContentNotFull(true) //是否在列表不满一页时候开启上拉加载功能

        mBinding.orderRefreshLayout.setEnableOverScrollBounce(true) //是否启用越界回弹

        mBinding.orderRefreshLayout.setEnableOverScrollDrag(true)
        mBinding.orderRefreshLayout.setOnRefreshListener(OnRefreshListener {
            if (mViewModel.isRefresh || mViewModel.isLoadMore) return@OnRefreshListener
            mViewModel.isRefresh = true
            mViewModel.pageIndex = 1
            if (null != mViewModel.adapter) {
                mViewModel.adapter!!.data.clear()
                mViewModel.adapter?.notifyDataSetChanged()
            }
            reqListData(mViewModel.pageIndex)
        })
        mBinding.orderRefreshLayout.setOnLoadMoreListener(OnLoadMoreListener {
            if (mViewModel.isRefresh || mViewModel.isLoadMore) return@OnLoadMoreListener
            mViewModel.isLoadMore = true
            mViewModel.pageIndex += 1
            reqListData(mViewModel.pageIndex)
        })

        mBinding.promoteRecyclerView.layoutManager = LinearLayoutManager(requireContext())


        val mTabEntities1 = ArrayList<CustomTabEntity>()
        mTabEntities1.add(TabEntity("招募文案", R.mipmap.ic_bg_close, R.mipmap.ic_bg_close))
        mTabEntities1.add(TabEntity("批卡文案", R.mipmap.ic_bg_close, R.mipmap.ic_bg_close))
        mBinding.promoteTabLay.setTabData(mTabEntities1)

        mBinding.promoteTabLay.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                mViewModel.type = position+1
                mViewModel.pageIndex = 1
                mViewModel.isRefresh = true
                if (null != mViewModel.adapter) {
                    mViewModel.adapter!!.data.clear()
                    mViewModel.adapter?.notifyDataSetChanged()
                }
                reqListData(mViewModel.pageIndex)
            }

            override fun onTabReselect(position: Int) {}
        })

    }




    private fun reqListData(page: Int){
        val reqDateMap = HashMap<String, Any>()
        reqDateMap["pageNow"] = page
        reqDateMap["type"] = mViewModel.type
        reqDateMap["pageSize"] = 10
        reqDateMap["isDraw"] = 1

        mViewModel.getExtensionList(reqDateMap)

    }


    private fun loadListData(list:MutableList<ExtendCopyBean>){
        if (null != mViewModel.adapter) {
            mViewModel.adapter?.data?.clear()
            mViewModel.adapter?.addData(list)
        } else {
            createAdapter(list)
        }
    }

    private fun createAdapter(list:MutableList<ExtendCopyBean>){
        val newList = mutableListOf<ExtendCopyBean>()
        newList.addAll(list)
        mViewModel.adapter = SharedAdapter(requireActivity(),newList)
        mViewModel.adapter?.setEmptyView(LayoutInflater.from(requireContext()).inflate(R.layout.empty_view_tab, null))
        mBinding.promoteRecyclerView.adapter = mViewModel.adapter
    }



}