package com.jqwong.music.api.entity.netEase

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author: Jq
 * @date: 8/13/2023
 */

@JsonClass(generateAdapter = true)
data class DailySongs(
    @field:Json(name = "dailySongs")  val dailySongs:List<Song>
)