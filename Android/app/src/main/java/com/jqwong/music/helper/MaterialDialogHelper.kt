package com.jqwong.music.helper

import android.content.Context
import android.widget.TextView
import androidx.core.view.children
import com.afollestad.materialdialogs.internal.main.DialogLayout
import com.jqwong.music.R

fun DialogLayout.setTitleDefaultStyle(ctx:Context){
    titleLayout.children.iterator().forEach {
        it.layoutParams
        if(it is TextView){
            it.setPadding(0,40,0,0)
            it.setTextColor(ctx.getColor(R.color.white))
            return@forEach
        }
    }
}