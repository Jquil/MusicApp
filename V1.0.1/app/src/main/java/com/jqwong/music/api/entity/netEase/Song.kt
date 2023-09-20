package com.jqwong.music.api.entity.netEase

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
            val alias = mutableListOf<String>()
            if(it.alias != null){
                alias.addAll(it.alias)
            }
            artists.add(com.jqwong.music.model.Artist(
                id = it.id.toString(),
                name = it.name,
                pic = "",
                description = "",
                platform = Platform.NetEaseCloud,
                alias = alias,
                data = mutableMapOf()
            ))
        }
        return Media(
            platform = Platform.NetEaseCloud,
            id = id.toString(),
            name = name,
            album = al.name,
            album_id = al.id.toString(),
            pic = al.picUrl,
            artists = artists,
            time = 0,
            play_url = "",
            mv_url = "",
            mv_id = mv.toString(),
            is_local = false,
            enable_media = null,
            data = mutableMapOf()
        )
    }
}


@JsonClass(generateAdapter = true)
data class SongX(
    @field:Json(name = "name") val name:String,
    @field:Json(name = "id") val id:Long,
    @field:Json(name = "artists") val artists:List<Artist>,
    @field:Json(name = "album") val album: Album,
    @field:Json(name = "mvid") val mvid:Long,
){
}