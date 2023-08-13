package com.jqwong.music.model

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * @author: Jq
 * @date: 7/28/2023
 */
class SongSheet(
    val platform: Platform,
    val id:String,
    val name:String,
    val pic:String?,
    val description:String?
){

    companion object{
        fun fromJsom(json:String):SongSheet{
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
            val adapter = moshi.adapter(SongSheet::class.java)
            return adapter.fromJson(json)!!
        }
    }
    fun toJson():String{
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val adapter = moshi.adapter(SongSheet::class.java)
        return adapter.toJson(this)
    }
}