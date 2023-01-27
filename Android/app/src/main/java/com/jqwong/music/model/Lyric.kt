package com.jqwong.music.model

class LyricItem(val lineLyric:String,val time:String,var inTime:Long)

class LyricList(val lrclist:List<LyricItem>){

    fun FindCurrentIndex(position:Long):Int{
        var start = 0
        var end: Int = lrclist.size - 1
        var index: Int = start + (end - start) / 2

        while (start < end) {
            val mid: Long = lrclist.get(index).inTime
            if (mid == position) break
            else if (mid > position) end = index
            else start = index + 1
            index = start + (end - start) / 2
        }

        index = if(index == lrclist.size-1) index else index - 1
        return index
    }
}

class Lyric(val data:LyricList,var rid:Int = 0)