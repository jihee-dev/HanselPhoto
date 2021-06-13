package com.android.study.hanselandphotograph.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.study.hanselandphotograph.R
import com.android.study.hanselandphotograph.adapter.PicListAdapter
import com.android.study.hanselandphotograph.databinding.ActivityCommentStoryBinding
import com.android.study.hanselandphotograph.model.Location
import com.android.study.hanselandphotograph.model.Picture
import com.android.study.hanselandphotograph.model.Story
import java.time.LocalDate

class CommentStoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityCommentStoryBinding
    private val FINISH_INTERVAL_TIME: Long = 2000
    private var backPressedTime: Long = 0
    lateinit var adapter: PicListAdapter
    var pictureList = ArrayList<Picture>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRecyclerView()
        initToolbar()
        init()
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = PicListAdapter(pictureList)
        adapter.itemClickListener = object : PicListAdapter.OnItemClickListener {
            override fun onItemClick(
                holder: PicListAdapter.ViewHolder,
                view: View,
                data: Picture,
                position: Int
            ) {
                val intent = Intent(this@CommentStoryActivity, EditImageActivity::class.java)
                intent.putExtra("picture", data)
                startActivity(intent)
            }
        }

        binding.recyclerView.adapter = adapter
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
                onBackPressed()
            }

            R.id.action_save -> {
                Toast.makeText(applicationContext, "스토리 저장", Toast.LENGTH_SHORT).show()

                val intent = Intent(this@CommentStoryActivity, ShowStoryActivity::class.java)
                // put sample data
                val sampleLocation = Location(37.54094112888107, 127.07934279796626)
                val sampleLocation2 = Location(37.54115049852869, 127.07834949860488)
                val sampleLocation3 = Location(37.541844388275294, 127.07859600645149)
                val trackList =
                    arrayListOf<Location>(sampleLocation, sampleLocation2, sampleLocation3)
                val picList = arrayListOf<Location>(sampleLocation2)

                val sampleStory = Story(
                    0,
                    LocalDate.of(2021, 1, 1),
                    "Sample Name",
                    "Sample Comment",
                    trackList as ArrayList<Location>,
                    picList as ArrayList<Location>
                )
                intent.putExtra("story", sampleStory)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val tempTime = System.currentTimeMillis()
        val intervalTime: Long = tempTime - backPressedTime

        if (intervalTime in 0..FINISH_INTERVAL_TIME) {
            val intent = Intent(this@CommentStoryActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        } else {
            backPressedTime = tempTime
            Toast.makeText(applicationContext, "한번 더 누르면 작성한 기록이 삭제됩니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        val picture = intent.getSerializableExtra("picture") as Picture

        for (p in pictureList) {
            if (p.id == picture.id) {
                p.title = picture.title
                // p.comment = picture.comment
            }
        }
    }
}