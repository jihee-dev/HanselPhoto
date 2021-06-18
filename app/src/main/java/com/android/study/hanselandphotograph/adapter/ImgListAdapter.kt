package com.android.study.hanselandphotograph.adapter

import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.study.hanselandphotograph.databinding.RowShowImageBinding
import com.android.study.hanselandphotograph.model.Picture

class ImgListAdapter(private val imgs: ArrayList<Picture>): RecyclerView.Adapter<ImgListAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: RowShowImageBinding): RecyclerView.ViewHolder(binding.root) {
        init {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = RowShowImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = imgs.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.imageTitle.text = imgs[position].title
        val bitmap = BitmapFactory.decodeFile(imgs[position].path)
        holder.binding.imageView.setImageBitmap(bitmap)
    }
}