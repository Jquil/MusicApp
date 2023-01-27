package com.jqwong.music.adapter

import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.jqwong.music.R
import com.jqwong.music.model.LyricItem
import com.jqwong.music.repository.entity.Sheet

class SelectSheetAdapter: BaseQuickAdapter<Sheet, QuickViewHolder>() {
    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: Sheet?) {
        holder.getView<Button>(R.id.btn_sheet).text = item?.name
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.component_select_sheet,parent)
    }
}