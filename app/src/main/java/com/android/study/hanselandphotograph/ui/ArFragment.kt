package com.android.study.hanselandphotograph.ui

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.android.study.hanselandphotograph.R
import java.io.File

class ArFragment : DialogFragment() {
    var picTitle = ""
    var picPath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            picTitle = requireArguments().getString("title").toString()
            picPath = requireArguments().getString("path").toString()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val dialogView = inflater.inflate(R.layout.fragment_ar, null)

            dialogView.findViewById<TextView>(R.id.arFragText).text = picTitle

            val imgFile = File(picPath)
            if (imgFile.exists()) {
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                val myImage = dialogView.findViewById<ImageView>(R.id.arFragImg)
                myImage.setImageBitmap(myBitmap)
            }

            builder.setView(dialogView)
            builder.create()
        }  ?: throw IllegalStateException("Activity cannot be null")
    }
}