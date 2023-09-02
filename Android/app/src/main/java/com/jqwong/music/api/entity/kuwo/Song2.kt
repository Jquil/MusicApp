package com.jqwong.music.api.entity.kuwo

import com.jqwong.music.model.Artist
import com.jqwong.music.model.Media
import com.jqwong.music.model.Platform
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author: Jq
 * @date: 8/27/2023
 */
@JsonClass(generateAdapter = true)
data class Song2(
    @field:Json(name = "ARTIST") val ARTIST:String,
    @field:Json(name = "ARTISTID") val ARTISTID:String,
    @field:Json(name = "allartistid") val allartistid:String,
    @field:Json(name = "MUSICRID") val MUSICRID:String,
    @field:Json(name = "ALBUM") val ALBUM:String,
    @field:Json(name = "ALBUMID") val ALBUMID:String,
    @field:Json(name = "NAME") val NAME:String,
    @field:Json(name = "SONGNAME") val SONGNAME:String,
    @field:Json(name = "web_albumpic_short") val web_albumpic_short:String,
    @field:Json(name = "web_artistpic_short") val web_artistpic_short:String,
){
    fun convert():Media{
        val id = MUSICRID.replace("MUSIC_","")
        val artist = mutableListOf<com.jqwong.music.model.Artist>()
        if(ARTIST.contains('&') && allartistid.contains('&')){
            val arr1 = ARTIST.split('&')
            val arr2 = allartistid.split('&')
            if(arr1.count() != arr2.count()){
                artist.add(Artist(
                    id = ARTISTID,
                    name = ARTIST,
                    pic = "https://star.kuwo.cn/star/starheads/${web_artistpic_short}",
                    description = "",
                    platform = Platform.KuWo,
                    alias = listOf()
                ))
            }
            else{
                for(i in 0 until arr1.size){
                    artist.add(Artist(
                        id = arr2[i],
                        name = arr1[i],
                        pic = "https://star.kuwo.cn/star/starheads/${web_artistpic_short}",
                        description = "",
                        platform = Platform.KuWo,
                        alias = listOf()
                    ))
                }
            }
        }
        else{
            artist.add(Artist(
                id = ARTISTID,
                name = ARTIST,
                pic = "https://star.kuwo.cn/star/starheads/${web_artistpic_short}",
                description = "",
                platform = Platform.KuWo,
                alias = listOf()
            ))
        }
        return Media(
            platform = Platform.KuWo,
            id = id,
            name = SONGNAME,
            album = ALBUM,
            album_id = ALBUMID,
            artists = artist,
            pic = "https://img4.kuwo.cn/star/albumcover/${web_albumpic_short}",
            time = 0,
            play_url = "",
            mv_id = id,
            mv_url = "",
            is_local = false,
            enable_media = null
        )
    }
}