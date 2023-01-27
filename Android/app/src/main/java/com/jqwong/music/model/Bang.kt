package com.jqwong.music.model

class Bang (
    val name:String,
    val list:List<BangItem>
)

class BangItem(
    val sourceid:String,
    val id:String,
    var name:String
)