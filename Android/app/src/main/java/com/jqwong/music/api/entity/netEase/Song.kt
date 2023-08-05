package com.jqwong.music.api.entity.netEase

import com.jqwong.music.model.Audio
import com.jqwong.music.model.Media
import com.jqwong.music.model.Platform
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Song(
    @field:Json(name = "id") val id:Long,
    @field:Json(name = "name") val name:String,
    @field:Json(name = "ar") val ar:List<Artist>,
    @field:Json(name = "al") val al:Album,
    @field:Json(name = "mv") val mv:Long,
){
    fun convert():Media{
        val artists = mutableListOf<com.jqwong.music.model.Artist>()
        ar.forEach {
            artists.add(com.jqwong.music.model.Artist(
                id = it.id.toString(),
                name = it.name,
                pic = null,
                description = null,
                platform = Platform.NetEaseCloud
            ))
        }
        return Media(
            video = null,
            audio = Audio(
                platform = Platform.NetEaseCloud,
                id = id.toString(),
                name = name,
                album = al.name,
                album_id = al.id.toString(),
                pic = al.picUrl,
                artists = artists,
                time = null,
                play_url = null,
                mv_url = null,
                has_mv = mv == "0".toLong()
            )
        )
    }
}