package com.android.study.hanselandphotograph.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.study.hanselandphotograph.R
import com.android.study.hanselandphotograph.databinding.ActivityMainBinding
import com.android.study.hanselandphotograph.model.Location
import com.android.study.hanselandphotograph.model.Story
import java.time.LocalDate
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: ArrayList<Story>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        initDB()
        init()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.title = "내 스토리"

        /*supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)*/
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            R.id.action_search -> {
                Toast.makeText(applicationContext, "검색 버튼 클릭", Toast.LENGTH_SHORT).show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initDB() {
        // 스토리 이름(id), 스토리 한줄평, 이동한 좌표, 사진 찍은 좌표
        val date = LocalDate.now()
        val comment = "첫번째 스토리"
        val route = arrayListOf(
            Location(37.557,126.973),
            Location(37.558,126.972),
            Location(37.559,126.971),
            Location(37.560,126.970),
            Location(37.559,126.969),
            Location(37.558,126.968)
        )
        val picture = arrayListOf(Location(37.557,126.973), Location(37.560,126.970))
        db = arrayListOf(Story(0, date, "firstStory", comment, route, picture))
    }

    private fun init() {
        binding.apply {
            mainInsertBtn.setOnClickListener {
                // new story
                val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
                startActivity(intent)
                // initDB() // 새로운 스토리 생성 후 데베 배열 다시 초기화
            }
            mainRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            mainRecyclerView.addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL))
            val adapter = StoryAdapter(db)
            adapter.onStoryClickListener = object: StoryAdapter.OnStoryClickListener {
                override fun onStoryClick(holder: StoryAdapter.ViewHolder, story: Story) {
                    // show story
                    val intent = Intent(this@MainActivity, ShowStoryActivity::class.java)
                    intent.putExtra("story", story)
                    startActivity(intent)
                }

            }
            mainRecyclerView.adapter = adapter
        }
    }
}