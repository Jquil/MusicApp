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
 * @date: 7/31/2023
 */
class MediaAdapter: BaseQuickAdapter<Media, QuickViewHolder>(){
    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: Media?) {
        val tvTitle = holder.getView<TextView>(R.id.tv_title)
        val tvDescription = holder.getView<TextView>(R.id.tv_description)
        val ivPic = holder.getView<ImageView>(R.id.iv_pic)
        if(item?.audio != null){
            val builder = StringBuilder()
            item.audio?.artists?.forEach {
                builder.append("${it.name}/")
            }
            if(builder.isNotEmpty()){
                builder.deleteCharAt(builder.length-1)
            }
            tvTitle.text = item.audio?.name
            tvDescription.text = "${builder.toString()} - ${item.audio?.album}"
            Glide.with(ivPic)
                .asBitmap()
                .load(item.audio?.pic)
                .placeholder(R.drawable.ic_music)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
                .into(ivPic)
        }
        else{

        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_media,parent)
    }
}