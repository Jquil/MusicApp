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
    val pic:String,
    val alias:List<String>,
    val description:String,
    val platform: Platform,
    val data:MutableMap<String,Any>
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

fun List<Artist>.compare(artist:List<Artist>):Boolean{
    var count = 0
    val list = mutableListOf<Artist>()
    list.addAll(artist)
    forEach {
        for (i in list.count()-1 downTo 0){
            val item = list[i]
            if(item.name == it.name){
                count++
                list.removeAt(i)
                break
            }
        }
    }
    return count == count()
}

fun List<Artist>.exist(artists: List<Artist>):Boolean{
    forEach {
        val name = it.name
        val alias = it.alias
        artists.forEach {
            if(name.equals(it.name) || alias.contains(it.name) || it.alias.contains(name))
                return true
        }
    }
    return false
}