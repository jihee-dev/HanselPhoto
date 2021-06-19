package com.android.study.hanselandphotograph.ui

import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.study.hanselandphotograph.DBHelper.MyDBHelper
import com.android.study.hanselandphotograph.R
import com.android.study.hanselandphotograph.model.ARData
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.core.exceptions.FatalException
import com.google.ar.core.exceptions.UnavailableException
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ViewRenderable
import uk.co.appoly.arcorelocation.LocationMarker
import uk.co.appoly.arcorelocation.LocationScene
import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper
import java.io.FileOutputStream
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.collections.ArrayList
import kotlin.concurrent.timer

class ArActivity : AppCompatActivity() {
    private lateinit var arSceneView: ArSceneView
    private var locationScene: LocationScene? = null

    val resumeAR = Runnable {
        locationScene?.resume()
        arSceneView.resume()
    }
    val arHandler = Handler(Looper.getMainLooper())

    lateinit var myDBHelper: MyDBHelper
    var installRequested = false
    var hasFinishedLoading = false
    var data = ArrayList<ARData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_ar)

        if (!checkLocationServicesStatus()) {
            showLocationServicesSetting()
        }
        initDB()

        arSceneView = findViewById(R.id.arSceneView)

        data.forEach {
            val exampleLayout = ViewRenderable.builder()
                .setView(this, R.layout.renderable)
                .build()

            lateinit var exampleLayoutRenderable: ViewRenderable
            val base = Node()
            CompletableFuture.anyOf(exampleLayout)
                .handle<Any> { _, throwable ->
                    if (throwable != null) {
                        return@handle null
                    }
                    try {
                        exampleLayoutRenderable = exampleLayout.get()
                        exampleLayout.get().view.findViewById<TextView>(R.id.arTitle).text = it.title

                        base.renderable = exampleLayoutRenderable
                        base.setOnTapListener { hitTestResult, motionEvent ->
                            val arFragment = ArFragment()
                            var args = Bundle()
                            args.putString("title", it.title)
                            args.putString("path", it.path)
                            arFragment.arguments = args
                            arFragment.show(this.supportFragmentManager, "arFrag")
                        }

                        var locationMarker = LocationMarker(it.lng, it.lat, base)
                        arHandler.postDelayed({
                            resumeAR.run {
                                locationMarker.scalingMode = LocationMarker.ScalingMode.FIXED_SIZE_ON_SCREEN

                                locationScene?.mLocationMarkers?.add(locationMarker)
                                locationMarker.anchorNode?.isEnabled = true

                                arHandler.post {
                                    locationScene?.refreshAnchors()
                                }
                            }

                            locationMarker.setRenderEvent {
                                resumeAR.run {
//                                    Toast.makeText(this@ArActivity, it.distance.toString(), Toast.LENGTH_SHORT).show()
                                    locationMarker.anchorNode?.isEnabled = it.distance <= 30
                                }
                            }

                            if (data.indexOf(it) == data.size - 1) {
                                hasFinishedLoading = true
                            }
                        }, 100)
                    } catch (ex: Exception) {
                        Toast.makeText(this, "error: $ex", Toast.LENGTH_SHORT).show()
                    }
                    null
                }
        }

        arSceneView
            .scene
            .addOnUpdateListener {
                if (!hasFinishedLoading) {
                    return@addOnUpdateListener
                }

                val frame = arSceneView.arFrame ?: return@addOnUpdateListener

                if (frame.camera.trackingState != TrackingState.TRACKING)
                    return@addOnUpdateListener

                if (locationScene != null)
                    locationScene!!.processFrame(frame)
            }

        ARLocationPermissionHelper.requestPermission(this)
    }

    private fun initDB() {
//        val dbfile = getDatabasePath("db1.db")
//        if(!dbfile.parentFile.exists()) {
//            dbfile.parentFile.mkdir()
//        }
//        if(!dbfile.exists()) {
//            val file = resources.openRawResource(R.raw.db1)
//            val fileSize = file.available()
//            val buffer = ByteArray(fileSize)
//            file.read(buffer)
//            file.close()
//            dbfile.createNewFile()
//            val output = FileOutputStream(dbfile)
//            output.write(buffer)
//            output.close()
//        }

        myDBHelper = MyDBHelper(this)
        myDBHelper.arSearch()
    }

    override fun onResume() {
        super.onResume()
        if (locationScene != null) {
            locationScene!!.resume()
        }

        if (arSceneView.session == null) {
            try {
                val session = createArSession(this, installRequested)
                if (session == null) {
                    installRequested = ARLocationPermissionHelper.hasPermission(this)
                    return
                } else {
                    arSceneView.setupSession(session)
                }
            } catch (e: UnavailableException) {
//                Toast.makeText(this, "error: $e", Toast.LENGTH_SHORT).show()
            } catch (ex: FatalException) {
//                Toast.makeText(this, "error: $ex", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        if (locationScene == null) {
            locationScene = LocationScene(this, arSceneView)
        }

        try {
            arSceneView.resume()
        } catch (ex: CameraNotAvailableException) {
//            Toast.makeText(this, "error: $ex", Toast.LENGTH_SHORT).show()
            finish()
        } catch (ex: FatalException) {
//            Toast.makeText(this, "error: $ex", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        if (locationScene != null) {
            locationScene!!.pause()
        }

        arSceneView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        arSceneView.destroy()
    }

    private fun showLocationServicesSetting() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage("AR을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 허용하겠습니까?")
        builder.setPositiveButton("설정", DialogInterface.OnClickListener { dialog, id ->
            val GpsSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(GpsSettingIntent, 1000)
        })
        builder.create().show()
    }

    private fun checkLocationServicesStatus(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!ARLocationPermissionHelper.hasPermission(this)) {
            if (!ARLocationPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                ARLocationPermissionHelper.launchPermissionSettings(this)
            } else {
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }

    private fun createArSession(activity: Activity, installRequested: Boolean): Session? {
        if (ARLocationPermissionHelper.hasPermission(activity)) {
            when (ArCoreApk.getInstance().requestInstall(activity, installRequested)) {
                ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                    Toast.makeText(activity, "ArCore 설치 필요", Toast.LENGTH_SHORT).show()
                    return null
                }
            }

            val session = Session(activity)
            val config = Config(session)

            config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
            session.configure(config)

            return session
        }
        return null
    }
}