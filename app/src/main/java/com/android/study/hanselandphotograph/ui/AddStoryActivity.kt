package com.android.study.hanselandphotograph.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.study.hanselandphotograph.R
import com.android.study.hanselandphotograph.databinding.ActivityAddStoryBinding

class AddStoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddStoryBinding
    private val FINISH_INTERVAL_TIME: Long = 2000
    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        init()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.title = "스토리 추가"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
    }

    private fun init() {
        binding.apply {
            startRecordBtn.setOnClickListener {
                val intent = Intent(this@AddStoryActivity, RecordingStoryActivity::class.java)
                intent.putExtra("title", binding.storyTitleEdit.text.toString())
                startActivity(intent)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val tempTime = System.currentTimeMillis()
        val intervalTime: Long = tempTime - backPressedTime

        if (intervalTime in 0..FINISH_INTERVAL_TIME) {
            val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        } else {
            backPressedTime = tempTime
            Toast.makeText(applicationContext, "한번 더 누르면 작성한 기록이 삭제됩니다.", Toast.LENGTH_SHORT).show()
        }
    }
}