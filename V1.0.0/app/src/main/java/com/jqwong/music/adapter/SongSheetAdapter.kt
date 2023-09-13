package com.jqwong.music.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.jqwong.music.R
import com.jqwong.music.model.Leaderboard
import com.jqwong.music.model.SongSheet

/**
 * @author: Jq
 * @date: 8/12/2023
 */
class SongSheetAdapter: BaseQuickAdapter<SongSheet, QuickViewHolder>()  {

    var showPic:Boolean = false

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: SongSheet?) {
        val tv = holder.getView<TextView>(R.id.tv_name)
        tv.text = item!!.name
        if(showPic){
            val iv = holder.getView<ImageView>(R.id.iv_pic)
            iv.visibility = View.VISIBLE
            Glide.with(iv)
                .asBitmap()
                .load(item.pic)
                .placeholder(R.drawable.ic_music)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
                .into(iv)
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_sheet,parent)
    }
}