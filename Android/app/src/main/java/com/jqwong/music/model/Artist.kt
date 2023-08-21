package com.jqwong.music.model

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * @author: Jq
 * @date: 7/28/2023
 */
class Artist(
    val id:String,
    val name:String,
    val pic:String?,
    val description:String?,
    val platform: Platform
){
    companion object{
        fun fromJson(json:String):Artist{
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())//使用kotlin反射处理，要加上这个
                .build()
            val adapter = moshi.adapter(Artist::class.java)
            return adapter.fromJson(json)!!
        }
    }
    fun toJson():String{
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val adapter = moshi.adapter(Artist::class.java)
        return adapter.toJson(this)
    }
}

fun List<Artist>.toName():String{
    val builder = StringBuilder()
    forEach {
        builder.append(it.name)
        builder.append('/')
    }
    builder.deleteCharAt(builder.length-1)
    return builder.toString()
}