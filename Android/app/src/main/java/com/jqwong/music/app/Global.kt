package com.jqwong.music.app

import com.jqwong.music.entity.User
import com.jqwong.music.model.Lyric
import com.jqwong.music.model.Media

/**
 * @author: Jq
 * @date: 5/1/2023
 */
class Global {
    companion object{
        var user: User? = null
        var media: Media? = null
        var lyric:Lyric? = null
        var lyricIndex:Int = 0
    }
}