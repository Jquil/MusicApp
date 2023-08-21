package com.jqwong.music.api.entity.kuwo

import com.jqwong.music.model.Platform
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author: Jq
 * @date: 7/28/2023
 */
@JsonClass(generateAdapter = true)
data class Song(
    @field:Json(name = "musicrid") val musicrid:String,
    @field:Json(name = "artist") val artist:String,
    @field:Json(name = "artistid") val artistid:Long,
    @field:Json(name = "mvpayinfo") val mvpayinfo:MvInfo,
    @field:Json(name = "pic") val pic:String?,
    @field:Json(name = "album") val album:String,
    @field:Json(name = "albumid") val albumid:Long,
    @field:Json(name = "albumpic") val albumpic:String?,
    @field:Json(name = "name") val name:String,
    //@field:Json(name = "songTimeMintues") val songTimeMintues:String,
    @field:Json(name = "hasmv") val hasmv:Int,
    @field:Json(name = "rid") val rid:Long
)
{
    @JsonClass(generateAdapter = true)
    data class MvInfo(
        @field:Json(name = "play") val play:Int,
        @field:Json(name = "vid") val vid:Long,
        @field:Json(name = "down") val down:Int?
    )

    fun convert():com.jqwong.music.model.Media{
        return com.jqwong.music.model.Media(
            platform = Platform.KuWo,
            id = rid.toString(),
            name = name,
            album = album,
            album_id = albumid.toString(),
            pic = pic ?: "",
            artists = listOf(
                com.jqwong.music.model.Artist(
                    platform = Platform.KuWo,
                    name = artist,
                    id = artistid.toString(),
                    description = null,
                    pic = null
                )
            ),
            time = null,
            play_url = null,
            mv_url = null,
            mv_id = rid.toString(),
            is_local = false,
            enable_media = null,
            play_uri = null
        )
    }
}