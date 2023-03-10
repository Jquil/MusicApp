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
import com.jqwong.music.model.ArtistInfo
import com.jqwong.music.repository.entity.Artist
import com.jqwong.music.repository.entity.Sheet

class ArtistAdapter: BaseQuickAdapter<ArtistInfo, QuickViewHolder>() {
    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: ArtistInfo?) {
        holder.getView<TextView>(R.id.tv_name).text = item?.name
        val ivAvatar = holder.getView<ImageView>(R.id.iv_avatar)
        Glide.with(ivAvatar)
            .asBitmap()
            .load(item?.pic70)
            .placeholder(R.color.black)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
            .into(ivAvatar)
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.component_artist,parent)
    }
}