package com.jqwong.music.api.entity.kuwo

/**
 * @author: Jq
 * @date: 7/28/2023
 */
class RecommendSongSheet(
    val list:List<Item>
)
{
    class Item(
        val img:String,
        val uname:String,
        val img700:String,
        val name:String,
        val id:Long,
        val info:String
    )
}