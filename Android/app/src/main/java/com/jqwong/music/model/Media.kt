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

fun List<Media>.getIndex(audio: Audio):Int{
    for (i in 0 until count()){
        if(get(i).audio != null
            && get(i).audio!!.platform == audio.platform
            && get(i).audio!!.id == audio.id){
            return i
        }
    }
    return -1
}