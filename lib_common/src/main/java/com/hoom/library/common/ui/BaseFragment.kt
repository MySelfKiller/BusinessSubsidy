package com.hoom.library.common.ui

import androidx.viewbinding.ViewBinding
import com.hoom.library.base.mvvm.BaseFrameFragment
import com.hoom.library.base.mvvm.BaseViewModel

/**
 * Fragment基类
 *
 */
abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel> : BaseFrameFragment<VB, VM>()