package com.jqwong.music.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.jqwong.music.R
import com.jqwong.music.model.Leaderboard

class LeaderBoardAdapter: BaseQuickAdapter<Leaderboard, QuickViewHolder>() {
    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: Leaderboard?) {
        val tv = holder.getView<TextView>(R.id.tv_name)
        tv.text = item!!.name
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_leaderboard,parent)
    }
}