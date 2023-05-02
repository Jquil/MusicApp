package com.jqwong.music.model

/**
 * @author: Jq
 * @date: 4/29/2023
 */
class MusicCondition(
    var title:String,
    val key:MStatus,
    val value:String,
)

enum class MStatus{
    SEARCH,
    ARTIST,
    BILLBOARD,
    SHEET,
    FAVORITE
}