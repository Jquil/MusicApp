package com.jqwong.music.model

class ResultCondition(
    val key: ResultKey,
    val title:String,
    var data:String
)


enum class ResultKey{
    Search,
    Favorite,
    Sheet,
    Artist,
    Bang
}