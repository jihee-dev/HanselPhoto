package com.android.study.hanselandphotograph.ui

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.android.study.hanselandphotograph.R
import com.android.study.hanselandphotograph.model.ARData
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.TrackingState
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.core.exceptions.UnavailableException
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import uk.co.appoly.arcorelocation.LocationMarker
import uk.co.appoly.arcorelocation.LocationScene
import uk.co.appoly.arcorelocation.utils.ARLocationPermissionHelper
import java.util.concurrent.CompletableFuture

class ArActivity : AppCompatActivity() {
    private lateinit var arSceneView: ArSceneView
    private var locationScene: LocationScene? = null
    //    lateinit var myDBHelper: MyDBHelper
    var installRequested = false
    var hasFinishedLoading = false
//    var data: ArrayList<ARData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)

        arSceneView = findViewById(R.id.arSceneView)

        initDB()

        val base = Node()
        base.setOnTapListener { hitTestResult, motionEvent ->
            val arFragment = com.android.study.hanselandphotograph.ui.ArFragment()
            var args = Bundle()
            args.putString("title", "test")
            args.putString("path", "")
            arFragment.arguments = args
            arFragment.show(this.supportFragmentManager, "arFrag")
        }

        val exampleLayout = ViewRenderable.builder()
            .setView(this, R.layout.renderable)
            .build()

        lateinit var exampleLayoutRenderable: ViewRenderable
        CompletableFuture.allOf(exampleLayout)
            .handle<Any> { _, throwable ->
                if (throwable != null) {
                    return@handle null
                }
                try {
                    exampleLayoutRenderable = exampleLayout.get()
                    hasFinishedLoading = true
                    base.renderable = exampleLayoutRenderable
                } catch (ex: Exception) {
                    Toast.makeText(this, "error: $ex", Toast.LENGTH_SHORT).show()
                }
                null
            }

        arSceneView
            .scene
            .addOnUpdateListener {
                if (!hasFinishedLoading) {
                    return@addOnUpdateListener
                }
                if (locationScene == null) {
                    locationScene = LocationScene(this, arSceneView)


                    var layoutLocationMarker = LocationMarker(37.541636, 127.077324, base)
                    layoutLocationMarker.setRenderEvent { locationNode ->
                        exampleLayoutRenderable.view.findViewById<TextView>(R.id.arTitle).text = "test"
                    }
                    locationScene?.mLocationMarkers?.add(layoutLocationMarker)
                    var lmarker2 = LocationMarker(37.542206, 127.077345, base)
                    locationScene?.mLocationMarkers?.add(lmarker2)
                    var lmarker3 = LocationMarker(37.540088, 127.076522, base)
                    locationScene?.mLocationMarkers?.add(lmarker3)
                    var lmarker4 = LocationMarker(37.542281, 127.077072, base)
                    locationScene?.mLocationMarkers?.add(lmarker4)
                    var lmarker5 = LocationMarker(37.542592, 127.077316, base)
                    locationScene?.mLocationMarkers?.add(lmarker5)
                    layoutLocationMarker.anchorNode?.isEnabled = true

                    locationScene?.refreshAnchors()
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
                Toast.makeText(this, "error: $e", Toast.LENGTH_SHORT).show()
            }
        }

        try {
            arSceneView.resume()
        } catch (ex: CameraNotAvailableException) {
            Toast.makeText(this, "error: $ex", Toast.LENGTH_SHORT).show()
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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