package com.kongsub.dailyq.ui.base

import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.kongsub.dailyq.api.ApiService

abstract class BaseActivity : AppCompatActivity() {
    val api: ApiService by lazy { ApiService.getInstance() }
    // Appbar 의 뒤로가기 버튼 클릭시, 액티비티 종료
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}