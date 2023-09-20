package com.jqwong.music.api.entity.kuwo

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author: Jq
 * @date: 8/27/2023
 */
@JsonClass(generateAdapter = true)
data class SearchResult(
    @field:Json(name = "PN") val PN:String,
    @field:Json(name = "RN") val RN:String,
    @field:Json(name = "TOTAL") val TOTAL:String,
    @field:Json(name = "abslist") val abslist:List<Song2>
)