package com.kongsub.dailyq.ui.main

import android.os.Bundle
import com.kongsub.dailyq.AuthManager
import com.kongsub.dailyq.R
import com.kongsub.dailyq.databinding.ActivityMainBinding
import com.kongsub.dailyq.ui.base.BaseActivity
import com.kongsub.dailyq.ui.profile.ProfileFragment
import com.kongsub.dailyq.ui.timeline.TimelineFragment
import com.kongsub.dailyq.ui.today.TodayFragment

class MainActivity : BaseActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // navigation 변경
        binding.navView.setOnItemSelectedListener {
            val ft = supportFragmentManager.beginTransaction()

            when (it.itemId) {
                R.id.timeline -> {
                    ft.replace(R.id.host, TimelineFragment())
                    supportActionBar?.setTitle(R.string.title_timeline)
                }
                R.id.today -> {
                    ft.replace(R.id.host, TodayFragment())
                    supportActionBar?.setTitle(R.string.title_today)
                }
                R.id.profile -> {
                    ft.replace(R.id.host, ProfileFragment().apply {
                        arguments = Bundle().apply {
                            putString(ProfileFragment.ARG_UID, AuthManager.uid)
                        }
                    })
                }
            }
            ft.commit()
            true
        }

        binding.navView.selectedItemId = R.id.today
    }
}