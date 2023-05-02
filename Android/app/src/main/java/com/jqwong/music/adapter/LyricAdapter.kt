package com.jqwong.music.adapter

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.view.ViewGroup
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.QuickViewHolder
import com.jqwong.music.R
import com.jqwong.music.app.Global
import com.jqwong.music.model.LyricItem

/**
 * @author: Jq
 * @date: 5/1/2023
 */
class LyricAdapter: BaseQuickAdapter<LyricItem, QuickViewHolder>() {
    private val TAG = "LyricAdapter"
    private val _Min = 14f
    private val _Max = 20f
    private var _PreAnimationToBigPoision = -1
    override fun onBindViewHolder(holder: QuickViewHolder, position: Int, item: LyricItem?) {
        val tvLyric = holder.getView<TextView>(R.id.tv_lyric)
        tvLyric.text = item?.lineLyric
        val duration:Long = 250
        if(position == Global.lyricIndex){
            val anim = PropertyValuesHolder.ofFloat("textSize",_Min,_Max)
            ObjectAnimator.ofPropertyValuesHolder(tvLyric,anim)
                .setDuration(duration)
                .start()
            val p = position
            _PreAnimationToBigPoision = p
        }
        else{
            if(position == _PreAnimationToBigPoision){
                val anim = PropertyValuesHolder.ofFloat("textSize",_Max,_Min)
                ObjectAnimator.ofPropertyValuesHolder(tvLyric,anim)
                    .setDuration(duration)
                    .start()
            }
            else{
                tvLyric.textSize = _Min
            }
        }
    }

    override fun onCreateViewHolder(
        context: Context,
        parent: ViewGroup,
        viewType: Int
    ): QuickViewHolder {
        return QuickViewHolder(R.layout.item_lyric,parent)
    }
}