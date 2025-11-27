package com.kayu.business.subsidy.ui.team

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.hjq.toast.ToastUtils
import com.hoom.library.common.ext.parseState
import com.hoom.library.common.ui.BaseFragment
import com.kayu.business.subsidy.MainActivity
import com.kayu.business.subsidy.R
import com.kayu.business.subsidy.SMApplication
import com.kayu.business.subsidy.data.bean.RankData
import com.kayu.business.subsidy.databinding.FragmentPerformanceBinding
import com.kayu.business.subsidy.databinding.ItemRankLayBinding
import com.kayu.business.subsidy.ui.WebViewActivity
import com.kayu.utils.LogUtil
import com.kayu.utils.NoMoreClickListener
import com.kongzue.dialog.v3.WaitDialog
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date


@AndroidEntryPoint
class PerformanceFragment : BaseFragment<FragmentPerformanceBinding, PerformanceViewModel>()   {


    override val mViewModel: PerformanceViewModel by viewModels()


    override fun showLoading(message: String) {
    }

    override fun dismissLoading() {
    }

    override fun onResume() {
        super.onResume()
        if (userVisibleHint && !mViewModel.mHasLoadedOnce) {
//            mBinding.perfRefreshLayout.autoRefresh()
            WaitDialog.show(
                requireActivity() as AppCompatActivity,
                "加载中..."
            )
            mViewModel.isRefresh = true
            reqData()
            mViewModel.mHasLoadedOnce = true
        }
    }

    override fun initObserve() {
        mViewModel.statisticLiveData.observe(this){ it ->
            parseState(it,{ it ->
                val strDate = SimpleDateFormat("MM.dd.yyyy").format(Date())
                val strMonth = strDate.split(".")[0].toInt().toString()
                mBinding.perfCurrentMonthTotal.text = strMonth+"月团队总业绩："
//                val ransk = it.ranks
                val rankList = mutableListOf<RankData>()
                for ( index in 0 until  it.ranks.size){
                    if (index !=0){
                        rankList.add(it.ranks[index])
                    }
                    if (it.ranks[index].isCurrent ==1){
                        if (index == it.ranks.size-1){
                            mBinding.perfUpgradeTip.text ="距离满级还差"+
                                    (it.ranks[index].endCount+1 - it.tmTeamSettleCount).toString()+
                                    "/张"
                        }else {
                            mBinding.perfUpgradeTip.text ="距离"+it.ranks[index+1].rank+
                                    "还差"+ (it.ranks[index].endCount+1 - it.tmTeamSettleCount).toString()+
                                    "/张"
                        }
//                        val difCount =it.ranks[index].endCount+1-it.ranks[index].beginCount
//                        val stopX = ((it.tmTeamSettleCount-it.ranks[index].beginCount+difCount *(index) )/ ( difCount * (ransk.size-1)).toFloat())
                        val stopX = index.toFloat()/(it.ranks.size-1)
//                        val stopX = (20 / (it.ranks[index].endCount * (ransk.size-1)).toFloat())
//                        LogUtil.e("等级划分值",it.ranks[index].rank+"----"+stopX.toString())
                        ConstraintSet().also { adx->
                            adx.clone(mBinding.perfLevelLay)
                            adx.setHorizontalBias(mBinding.perfCurrentLevel.id, stopX)
                        }.applyTo(mBinding.perfLevelLay)
                    }
                }


                mBinding.perfLevel0.text = it.ranks[0].rank
                if(it.ranks[0].isCurrent ==1){
                    mBinding.perfLevel0.setTextColor(resources.getColor(R.color.red,null))
                }
                mBinding.perfRanksRecycler.layoutManager = GridLayoutManager(requireContext(),rankList.size)
                mBinding.perfRanksRecycler.adapter = object :BaseQuickAdapter<RankData,BaseDataBindingHolder<ItemRankLayBinding>>(R.layout.item_rank_lay,rankList){
                    override fun convert(
                        holder: BaseDataBindingHolder<ItemRankLayBinding>,
                        item: RankData
                    ) {
                        if (null != holder.dataBinding) {
                            holder.dataBinding!!.itemRankName.text = item.rank
                            if(item.isCurrent ==1){
                                holder.dataBinding!!.itemRankName.setTextColor(resources.getColor(R.color.red,null))
                            }else{
                                holder.dataBinding!!.itemRankName.setTextColor(resources.getColor(R.color.grayText8,null))
                            }
                        }
                    }

                }
                mBinding.perfMonthTotalNum.text = it.tmTeamSettleCount.toString()
                mBinding.perfTodayIncomeSum.text = it.tdAchievementStatistics.myApplyCount.toString()
                mBinding.perfTodayIncomeTeamSum.text = it.tdAchievementStatistics.teamApplyCount.toString()
                mBinding.perfTodaySubsidySum.text = it.tdAchievementStatistics.mySettleCount.toString()
                mBinding.perfTodaySubsidyTeamSum.text = it.tdAchievementStatistics.teamSettleCount.toString()

                mBinding.perfMonthIncomeSum.text = it.tmAchievementStatistics.myApplyCount.toString()
                mBinding.perfMonthIncomeTeamSum.text = it.tmAchievementStatistics.teamApplyCount.toString()
                mBinding.perfMonthCashOut.text = it.tmAchievementStatistics.mySettleCount.toString()
                mBinding.perfMonthTeamCashOut.text = it.tmAchievementStatistics.teamSettleCount.toString()

            },{

            })
            if (mViewModel.isRefresh) {
                mBinding.perfRefreshLayout.finishRefresh()
                mViewModel.isRefresh = false
            }
            WaitDialog.dismiss()
        }

        mViewModel.subsidyRuleResult.observe(this) { it ->
            parseState(it, {
                mBinding.perfSubsidyRule.setOnClickListener(object :NoMoreClickListener(){
                    override fun OnMoreClick(view: View) {
                        if (it?.url0.isNullOrEmpty()){
                            val intent = Intent(context, WebViewActivity::class.java)
                            val sb = StringBuilder()
                            sb.append(it?.url0)
                            if (it?.url0!!.contains("?")) {
                                sb.append("&token=")
                            } else {
                                sb.append("?token=")
                            }
                            sb.append(SMApplication.instance.token)
                            intent.putExtra("url", sb.toString())
                            intent.putExtra("from", "我的")
                            startActivity(intent)
                        }else {
                            ToastUtils.show("链接不存在！")
                        }
                    }

                    override fun OnMoreErrorClick() {
                    }

                })

            }, {

            })
        }

    }

    private fun reqData(){
        mViewModel.getStatisticsData()
        mViewModel.getSubsidyRule()

    }
    override fun initRequestData() {
    }

    override fun FragmentPerformanceBinding.initView() {
        if (activity?.javaClass?.equals(MainActivity::class.java) == true){
            mBinding.fragTitleRootLay.visibility = View.VISIBLE
        }else{
            mBinding.fragTitleRootLay.visibility = View.GONE
        }
//        mBinding.perfRanksRecycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

//        val i: Int = DisplayUtils.dip2px(requireContext(), 22.5f)
//        val rect = Rect(0, 0, i, 0)
//
//        val j: Int = DisplayUtils.dip2px(requireContext(), 15f)
//        val firstAndLastRect = Rect(j, 0, i, 0)
//        val spacesDecoration = HorizontalSpacesDecoration(rect, firstAndLastRect)
//        mBinding.perfRanksRecycler.addItemDecoration(spacesDecoration)

        mBinding.perfRefreshLayout.setEnableAutoLoadMore(false)
        mBinding.perfRefreshLayout.setEnableLoadMore(false)
        mBinding.perfRefreshLayout.setEnableLoadMoreWhenContentNotFull(false) //是否在列表不满一页时候开启上拉加载功能
        mBinding.perfRefreshLayout.setEnableOverScrollBounce(true) //是否启用越界回弹
        mBinding.perfRefreshLayout.setEnableOverScrollDrag(true)
        mBinding.perfRefreshLayout.setOnRefreshListener {
            if (mViewModel.isRefresh) return@setOnRefreshListener
            mViewModel.isRefresh = true
            reqData()
        }

    }

}