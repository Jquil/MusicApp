package com.jqwong.music.adapter

import android.content.Context
import android.graphics.Color
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
import com.jqwong.music.app.App
import com.jqwong.music.model.Media

/**
 * @author: Jq
 * @date: 7/31/2023
 */
class MediaAdapter: BaseQuickAdapter<Media, QuickViewHolder>(){

    private val color_select = Color.parseColor("#C04641")
    private val color_default = Color.parseColor("#FFFFFF")

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: Media?) {
        val tvTitle = holder.getView<TextView>(R.id.tv_title)
        val tvDescription = holder.getView<TextView>(R.id.tv_description)
        val ivPic = holder.getView<ImageView>(R.id.iv_pic)
        val ivPlaying = holder.getView<ImageView>(R.id.iv_playing)
        if(item?.audio != null){
            val builder = StringBuilder()
            item.audio?.artists?.forEach {
                builder.append("${it.name}/")
            }
            if(builder.isNotEmpty()){
                builder.deleteCharAt(builder.length-1)
                item.audio?.album.let {
                    if(!it.isNullOrEmpty()){
                        builder.append(" - ")
                    }
                }
            }
            tvTitle.text = item.audio?.name
            tvDescription.text = "${builder.toString()}${item.audio?.album}"
            Glide.with(ivPic)
                .asBitmap()
                .load(item.audio?.pic)
                .placeholder(R.drawable.ic_music)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
                .into(ivPic)
        }
        else{
            // show video info
        }


        if(App.playListIsInitialized()){
            val current = App.playList.data.get(App.playList.index)
            if(current.audio != null && item!!.audio != null){
                if(current.audio!!.id == item.audio!!.id && current.audio!!.name == item.audio!!.name){
                    tvTitle.setTextColor(color_select)
                    tvDescription.setTextColor(color_select)
                    ivPlaying.visibility = View.VISIBLE
                    ivPlaying.setImageResource(R.drawable.ic_playing)
                }
                else{
                    tvTitle.setTextColor(color_default)
                    tvDescription.setTextColor(color_default)
                    ivPlaying.visibility = View.GONE
                }
            }
            else{

            }
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