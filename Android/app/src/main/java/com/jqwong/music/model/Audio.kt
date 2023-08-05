package com.jqwong.music.model

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

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
    has_mv: Boolean
) : Song(platform, id, name,album_id, album, pic, artists, time, play_url, mv_url, has_mv) {
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
                song.has_mv
            )
        }
    }
}

fun Audio.build(): MediaItem {
    return MediaItem.Builder()
        .setUri(play_url)
        .setMediaMetadata(
            MediaMetadata.Builder()
                .setAlbumTitle(album)
                .setAlbumArtist(artists.toName())
                .setArtist(artists.toName())
                .setTitle(name)
                .setArtworkUri(Uri.parse("https://jqwong.cn/file/music_app_artwork.png"))
                .build()
        )
        .build()
}