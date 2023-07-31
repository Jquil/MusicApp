package com.jqwong.music.model

/**
 * @author: Jq
 * @date: 7/28/2023
 */
open class Song(
    val platform: Platform,
    val id:String,
    val album_id:String,
    val album:String,
    val pic:String,
    val artists:List<Artist>,
    val time:Long?,
    val play_url:String?,
    val mv_url:String?,
    val has_mv:Boolean
)