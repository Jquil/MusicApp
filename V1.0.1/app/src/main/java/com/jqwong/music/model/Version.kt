package com.jqwong.music.model

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * @author: Jq
 * @date: 7/28/2023
 */
class Version(
    val version:String,
    val number:Int,
    val log:String,
    val path:String,
){
    companion object{
        fun fromJson(json:String):Version{
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
            val adapter = moshi.adapter(Version::class.java)
            return adapter.fromJson(json)!!
        }
    }
}