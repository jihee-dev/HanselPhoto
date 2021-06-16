package com.android.study.hanselandphotograph.ui

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.study.hanselandphotograph.DBHelper.MyDBHelper
import com.android.study.hanselandphotograph.R
import com.android.study.hanselandphotograph.adapter.PicListAdapter
import com.android.study.hanselandphotograph.databinding.ActivityRecordingStoryBinding
import com.android.study.hanselandphotograph.model.Location
import com.android.study.hanselandphotograph.model.Picture
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.io.File
import kotlin.math.*

class RecordingStoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityRecordingStoryBinding
    lateinit var adapter: PicListAdapter
    private val FINISH_INTERVAL_TIME: Long = 2000
    private var backPressedTime: Long = 0

    lateinit var googleMap: GoogleMap
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
    lateinit var myDBHelper: MyDBHelper
    var startUpdate = false
    var isFirstGPS = true
    var loc = LatLng(37.554752, 126.970631)
    var lastLoc = LatLng(0.0, 0.0)
    var locationList = ArrayList<Location>()
    var locationList2 = ArrayList<LatLng>()
    var pictureList = ArrayList<Picture>()
    var picNum = 0

    val PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    val PERMISSIONS_REQUEST = 100
    private val BUTTON = 100
    private var photoUri: Uri? = null
    lateinit var filepath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordingStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        initRecyclerView()
        initMap()
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
                val intent = Intent(this@RecordingStoryActivity, EditImageActivity::class.java)
                intent.putExtra("picture", data)
                startActivity(intent)
            }
        }

        binding.recyclerView.adapter = adapter
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.title = "스토리 기록 중"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
    }

    private fun initMap() {
        initLocation()

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            googleMap = it
        }
    }

    private fun initLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(location: LocationResult) {
                if (location.locations.size == 0) return
                loc = LatLng(
                    location.locations[location.locations.size - 1].latitude,
                    location.locations[location.locations.size - 1].longitude
                )
                setCurrentLocation(loc)
                Log.i("location", "LocationCallback()")
            }
        }
    }

    fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                ), 100
            )
        } else {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                if (it != null) {
                    loc = LatLng(it.latitude, it.longitude)
                    setCurrentLocation(loc)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
                // getLastLocation()
            } else {
                Toast.makeText(this, "위치 정보가 필요합니다.", Toast.LENGTH_SHORT).show()
                setCurrentLocation(loc)
            }
        }
    }

    fun setCurrentLocation(location: LatLng) {
        Log.i(
            "Current Location: ",
            "(" + location.latitude.toString() + ", " + location.longitude.toString() + ")"
        )
        if (isFirstGPS || updateLoc(lastLoc, location)) {
            isFirstGPS = false
            lastLoc = location
            locationList.add(Location(lastLoc.latitude, lastLoc.longitude))
            locationList2.add(lastLoc)
            Log.i(
                "Update Location List",
                "(" + lastLoc.latitude.toString() + ", " + lastLoc.longitude.toString() + ")"
            )

            val option = MarkerOptions()
            option.position(lastLoc)
            option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            googleMap.clear()
            googleMap.addMarker(option)

            val option2 = PolylineOptions().color(Color.GREEN).addAll(locationList2)
            googleMap.addPolyline(option2)

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLoc, 16.0f))
        }
    }

    private fun updateLoc(loc1: LatLng, loc2: LatLng): Boolean {
        val R = 6372.8 * 1000
        val dLat = Math.toRadians(loc2.latitude - loc1.latitude)
        val dLon = Math.toRadians(loc2.longitude - loc1.longitude)
        val a =
            sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(Math.toRadians(loc1.latitude)) * cos(
                Math.toRadians(loc2.latitude)
            )
        val c = 2 * asin(sqrt(a))
        return ((R * c) > 5.0) // 5m
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                ), 100
            )
        } else {
            if (!checkLocationServicesStatus()) { // GPS가 켜져 있는지 확인
                showLocationServicesSetting()
            } else {
                startUpdate = true
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest, locationCallback, Looper.getMainLooper()
                )
                Log.i("loaction", "startLocationUpdate()")
            }
        }
    }

    private fun stopLocationUpdate() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        startUpdate = false
        Log.i("location", "stopLocationUpdate()")
    }

    private fun checkLocationServicesStatus(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))

    }

    private fun showLocationServicesSetting() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 허용하겠습니까?")
        builder.setPositiveButton("설정", DialogInterface.OnClickListener { dialog, id ->
            val gpsSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(gpsSettingIntent, 1000)
        })
        builder.setNegativeButton("취소", DialogInterface.OnClickListener { dialog, id ->
            dialog.dismiss()
            setCurrentLocation(loc)
        })
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1000 -> {
                if (checkLocationServicesStatus()) {
                    Toast.makeText(this, "GPS가 활성화 되었음", Toast.LENGTH_SHORT).show()
                    startLocationUpdates()
                }
            }

            BUTTON -> {
                var picture = Picture(picNum, "", filepath, lastLoc.latitude, lastLoc.longitude)
                pictureList.add(picture)
                picNum += 1
            }
        }
    }

    private fun init() {
        myDBHelper = MyDBHelper(this)
        binding.apply {
            finishRecordBtn.setOnClickListener {
                val intent =
                    Intent(this@RecordingStoryActivity, CommentStoryActivity::class.java)
                for (i in 0..locationList.size){
                    myDBHelper.insertLocation(locationList[i])
                }
                startActivity(intent)
            }

            addImageBtn.setOnClickListener {
                // select image from gallery or camera
                checkPermissions(PERMISSIONS, PERMISSIONS_REQUEST)
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val photoFile = File(
                    File("${filesDir}/image").apply {
                        if (!this.exists()) {
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

                takePictureIntent.resolveActivity(packageManager)?.also {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(takePictureIntent, BUTTON)
                }
            }
        }
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

    private fun newJpgFileName(): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss")
        val filename = sdf.format(System.currentTimeMillis())
        filepath =
            "data/data/com.android.study.hanselandphotograph/files/image/" + "${filename}.jpg"
        return "${filename}.jpg"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val tempTime = System.currentTimeMillis()
        val intervalTime: Long = tempTime - backPressedTime

        if (intervalTime in 0..FINISH_INTERVAL_TIME) {
            val intent = Intent(this@RecordingStoryActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        } else {
            backPressedTime = tempTime
            Toast.makeText(applicationContext, "한번 더 누르면 작성한 기록이 삭제됩니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i("location", "onResume()")
        if (!startUpdate) {
            startLocationUpdates()
        }

        super.onResume()
        val picture = intent.getSerializableExtra("picture") as Picture

        for (p in pictureList) {
            if (p.id == picture.id) {
                p.title = picture.title
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.i("location", "onPause()")
        stopLocationUpdate()
    }

}