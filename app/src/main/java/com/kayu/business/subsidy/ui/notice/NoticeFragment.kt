package com.kayu.business.subsidy.ui.notice

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.flyco.tablayout.listener.OnTabSelectListener
import com.hoom.library.common.ext.parseState
import com.hoom.library.common.ui.BaseFragment
import com.kayu.business.subsidy.MainActivity
import com.kayu.business.subsidy.R
import com.kayu.business.subsidy.SMApplication
import com.kayu.business.subsidy.data.bean.MessageBean
import com.kayu.business.subsidy.data.bean.MsgContentBean
import com.kayu.business.subsidy.databinding.FragmentNoticeBinding
import com.kayu.business.subsidy.databinding.ItemMessageContentLayBinding
import com.kayu.business.subsidy.databinding.ItemMessageLayBinding
import com.kayu.business.subsidy.ui.WebViewActivity
import com.kayu.utils.LogUtil
import com.kayu.utils.NoMoreClickListener
import com.kongzue.dialog.v3.WaitDialog
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.notifyAll

@AndroidEntryPoint
class NoticeFragment : BaseFragment<FragmentNoticeBinding, NoticeViewModel>() {

    companion object {
        fun newInstance() = NoticeFragment()
    }

    override val mViewModel: NoticeViewModel by viewModels()

    override fun showLoading(message: String) {
    }

    override fun dismissLoading() {
    }


    override fun onResume() {
        super.onResume()
        if (userVisibleHint && !mViewModel.mHasLoadedOnce) {
//            mBinding.noticeRefreshLayout.autoRefresh()
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


    override fun FragmentNoticeBinding.initView() {
        if (activity?.javaClass?.equals(MainActivity::class.java) == true){
            mBinding.fragTitleRootLay.visibility = View.VISIBLE
        }else{
            mBinding.fragTitleRootLay.visibility = View.GONE
        }

        mBinding.noticeRefreshLayout.setEnableAutoLoadMore(false)
        mBinding.noticeRefreshLayout.setEnableLoadMore(true)
        mBinding.noticeRefreshLayout.setEnableLoadMoreWhenContentNotFull(true) //是否在列表不满一页时候开启上拉加载功能

        mBinding.noticeRefreshLayout.setEnableOverScrollBounce(true) //是否启用越界回弹

        mBinding.noticeRefreshLayout.setEnableOverScrollDrag(true)
        mBinding.noticeRefreshLayout.setOnRefreshListener(OnRefreshListener {
            if (mViewModel.isRefresh || mViewModel.isLoadMore) return@OnRefreshListener
            mViewModel.isRefresh = true
            mViewModel.pageIndex = 1
            if (null != mViewModel.adapter) {
                mViewModel.adapter!!.data.clear()
                mViewModel.adapter!!.notifyDataSetChanged()
            }
            reqListData(mViewModel.pageIndex)
        })
        mBinding.noticeRefreshLayout.setOnLoadMoreListener(OnLoadMoreListener {
            if (mViewModel.isRefresh || mViewModel.isLoadMore) return@OnLoadMoreListener
            mViewModel.isLoadMore = true
            mViewModel.pageIndex += 1
            reqListData(mViewModel.pageIndex)
        })

        mBinding.noticeRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        mBinding.noticeTabLay.setTabData(mViewModel.mTabEntities)
        mBinding.noticeTabLay.setOnTabSelectListener(object : OnTabSelectListener {
            override fun onTabSelect(position: Int) {
                if (position == 0){
                    mViewModel.msgType = null
                }else{
                    mViewModel.msgType = position-1
                }

                mViewModel.pageIndex = 1
                mViewModel.isRefresh = true
                if (null != mViewModel.adapter) {
                    mViewModel.adapter!!.data.clear()
                    mViewModel.adapter!!.notifyDataSetChanged()
                }
                reqListData(mViewModel.pageIndex)
            }

            override fun onTabReselect(position: Int) {}
        })
    }

    override fun initObserve() {
        mViewModel.messageListResult.observe(this){ it ->
            parseState(it,{
                val listNewData = mutableListOf<MessageBean>()
                it.list?.let { it1 -> listNewData.addAll(it1) }
                if (null == mViewModel.messageListData.value ) {
                    mViewModel.messageListData.value = mutableListOf()
                } else {
                    if (mViewModel.isRefresh) {
                        mViewModel.messageListData.value?.clear()
                    }
                }
                mViewModel.messageListData.value?.addAll(listNewData)
                loadListData(mViewModel.messageListData.value as MutableList<MessageBean>)
            },{
                LogUtil.e("mySettlementListResult",it.errorMsg)
            })
            if (mViewModel.isRefresh) {
                mBinding.noticeRefreshLayout.finishRefresh()
                mViewModel.isRefresh = false
            }
            if (mViewModel.isLoadMore) {
                mBinding.noticeRefreshLayout.finishLoadMore()
                mViewModel.isLoadMore = false
            }
            WaitDialog.dismiss()
        }
    }

    override fun initRequestData() {

    }

    private fun loadListData(list:MutableList<MessageBean>){
        if (null != mViewModel.adapter) {
            mViewModel.adapter?.data?.clear()
            mViewModel.adapter?.addData(list)
        } else {
            createAdapter(list)
        }
    }

    private fun createAdapter(list:MutableList<MessageBean>){
        val newList = mutableListOf<MessageBean>()
        newList.addAll(list)
        mViewModel.adapter = object : BaseQuickAdapter<MessageBean, BaseDataBindingHolder<ItemMessageLayBinding>>
            (R.layout.item_message_lay, newList) {
            @SuppressLint("Range")
            override fun convert(
                holder: BaseDataBindingHolder<ItemMessageLayBinding>,
                item: MessageBean
            ) {
                if (null != holder.dataBinding) {
                    if (null != item.title){
                        holder.dataBinding!!.itemMsgTitle.text = item.title?.value
                        if (!item.title?.color.isNullOrEmpty()){
                            holder.dataBinding!!.itemMsgTitle.setTextColor(Color.parseColor( item.title?.color))
                        }
                        holder.dataBinding!!.itemMsgTitle.visibility =View.VISIBLE
                    }else {
                        holder.dataBinding!!.itemMsgTitle.visibility =View.GONE
                    }
                    if (null != item.first){
                        holder.dataBinding!!.itemMsgFirst.text = item.first?.value
                        if (!item.first?.color.isNullOrEmpty()){
                            holder.dataBinding!!.itemMsgFirst.setTextColor(Color.parseColor( item.first?.color))
                        }
                        holder.dataBinding!!.itemMsgFirst.visibility =View.VISIBLE
                    }else{
                        holder.dataBinding!!.itemMsgFirst.visibility =View.GONE
                    }
                    if (null != item.remark){
                        holder.dataBinding!!.itemMsgRemark.text = item.remark?.value
                        if (!item.remark?.color.isNullOrEmpty()){
                            holder.dataBinding!!.itemMsgRemark.setTextColor(Color.parseColor( item.remark?.color ))
                        }
                        holder.dataBinding!!.itemMsgRemark.visibility =View.VISIBLE
                    }else{
                        holder.dataBinding!!.itemMsgRemark.visibility =View.GONE
                    }

                    holder.dataBinding!!.itemMsgTime.text = item.createTime

                    if (item.content.isNullOrEmpty() || item.content!!.isEmpty()){
                        holder.dataBinding!!.itemMsgContentRecycler.visibility = View.GONE
                    }else{
                        holder.dataBinding!!.itemMsgContentRecycler.layoutManager = LinearLayoutManager(requireContext())
                        holder.dataBinding!!.itemMsgContentRecycler.adapter = object : BaseQuickAdapter<MsgContentBean, BaseDataBindingHolder<ItemMessageContentLayBinding>>
                            (R.layout.item_message_content_lay, item.content){
                            override fun convert(
                                contentHolder: BaseDataBindingHolder<ItemMessageContentLayBinding>,
                                itemContent: MsgContentBean
                            ) {
                                if (null !=contentHolder.dataBinding) {
                                    contentHolder.dataBinding!!.itemMsgContent.text = itemContent.title+"："+itemContent.value
                                    if (!itemContent.color.isNullOrEmpty()){
                                        holder.dataBinding!!.itemMsgFirst.setTextColor(Color.parseColor( itemContent.color))
                                    }
                                }
                            }

                        }
                        holder.dataBinding!!.itemMsgContentRecycler.visibility = View.VISIBLE
                    }


                    if (!item.redirectUrl.isNullOrEmpty()){
                        holder.dataBinding!!.itemMsgDetail.setOnClickListener(object :
                            NoMoreClickListener(){
                            override fun OnMoreClick(view: View) {
                                val intent = Intent(requireContext(), WebViewActivity::class.java)
                                val sb = StringBuilder()
                                sb.append(item.redirectUrl)
                                if (item.redirectUrl!!.contains("?")) {
                                    sb.append("&token=")
                                } else {
                                    sb.append("?token=")
                                }
                                sb.append(SMApplication.instance.token)
                                intent.putExtra("url", sb.toString())
                                intent.putExtra("from", "")
                                startActivity(intent)
                            }

                            override fun OnMoreErrorClick() {
                            }
                        })
                        holder.dataBinding!!.itemMsgDetail.visibility = View.VISIBLE
                    }else {
                        holder.dataBinding!!.itemMsgDetail.setOnClickListener(null)
                        holder.dataBinding!!.itemMsgDetail.visibility = View.GONE
                    }

                }
            }
        }
        mViewModel.adapter?.setEmptyView(LayoutInflater.from(requireContext()).inflate(R.layout.empty_view_tab, null))
        mBinding.noticeRecyclerView.adapter = mViewModel.adapter
    }


    private fun reqListData(page: Int){
        val reqDateMap = HashMap<String, Any?>()
        reqDateMap["pageNow"] = page
        reqDateMap["pageSize"] = 10
        reqDateMap["type"] = mViewModel.msgType
        mViewModel.getNoticeList(reqDateMap)
    }


}