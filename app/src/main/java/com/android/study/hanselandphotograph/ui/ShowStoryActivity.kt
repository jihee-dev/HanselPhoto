package com.android.study.hanselandphotograph.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.study.hanselandphotograph.R
import com.android.study.hanselandphotograph.databinding.ActivityShowStoryBinding
import com.android.study.hanselandphotograph.model.Location
import com.android.study.hanselandphotograph.model.Story
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.time.LocalDate

class ShowStoryActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnPolylineClickListener {
    private lateinit var binding: ActivityShowStoryBinding
    private lateinit var name: String
    private lateinit var date: LocalDate
    private lateinit var comment: String
    private lateinit var route: ArrayList<Location>
    private lateinit var picture: ArrayList<Location>
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        initToolbar()
        init()
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
//        binding.toolbar.title = "스토리 입력"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
    }

    private fun init() {
        Log.i("story", "2")
        val intent = intent
        val story = intent.getSerializableExtra("story") as Story
        name = story.name
        date = story.date
        comment = story.comment
        route = story.route
        picture = story.picture

        binding.apply {
            binding.toolbar.title = name
            showDate.text = date.toString()
            showComment.text = comment
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.showStoryMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.i("story", "1")
        map = googleMap
        if (route.size != 0) {
            val polyLineOptions = PolylineOptions()
            polyLineOptions.color(0xffff0000.toInt())
            polyLineOptions.add()
            for (xy in route) {
                polyLineOptions.add(LatLng(xy.x, xy.y))
            }
            map.addPolyline(polyLineOptions)

            for (xy in picture) {
                val markerOptions = MarkerOptions()
                markerOptions.position(LatLng(xy.x, xy.y))

                val cameraIcon = BitmapFactory.decodeResource(resources, R.drawable.camera_icon)
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(cameraIcon, 120, 120, true)))
//                markerOptions.title("1")
//                markerOptions.snippet("1")
                map.addMarker(markerOptions)

                val middleXY = LatLng(route[route.size / 2].x, route[route.size / 2].y)

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(middleXY, 15f))

                map.setOnMarkerClickListener {
                    val intent = Intent(this, ShowImageActivity::class.java)
                    val path = "./image"
                    intent.putExtra("path", path)
//                    Toast.makeText(this, "marker click!", Toast.LENGTH_SHORT).show()
                    startActivity(intent)
                    false
                }

//                map.setOnInfoWindowClickListener {
//                    Toast.makeText(this, "info click!", Toast.LENGTH_SHORT).show()
//                }
            }
        }

    }

    override fun onPolylineClick(googleMap: Polyline) {
        // nothing
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}