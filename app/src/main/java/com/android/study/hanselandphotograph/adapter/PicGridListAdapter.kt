package com.android.study.hanselandphotograph.adapter


import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.study.hanselandphotograph.R
import com.android.study.hanselandphotograph.model.Picture

class PicGridListAdapter(private val items: ArrayList<Picture>) : RecyclerView.Adapter<PicGridListAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val picImage: ImageView = itemView.findViewById(R.id.pic_list_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grid_picture, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val picUri = Uri.parse(items[position].path)
        holder.picImage.setImageURI(picUri)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
