package com.kongsub.dailyq.ui.base

import androidx.fragment.app.Fragment
import com.kongsub.dailyq.api.ApiService

abstract class BaseFragment : Fragment() {
    val api: ApiService by lazy { ApiService.getInstance() }

}