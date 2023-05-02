package com.jqwong.music.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.jqwong.music.R
import com.jqwong.music.entity.Sheet

/**
 * @author: Jq
 * @date: 4/30/2023
 */

class SheetAdapter: BaseQuickAdapter<Sheet, QuickViewHolder>() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val _Gradients:List<Gradient> = listOf(
        Gradient(before = Color.parseColor("#fbc2eb"), after = Color.parseColor("#a6c1ee")),
        Gradient(before = Color.parseColor("#fc4a1a"), after = Color.parseColor("#f7b733")),
        Gradient(before = Color.parseColor("#43C6AC"), after = Color.parseColor("#191654")),
        Gradient(before = Color.parseColor("#43C6AC"), after = Color.parseColor("#F8FFAE")),
        Gradient(before = Color.parseColor("#FFAFBD"), after = Color.parseColor("#ffc3a0"))
    )


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: Sheet?) {
        holder.getView<Button>(R.id.btn_sheet_detail).text = item?.name

        val index = (0.._Gradients.size-1).random()
        val bg = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            gradientType = GradientDrawable.RADIAL_GRADIENT
            gradientRadius = 1000f
            setGradientCenter(1f,1f)
            setColors(intArrayOf(_Gradients[index].before,_Gradients[index].after))
        }
        holder.getView<ConstraintLayout>(R.id.cl_sheet).background = bg
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_sheet,parent)
    }


    class Gradient(val before:Int,val after:Int)
}