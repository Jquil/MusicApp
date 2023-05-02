package com.jqwong.music.model

/**
 * @author: Jq
 * @date: 4/30/2023
 */
class Billboard (
    val sourceid:String,
    val id:String,
    var name:String,
    val time:String?,
)

class BillboardList(
    var name:String,
    val list:List<Billboard>
)