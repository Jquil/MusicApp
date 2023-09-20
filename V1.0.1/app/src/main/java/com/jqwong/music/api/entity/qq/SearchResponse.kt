package com.jqwong.music.api.entity.qq

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchResponse(
    @field:Json(name = "code") val code:Int,
    @Json(name = "music.search.SearchCgiService.DoSearchForQQMusicDesktop") val result:SearchResult
)

@JsonClass(generateAdapter = true)
data class SearchResult(
    @field:Json(name = "code") val code:Int,
    @field:Json(name = "data") val data:SearchData
)

@JsonClass(generateAdapter = true)
data class SearchData(
    @field:Json(name = "code") val code:Int,
    @field:Json(name = "body") val body:SearchBody,
)

@JsonClass(generateAdapter = true)
data class SearchBody(
    @field:Json(name = "song") val song:SearchBodySong
)

@JsonClass(generateAdapter = true)
data class SearchBodySong(
    @field:Json(name = "list") val list:List<Song>
)