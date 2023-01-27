package com.jqwong.music.model


class GlobalObject{


    companion object{
        var User:User? = null
        var CurrentMedia:Media? = null
        var Lyric:Lyric? = null
        var CurrentLyricIndex = 0
        val ExtraKey = "extra"
        val UserKey = "user"
    }
}