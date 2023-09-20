package com.jqwong.music.helper

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory


fun Map<String,Any>.toJson():String{
    val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    val adapter = moshi.adapter(Map::class.java)
    return adapter.toJson(this)
}