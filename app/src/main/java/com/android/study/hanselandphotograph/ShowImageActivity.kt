package com.android.study.hanselandphotograph

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.study.hanselandphotograph.databinding.ActivityShowImageBinding
import com.android.study.hanselandphotograph.databinding.ActivityShowStoryBinding

class ShowImageActivity : AppCompatActivity() {
    lateinit var binding: ActivityShowImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        val intent = intent
        val path = intent.getStringExtra("path")
        Toast.makeText(this, path, Toast.LENGTH_LONG).show()
    }
}