package com.jqwong.music.model

/**
 * @author: Jq
 * @date: 7/28/2023
 */
class Audio:Song {
    constructor(
        platform: Platform,
        id: String,
        album_id: String,
        album: String,
        pic: String,
        artists: List<Artist>,
        time: Long,
        play_url: String,
        mv_url: String,
        has_mv: Boolean
    ) : super(platform, id, album_id, album, pic, artists, time, play_url, mv_url, has_mv)
}