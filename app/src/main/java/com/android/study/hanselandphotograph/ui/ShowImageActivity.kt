package com.android.study.hanselandphotograph.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.viewpager.widget.ViewPager
import com.android.study.hanselandphotograph.R
import com.android.study.hanselandphotograph.adapter.ImagePageAdapter
import com.android.study.hanselandphotograph.databinding.ActivityShowImageBinding
import com.android.study.hanselandphotograph.model.Picture
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class ShowImageActivity : AppCompatActivity() {
    lateinit var binding: ActivityShowImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        init()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.title = "사진 둘러보기"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
    }

    private fun init() {
        val intent = intent
        val title = intent.getStringArrayListExtra("title") as ArrayList<String>
        val path = intent.getStringArrayListExtra("path") as ArrayList<String>

        val picArr = arrayListOf<Picture>()
        for (i in 0 until title.size) {
            picArr.add(Picture(0, title[i], path[i], 0.0, 0.0))
        }

        val springDotsIndicator = findViewById<SpringDotsIndicator>(R.id.indicator)
        val adapter = ImagePageAdapter(picArr)
        binding.viewPager.adapter = adapter
        springDotsIndicator.setViewPager(binding.viewPager)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}