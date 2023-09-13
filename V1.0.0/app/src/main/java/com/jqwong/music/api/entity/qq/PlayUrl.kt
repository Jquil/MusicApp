package com.jqwong.music.api.entity.qq

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory


class PlayUrl(
    val code:Int,
    val data:String,
    val error_msg:String,
){
    companion object{
        fun fromJson(json:String): PlayUrl {
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
            val adapter = moshi.adapter(PlayUrl::class.java)
            return adapter.fromJson(json)!!
        }
    }
}