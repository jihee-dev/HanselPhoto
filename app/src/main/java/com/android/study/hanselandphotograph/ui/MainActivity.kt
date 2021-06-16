package com.android.study.hanselandphotograph.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var adapter:StoryAdapter
//    private lateinit var db: ArrayList<Story>
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

        menuItem?.setOnActionExpandListener(object :MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                binding.mainInsertBtn.visibility = View.GONE
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                binding.mainInsertBtn.visibility = View.VISIBLE
                adapter.storys = storyList
                adapter.notifyDataSetChanged()
                return true
            }

        })

        val searchView = menuItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
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

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when(item?.itemId) {
//            R.id.action_search -> {
//                Toast.makeText(applicationContext, "검색 버튼 클릭", Toast.LENGTH_SHORT).show()
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    private fun initDB() {
        // 스토리 이름(id), 스토리 한줄평, 이동한 좌표, 사진 찍은 좌표
        /**************************************************************************************************/
        dbHelper = MyDBHelper(this)

//        for (i in 0..10) {
//            dbHelper.insertStory(Story(-1, "2021-01-0$i", "test$i", "$i$i$i$i"))
//
//            dbHelper.insertLocation(Location(37.539487465926264, 127.0769945246811))
//            dbHelper.insertLocation(Location(37.53967249848109, 127.07706157990259))
//            dbHelper.insertLocation(Location(37.53983200893566, 127.07708840199119))
//            dbHelper.insertLocation(Location(37.540012787038336, 127.07716082163039))
//            dbHelper.insertLocation(Location(37.54021908622001, 127.07726810998476))
//            dbHelper.insertLocation(Location(37.540416877682944, 127.0773834449657))
//            dbHelper.insertLocation(Location(37.540567879414375, 127.0775095087821))
//            dbHelper.insertLocation(Location(37.54068591576632, 127.07758058726016))
//            dbHelper.insertLocation(Location(37.540851804377226, 127.07771469770314))
//            dbHelper.insertLocation(Location(37.54101556584907, 127.07788904127898))
//            dbHelper.insertLocation(Location(37.54106235478233, 127.0780848425643))
//            dbHelper.insertLocation(Location(37.54115380577998, 127.07836647449452))
//            dbHelper.insertLocation(Location(37.54115380577998, 127.07836647449452))
//            dbHelper.insertLocation(Location(37.541068735088125, 127.07851399598178))
//            dbHelper.insertLocation(Location(37.540919861143855, 127.0787151616462))
//            dbHelper.insertLocation(Location(37.54086031148291, 127.07889755184864))
//            dbHelper.insertLocation(Location(37.540807142102594, 127.07911212855738))
//            dbHelper.insertLocation(Location(37.54073270490644, 127.07935889177243))
//            dbHelper.insertLocation(Location(37.54072419779154, 127.07954396426716))
//            dbHelper.insertLocation(Location(37.540703993394615, 127.0801514845738))
//            dbHelper.insertLocation(Location(37.54070824695232, 127.08074425273168))
//            dbHelper.insertLocation(Location(37.54056575263709, 127.08064232879502))
//            dbHelper.insertLocation(Location(37.54045941341844, 127.08047603184576))
//            dbHelper.insertLocation(Location(37.54041262411417, 127.08037410790911))
//            dbHelper.insertLocation(Location(37.54029990430606, 127.08017294224467))
//            dbHelper.insertLocation(Location(37.540172296770486, 127.0799798232068))
//            dbHelper.insertLocation(Location(37.54001278704388, 127.07974378882719))
//            dbHelper.insertLocation(Location(37.539927715046524, 127.07962308941421))
//            dbHelper.insertLocation(Location(37.539757570767904, 127.07940314828775))
//            dbHelper.insertLocation(Location(37.53971503463568, 127.07919930036338))
//            dbHelper.insertLocation(Location(37.539689512945834, 127.07893912610405))
//            dbHelper.insertLocation(Location(37.5396533572036, 127.07859043895236))
//            dbHelper.insertLocation(Location(37.53959380653114, 127.07826320947153))
//            dbHelper.insertLocation(Location(37.53957253842228, 127.07804595055393))
//            dbHelper.insertLocation(Location(37.53956828479979, 127.07787965360464))
//            dbHelper.insertLocation(Location(37.539536382623325, 127.07759265725673))
//            dbHelper.insertLocation(Location(37.53949384636683, 127.07727079219362))
//
//            dbHelper.insertPicture(Picture(-1,"0","./0",37.54115380577998, 127.07836647449452))
//            dbHelper.insertPicture(Picture(-1,"1","./1",37.54070824695232, 127.08074425273168))
//            dbHelper.insertPicture(Picture(-1,"2","./2",37.539536382623325, 127.07759265725673))
//        }

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
            mainRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            mainRecyclerView.addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL))
            adapter = StoryAdapter(storyList)
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