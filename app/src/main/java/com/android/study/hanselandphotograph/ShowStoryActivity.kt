package com.android.study.hanselandphotograph

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.study.hanselandphotograph.databinding.ActivityShowStoryBinding

class ShowStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        val intent = intent
        val story = intent.getSerializableExtra("story") as Story
    }
}