package com.jqwong.music.model

/**
 * @author: Jq
 * @date: 7/28/2023
 */
class Video(
    val platform:Platform,
    val id:String,
    val title:String,
    val publisher:String,
    val pic:String,
    val url:String,
    val bind_lyric_info:BindLyricInfo
) {
    class BindLyricInfo(
        val platform:Platform,
        val id:String,
        val play_speed_delay:Long,
        val play_speed_quicken:Long,
        val is_bind:Boolean
    )
}