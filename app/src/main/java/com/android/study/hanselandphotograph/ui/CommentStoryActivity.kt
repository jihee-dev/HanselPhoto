package com.android.study.hanselandphotograph.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.study.hanselandphotograph.DBHelper.MyDBHelper
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
    lateinit var locationList: ArrayList<Location>
    lateinit var story_title: String
    lateinit var myDBHelper: MyDBHelper
    private val EDIT_IMAGE_REQUEST = 7777

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getIntentData()
        initRecyclerView()
        initToolbar()
        init()
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                this@CommentStoryActivity,
                LinearLayoutManager.VERTICAL
            )
        )

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
                startActivityForResult(intent, EDIT_IMAGE_REQUEST)
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

    private fun getIntentData() {
        // if (intent.hasExtra("location_list"))
        locationList = intent.getSerializableExtra("location_list") as ArrayList<Location>
        pictureList = intent.getSerializableExtra("picture_list") as ArrayList<Picture>
        story_title = intent.getStringExtra("title").toString()

        binding.storyTitleEdit.setText(story_title)
    }

    private fun init() {
        myDBHelper = MyDBHelper(this)
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

                /*val sampleStory = Story(
                    0,
                    LocalDate.of(2021, 1, 1),
                    "Sample Name",
                    "Sample Comment",
                    trackList as ArrayList<Location>,
                    picList as ArrayList<Location>
                )
                intent.putExtra("story", sampleStory)*/

                val picLocList = ArrayList<Location>()

                for (p in pictureList) {
                    picLocList.add(Location(p.lat, p.long))
                }

                var story = Story(
                    0,
                    LocalDate.now().toString(),
                    binding.storyTitleEdit.text.toString(),
                    binding.storyCommentEdit.text.toString()
                )

                myDBHelper.insertStory(story)

                for (i in 0 until locationList.size) {
                    /*Log.i("RecordingStroyActivity: InsertLocation - ", "i: " + i + "location: " + locationList[i].toString())
                    Log.i("RecordingStroyActivity: InsertLocation - ", "is Location Type?: " + (locationList[i] is Location).toString())*/
                    myDBHelper.insertLocation(locationList[i])
                }

                for (i in 0 until pictureList.size) {
                    myDBHelper.insertPicture(pictureList[i])
                }

                intent.putExtra("story", myDBHelper.getStory(myDBHelper.getStoryID()))
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            EDIT_IMAGE_REQUEST -> {
                if ((resultCode == RESULT_OK) && (data != null)) {
                    val picture = data.getSerializableExtra("picture") as Picture
                    for (p in pictureList) {
                        if (p.id == picture.id) {
                            p.title = picture.title
                            // p.comment = picture.comment
                        }
                    }
                    adapter.notifyDataSetChanged()
                    // story_title = data.getStringExtra("title").toString()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (intent.hasExtra("picture")) {
            val picture = intent.getSerializableExtra("picture") as Picture

            for (p in pictureList) {
                if (p.id == picture.id) {
                    p.title = picture.title
                    // p.comment = picture.comment
                }
            }
        }
    }
}