package com.kongsub.dailyq.ui.image

import android.os.Bundle
import coil.load
import com.kongsub.dailyq.R
import com.kongsub.dailyq.databinding.ActivityImageViewerBinding
import com.kongsub.dailyq.ui.base.BaseActivity

class ImageViewerActivity : BaseActivity() {
    companion object {
        const val EXTRA_URL = "url"
    }

    lateinit var binding: ActivityImageViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val url = intent.getStringExtra(EXTRA_URL)
        binding.image.load(url)
    }
}