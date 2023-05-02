package com.jqwong.music.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.jqwong.music.R
import com.jqwong.music.model.Media

/**
 * @author: Jq
 * @date: 4/29/2023
 */
class MusicListAdapter : BaseQuickAdapter<Media, QuickViewHolder>(){

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: Media?) {
        holder.getView<TextView>(R.id.btn_sheet_detail).text = item!!.name
        val ivPic = holder.getView<ImageView>(R.id.iv_pic)
        Glide.with(ivPic)
            .asBitmap()
            .load(item.pic)
            .placeholder(R.color.black)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
            .into(ivPic)
        val info:String = if(item.album == "") item.artist else item.artist + " - " + item.album
        holder.getView<TextView>(R.id.tv_info).text = info
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.componment_music_list_item,parent)
    }

}