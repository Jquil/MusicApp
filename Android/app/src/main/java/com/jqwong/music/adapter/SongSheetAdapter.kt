package com.jqwong.music.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
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
    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: SongSheet?) {
        val tv = holder.getView<TextView>(R.id.tv_name)
        tv.text = item!!.name
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_sheet,parent)
    }
}