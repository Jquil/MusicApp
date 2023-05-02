package com.jqwong.music.model

/**
 * @author: Jq
 * @date: 4/29/2023
 */
class Media(
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
    var url:String?,
    var artistid:Long
)


class MediaList(
    val list: List<Media>
)

class BillboardMediaList(
    val musicList: List<Media>
)