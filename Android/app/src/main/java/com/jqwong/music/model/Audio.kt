package com.jqwong.music.model

import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * @author: Jq
 * @date: 7/28/2023
 */
class Audio(
    platform: Platform,
    id: String,
    name:String,
    album_id: String,
    album: String,
    pic: String,
    artists: List<Artist>,
    time: Long?,
    play_url: String?,
    mv_url: String?,
    has_mv: Boolean,
    mv_id:String?,
    var changeInfo: AudioChangeInfo? = null
) : Song(platform, id, name,album_id, album, pic, artists, time, play_url, mv_url, has_mv,mv_id) {
    companion object{
        fun convert(song:Song):Audio{
            return Audio(
                song.platform,
                song.id,
                song.name,
                song.album_id,
                song.album,
                song.pic,
                song.artists,
                song.time,
                song.play_url,
                song.mv_url,
                song.has_mv,
                song.mv_id
            )
        }
    }
}

data class AudioChangeInfo(
    var platform: Platform?,
    var id:String?,
    var url:String?,
    var data:Any?
)

fun Audio.toJson():String{
    val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    val adapter = moshi.adapter(Audio::class.java)
    return adapter.toJson(this)
}

fun String.toAudio():Audio{
    val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())//使用kotlin反射处理，要加上这个
        .build()
    val adapter = moshi.adapter(Audio::class.java)
    return adapter.fromJson(this)!!
}

fun Audio.build(): MediaItem {
    val bundle = Bundle().apply {
        putString(ExtraKey.Audio.name,toJson())
    }
    return MediaItem.Builder()
        .setUri(play_url)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setExtras(bundle)
                .setAlbumTitle(album)
                .setAlbumArtist(artists.toName())
                .setArtist(artists.toName())
                .setTitle(name)
                .setArtworkUri(Uri.parse("https://jqwong.cn/file/music_app_artwork.png"))
                .build()
        )
        .build()
}