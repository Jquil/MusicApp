package com.jqwong.music.model

/**
 * @author: Jq
 * @date: 7/28/2023
 */
data class Media(
    var audio:Audio?,
    var video:Video?
)

fun List<Media>.copy():List<Media>{
    val list = mutableListOf<Media>()
    forEach {
        list.add(it.copy())
    }
    return list
}