package com.kongsub.dailyq.ui.base

import androidx.fragment.app.Fragment
import com.kongsub.dailyq.api.ApiService
import com.kongsub.dailyq.db.AppDatabase

abstract class BaseFragment : Fragment() {
    val api: ApiService by lazy { ApiService.getInstance() }
    val db: AppDatabase by lazy { AppDatabase.getInstance() }
}