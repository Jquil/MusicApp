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
            artists.add(com.jqwong.music.model.Artist(
                id = it.id.toString(),
                name = it.name,
                pic = null,
                description = null,
                platform = Platform.NetEaseCloud
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
            time = null,
            play_url = null,
            mv_url = null,
            mv_id = mv.toString(),
            is_local = false,
            enable_media = null,
            play_uri = null
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
    fun convert():Media{
        val artists = mutableListOf<com.jqwong.music.model.Artist>()
        artists.forEach {
            artists.add(com.jqwong.music.model.Artist(
                id = it.id.toString(),
                name = it.name,
                pic = null,
                description = null,
                platform = Platform.NetEaseCloud
            ))
        }
        return Media(
            platform = Platform.NetEaseCloud,
            id = id.toString(),
            name = name,
            album = album.name,
            album_id = album.id.toString(),
            pic = album.picUrl,
            artists = artists,
            time = null,
            play_url = null,
            mv_url = null,
            mv_id = mvid.toString(),
            is_local = false,
            enable_media = null,
            play_uri = null
        )
    }
}