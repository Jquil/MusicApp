package com.jqwong.music.model

/**
 * @author: Jq
 * @date: 7/28/2023
 */
class PlayList(
    var index:Int,
    var lyrics: Lyrics?,
    val data:List<Media>
){
    fun current():Media{
        return data.get(index)
    }
}