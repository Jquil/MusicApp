package com.jqwong.music.api.entity.qq

import com.jqwong.music.model.Media
import com.jqwong.music.model.Platform
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Song(
    @field:Json(name = "id") val id:Long,
    @field:Json(name = "name") val name:String,
    @field:Json(name = "singer") val singer:List<Artist>,
    @field:Json(name = "title") val title:String,
    @field:Json(name = "url") val url:String,
    @field:Json(name = "mv") val mv: Mv,
    @field:Json(name = "album") val album: Album,
    @field:Json(name = "mid") val mid: String,
){
    fun convert():Media{
        val pic = "https://y.qq.com/music/photo_new/T002R300x300M000${album.mid}.jpg?max_age=2592000"
        val artists = mutableListOf<com.jqwong.music.model.Artist>()
        singer.forEach {
            artists.add(it.convert())
        }
        return Media(
            Platform.QQ,
            id = mid,
            name = name,
            album = album.name,
            album_id = album.id.toString(),
            artists = artists,
            pic = pic,
            time = 0,
            play_url = url,
            mv_id = mv.id.toString(),
            mv_url = "",
            is_local = false,
            enable_media = null
        )
    }
}