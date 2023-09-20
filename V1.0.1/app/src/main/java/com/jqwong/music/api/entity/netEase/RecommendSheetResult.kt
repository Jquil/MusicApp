package com.jqwong.music.api.entity.netEase

import com.jqwong.music.model.Platform
import com.jqwong.music.model.SongSheet
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author: Jq
 * @date: 8/12/2023
 */
@JsonClass(generateAdapter = true)
class RecommendSheetResult(
    @field:Json(name = "code") val code:Int,
    @field:Json(name = "featureFirst") val featureFirst:Boolean,
    @field:Json(name = "haveRcmdSongs") val haveRcmdSongs:Boolean,
    @field:Json(name = "recommend") val recommend:List<RecommendSheet>
)

@JsonClass(generateAdapter = true)
class RecommendSheet(
    @field:Json(name = "id") val id:Long,
    @field:Json(name = "type") val type:Int,
    @field:Json(name = "name") val name:String,
    @field:Json(name = "copywriter") val copywriter:String,
    @field:Json(name = "picUrl") val picUrl:String,
    @field:Json(name = "playcount") val playcount:Long,
    @field:Json(name = "createTime") val createTime:Long,
    @field:Json(name = "trackCount") val trackCount:Int,
    //@field:Json(name = "creator") val creator:RecommendSheetCreator?
){
    fun convert():SongSheet{
        return SongSheet(
            platform = Platform.NetEaseCloud,
            id = id.toString(),
            description = "",
            pic =  picUrl,
            name = name
        )
    }
}

@JsonClass(generateAdapter = true)
class RecommendSheetCreator(
    @field:Json(name = "remarkName") val remarkName:String,
    @field:Json(name = "mutual") val mutual:Boolean,
    @field:Json(name = "avatarImgId") val avatarImgId:Long,
    @field:Json(name = "backgroundImgId") val backgroundImgId:Long,
    @field:Json(name = "detailDescription") val detailDescription:String,
)