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