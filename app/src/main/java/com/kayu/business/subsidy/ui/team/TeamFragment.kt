package com.kayu.business.subsidy.ui.team

import androidx.fragment.app.viewModels
import com.hoom.library.common.ui.BaseFragment
import com.kayu.business.subsidy.databinding.FragmentTeamBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeamFragment : BaseFragment<FragmentTeamBinding, TeamViewModel>()  {
    override val mViewModel: TeamViewModel by viewModels()

    override fun showLoading(message: String) {
    }

    override fun dismissLoading() {
    }

    override fun initObserve() {
    }

    override fun initRequestData() {
    }

    override fun FragmentTeamBinding.initView() {
    }


}