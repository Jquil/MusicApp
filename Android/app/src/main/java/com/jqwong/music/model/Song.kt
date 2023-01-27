package com.jqwong.music.model


class SongList(
    val list: List<Song>
)

class MusicList(
    val musicList:List<Song>
)

class Song(
    val musicrid:String,
    val rid:Int,
    val artist:String,
    val pic:String,
    val album:String,
    val albumid:Int,
    val name:String,
    val pic120:String,
    val songTimeMinutes:String,
    var time:Long = 0,
    var url:String = "",
    var artistid:Long
)