package com.jqwong.music.adapter

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.jqwong.music.R
import com.jqwong.music.model.Lyric

/**
 * @author: Jq
 * @date: 8/4/2023
 */
class LyricAdapter: BaseQuickAdapter<Lyric, QuickViewHolder>(){

    lateinit var current:Lyric

    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: Lyric?) {
        val tvName = holder.getView<TextView>(R.id.tv_name)
        tvName.text = item!!.text
        if(this::current.isInitialized && item.text == current.text && item.time == current.time){
            tvName.setTextColor(Color.WHITE)
        }
        else{
            tvName.setTextColor(Color.parseColor("#928b7b"))
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_lyric,parent)
    }

    fun currentIsInitialized():Boolean{
        return this::current.isInitialized
    }
}