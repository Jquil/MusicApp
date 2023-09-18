package com.jqwong.music.model

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Alias(val name:String,val alias:List<String>){
    companion object{
        fun fromJson(json:String):List<Alias>{
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
            val type = Types.newParameterizedType(MutableList::class.java, Alias::class.java)
            val adapter = moshi.adapter<List<Alias>>(type)
            return adapter.fromJson(json)!!
        }
    }
}