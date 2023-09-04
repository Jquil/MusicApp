package com.jqwong.music.api.entity.qq

import com.jqwong.music.model.Platform
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Artist(
    @field:Json(name = "id") val id:Long,
    @field:Json(name = "mid") val mid:String,
    @field:Json(name = "name") val name:String,
    @field:Json(name = "pmid") val pmid:String?,
    @field:Json(name = "title") val title:String,
){
    fun convert():com.jqwong.music.model.Artist{
        val url = "https://y.qq.com/music/photo_new/T001R500x500M000${mid}.jpg?max_age=2592000"
        return com.jqwong.music.model.Artist(
            id = id.toString(),
            name = name,
            pic = url,
            alias = listOf(),
            description = "",
            platform = Platform.QQ
        )
    }
}