package com.jqwong.music.helper

import android.graphics.Color
import android.widget.TextView
import androidx.core.view.children
import com.afollestad.materialdialogs.internal.main.DialogLayout

fun DialogLayout.setTitlePadding(padding:Int){
    titleLayout.children.iterator().forEach {
        it.layoutParams
        if(it is TextView){
            it.setPadding(0,40,0,0)
            return@forEach
        }
    }
}

fun DialogLayout.setTitleColor(color: Int){
    titleLayout.children.iterator().forEach {
        it.layoutParams
        if(it is TextView){
            it.setPadding(0,40,0,0)
            it.setTextColor(color)
            return@forEach
        }
    }
}