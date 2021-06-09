package com.android.study.hanselandphotograph.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.android.study.hanselandphotograph.R
import com.android.study.hanselandphotograph.databinding.ActivityRecordingStoryBinding

class RecordingStoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityRecordingStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordingStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        init()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.title = "스토리 기록 중"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
    }

    private fun init() {
        binding.apply {
            finishRecordBtn.setOnClickListener {
                val intent = Intent(this@RecordingStoryActivity, CommentStoryActivity::class.java)
                startActivity(intent)
            }

            addImageBtn.setOnClickListener {
                // select image from gallery or camera
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}