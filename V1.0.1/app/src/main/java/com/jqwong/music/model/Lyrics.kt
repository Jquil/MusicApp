package com.jqwong.music.model

/**
 * @author: Jq
 * @date: 7/28/2023
 */
class Lyrics(
    val platform: Platform,
    val id:String,
    val lyrics:List<Lyric>
)
class Lyric(
    val time:Long,
    val text:String
)
enum class LyricStatus{
    Loading,
    Success,
    Error
}

fun Lyrics.current(position:Long):Lyric{
    var start = 0
    var end = lyrics.size - 1
    var mid = start + (end - start) / 2
    while ((end - start) != 1){
        val item = lyrics.get(mid)
        if(item.time == position)
            return item
        else if (item.time > position){
            end = mid
        }
        else{
            start = mid
        }
        mid = start + (end - start) / 2
    }
    return if(position < lyrics.get(end).time)
        lyrics.get(start)
    else
        lyrics.get(end)
}