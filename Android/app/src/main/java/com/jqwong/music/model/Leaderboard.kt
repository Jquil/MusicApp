package com.jqwong.music.model

/**
 * @author: Jq
 * @date: 7/28/2023
 */
class Leaderboard(
    val platform: Platform,
    val id:String?,
    val name:String,
    val children:List<Leaderboard>?
)