package com.android.study.hanselandphotograph.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.study.hanselandphotograph.DBHelper.MyDBHelper
import com.android.study.hanselandphotograph.R
import com.android.study.hanselandphotograph.adapter.StoryAdapter
import com.android.study.hanselandphotograph.databinding.ActivityMainBinding
import com.android.study.hanselandphotograph.model.Location
import com.android.study.hanselandphotograph.model.Picture
import com.android.study.hanselandphotograph.model.Story
import java.time.LocalDate
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: MyDBHelper

    /**************************************************************************************************/
    private lateinit var storyList: ArrayList<Story>
    var searchList = arrayListOf<Story>()

    /**************************************************************************************************/
    private lateinit var adapter: StoryAdapter

    //    private lateinit var db: ArrayList<Story>
    private val FINISH_INTERVAL_TIME: Long = 2000
    private var backPressedTime: Long = 0

    // camera permission
    val PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    val PERMISSIONS_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        initDB()
        init()
        // test
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

        val menuItem = menu?.findItem(R.id.action_search)

        menuItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                binding.mainInsertBtn.visibility = View.GONE
                binding.mainToArBtn.visibility = View.GONE
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                binding.mainInsertBtn.visibility = View.VISIBLE
                binding.mainToArBtn.visibility = View.VISIBLE
                adapter.storys = storyList
                adapter.notifyDataSetChanged()
                return true
            }

        })

        val searchView = menuItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                Log.i("text", p0!!)
                searchList = arrayListOf()
                for (story in storyList) {
                    if (story.name.contains(p0))
                        searchList.add(story)
                }
                adapter.storys = searchList
                adapter.notifyDataSetChanged()
                return true
            }

        })
        return true
    }

    private fun initDB() {
        // 스토리 이름(id), 스토리 한줄평, 이동한 좌표, 사진 찍은 좌표
        /**************************************************************************************************/
        dbHelper = MyDBHelper(this)
        storyList = dbHelper.getAllStory()
        /**************************************************************************************************/
    }

    private fun init() {
        binding.apply {
            mainInsertBtn.setOnClickListener {
                // new story
                val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
                startActivity(intent)
                // initDB() // 새로운 스토리 생성 후 데베 배열 다시 초기화
            }

            mainToArBtn.setOnClickListener {
                val intent = Intent(this@MainActivity, ArActivity::class.java)
                startActivity(intent)
            }
            mainRecyclerView.layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            mainRecyclerView.addItemDecoration(
                DividerItemDecoration(
                    this@MainActivity,
                    LinearLayoutManager.VERTICAL
                )
            )
            adapter = StoryAdapter(storyList)
            adapter.onStoryClickListener = object : StoryAdapter.OnStoryClickListener {
                override fun onStoryClick(holder: StoryAdapter.ViewHolder, story: Story) {
                    // show story
                    val intent = Intent(this@MainActivity, ShowStoryActivity::class.java)
                    intent.putExtra("story", story)
                    startActivity(intent)
                }

            }
            mainRecyclerView.adapter = adapter
        }

        checkPermissions(PERMISSIONS, PERMISSIONS_REQUEST)
    }

    private fun checkPermissions(permissions: Array<String>, permissionsRequest: Int): Boolean {
        val permissionList: MutableList<String> = mutableListOf()
        for (permission in permissions) {
            val result = ContextCompat.checkSelfPermission(this, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission)
            }
        }
        if (permissionList.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionList.toTypedArray(),
                permissionsRequest
            )
            return false
        }
        return true
    }

    override fun onBackPressed() {
        val tempTime = System.currentTimeMillis()
        val intervalTime: Long = tempTime - backPressedTime

        if (intervalTime in 0..FINISH_INTERVAL_TIME) {
            finish()
        } else {
            backPressedTime = tempTime
            Toast.makeText(applicationContext, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
    }
}