package com.jqwong.music.model

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