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

class PicListAdapter(val items: ArrayList<Picture>) : RecyclerView.Adapter<PicListAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(holder: ViewHolder, view: View, data: Picture, position: Int)
    }

    var itemClickListener: OnItemClickListener? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val picImage: ImageView = itemView.findViewById(R.id.pic_list_img)
        val picTitle: TextView = itemView.findViewById(R.id.pic_list_title)

        init {
            itemView.setOnClickListener {
                itemClickListener?.onItemClick(this, it, items[adapterPosition], adapterPosition)
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_picture, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var picUri = Uri.parse(items[position].path)
        holder.picImage.setImageURI(picUri)
        holder.picTitle.text = items[position].title
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
