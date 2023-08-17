package com.jqwong.music.model

/**
 * @author: Jq
 * @date: 7/28/2023
 */
open class Song(
    val platform: Platform,
    val id:String,
    val name:String,
    val album_id:String,
    val album:String,
    val pic:String,
    val artists:List<Artist>,
    val time:Long?,
    var play_url:String?,
    val mv_url:String?,
    val has_mv:Boolean,
    val mv_id:String?,
)