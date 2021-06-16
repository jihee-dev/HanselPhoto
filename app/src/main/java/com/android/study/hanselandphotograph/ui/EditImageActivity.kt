package com.android.study.hanselandphotograph.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.android.study.hanselandphotograph.R
import com.android.study.hanselandphotograph.databinding.ActivityEditImageBinding
import com.android.study.hanselandphotograph.model.Picture

class EditImageActivity : AppCompatActivity() {
    lateinit var binding: ActivityEditImageBinding
    var backFlag = 0
    lateinit var picture: Picture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        init()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.title = "사진 정보 수정"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
    }

    private fun init() {
        val intent = intent
        val path = intent.getStringExtra("path")
        Toast.makeText(this, path, Toast.LENGTH_LONG).show()
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
                Toast.makeText(applicationContext, "이미지 저장", Toast.LENGTH_SHORT).show()

                // default Recording Activity, flag == 1
                var intent = Intent(this@EditImageActivity, RecordingStoryActivity::class.java)

                if (backFlag == 2) { // Comment Activity
                    intent = Intent(this@EditImageActivity, CommentStoryActivity::class.java)
                } else if (backFlag != 1) {
                    Toast.makeText(this, "Change Activity Error", Toast.LENGTH_SHORT).show()
                }

                // put sample data
                picture.title = binding.picTitleEdit.text.toString()
                // picture.comment = binding.picCommentEdit.text.toString()

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra("picture", picture)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        picture = intent.getSerializableExtra("picture") as Picture
        binding.imageView.setImageURI(Uri.parse(picture.path))
        binding.picTitleEdit.setText(picture.title)
    }
}