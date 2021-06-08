package com.android.study.hanselandphotograph.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.study.hanselandphotograph.databinding.ActivityAddStoryBinding

class AddStoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}