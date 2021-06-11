package com.android.study.hanselandphotograph.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.android.study.hanselandphotograph.R
import com.android.study.hanselandphotograph.databinding.ActivityRecordingStoryBinding
import java.io.File

class RecordingStoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityRecordingStoryBinding
    val PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    val PERMISSIONS_REQUEST = 100
    private val BUTTON = 100
    private var photoUri: Uri? = null
    lateinit var filepath:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordingStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        init()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.title = "스토리 기록 중"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
    }

    private fun init() {
        binding.apply {
            finishRecordBtn.setOnClickListener {
                val intent = Intent(this@RecordingStoryActivity, CommentStoryActivity::class.java)
                startActivity(intent)
            }

            addImageBtn.setOnClickListener {
                // select image from gallery or camera
                checkPermissions(PERMISSIONS, PERMISSIONS_REQUEST)
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val photoFile = File(
                    File("${filesDir}/image").apply{
                        if(!this.exists()){
                            this.mkdirs()
                        }
                    },
                    newJpgFileName()
                )
                photoUri = FileProvider.getUriForFile(
                    this@RecordingStoryActivity,
                    "com.android.study.hanselandphotograph.fileprovider",
                    photoFile
                )
                takePictureIntent.resolveActivity(packageManager)?.also{
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(takePictureIntent, BUTTON)
                }
            }
        }
    }

    private fun checkPermissions(permissions: Array<String>, permissionsRequest: Int): Boolean {
        val permissionList : MutableList<String> = mutableListOf()
        for(permission in permissions){
            val result = ContextCompat.checkSelfPermission(this, permission)
            if(result != PackageManager.PERMISSION_GRANTED){
                permissionList.add(permission)
            }
        }
        if(permissionList.isNotEmpty()){
            ActivityCompat.requestPermissions(this, permissionList.toTypedArray(), permissionsRequest)
            return false
        }
        return true
    }

    private fun newJpgFileName() : String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = sdf.format(System.currentTimeMillis())
        filepath = "data/data/com.android.study.hanselandphotograph/files/image/"+"${filename}.jpg"
        return "${filename}.jpg"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}