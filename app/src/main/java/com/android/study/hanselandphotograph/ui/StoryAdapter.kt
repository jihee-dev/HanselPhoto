package com.android.study.hanselandphotograph.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.study.hanselandphotograph.databinding.RowStoryBinding
import com.android.study.hanselandphotograph.model.Story

class StoryAdapter(private val storys: ArrayList<Story>): RecyclerView.Adapter<StoryAdapter.ViewHolder>() {

    interface OnStoryClickListener {
        fun onStoryClick(holder: ViewHolder, story: Story)
    }

    lateinit var onStoryClickListener: OnStoryClickListener

    inner class ViewHolder(val binding: RowStoryBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.rowStoryName.setOnClickListener {
                onStoryClickListener.onStoryClick(this, storys[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = RowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return storys.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.rowStoryName.text = storys[position].name
    }
}