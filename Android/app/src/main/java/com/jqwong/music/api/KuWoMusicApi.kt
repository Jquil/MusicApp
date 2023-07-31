package com.jqwong.music.api

import com.jqwong.music.api.entity.kuwo.BaseResponse
import com.jqwong.music.api.entity.kuwo.SongListM
import com.jqwong.music.api.entity.kuwo.SongListX
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author: Jq
 * @date: 7/28/2023
 */
interface KuWoMusicApi {

    @GET("www/search/searchMusicBykeyWord?httpsStatus=1&reqId=23016430-e1eb-11eb-a2ee-bf024dbfa4c7")
    fun Search(@Query("key") key:String,@Query("pn") pn:Int,@Query("rn") rn:Int):Call<BaseResponse<SongListM>>
}