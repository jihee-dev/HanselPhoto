package com.android.study.hanselandphotograph.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.android.study.hanselandphotograph.R
import com.android.study.hanselandphotograph.model.Picture

class ImagePageAdapter(private val list: ArrayList<Picture>) : PagerAdapter() {
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        val view = inflater.inflate(R.layout.view_pager_layout, container, false)

        val pagerTitleText = view.findViewById<TextView>(R.id.pager_title_text)
        val pagerImage = view.findViewById<ImageView>(R.id.pager_image)

        pagerTitleText.text = list[position].title
        pagerImage.setImageURI(Uri.parse(list[position].path))

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }
}