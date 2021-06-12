package com.android.study.hanselandphotograph.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.study.hanselandphotograph.R
import com.android.study.hanselandphotograph.databinding.ActivityCommentStoryBinding
import com.android.study.hanselandphotograph.model.Location
import com.android.study.hanselandphotograph.model.Story
import java.time.LocalDate

class CommentStoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityCommentStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        init()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.title = "스토리 입력"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
    }

    private fun init() {
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this@CommentStoryActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }

            R.id.action_save -> {
                Toast.makeText(applicationContext, "스토리 저장", Toast.LENGTH_SHORT).show()

                val intent = Intent(this@CommentStoryActivity, ShowStoryActivity::class.java)
                // put sample data
                val sampleStory = Story(
                    0,
                    LocalDate.of(2021, 1, 1),
                    "Sample Name",
                    "Sample Comment",
                    ArrayList<Location>(),
                    ArrayList<Location>()
                )
                intent.putExtra("story", sampleStory)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}