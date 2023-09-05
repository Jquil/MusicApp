package com.jqwong.music.model

import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * @author: Jq
 * @date: 7/28/2023
 */
data class Media(
    var platform: Platform,
    var id:String,
    var name:String,
    var album:String,
    var album_id:String,
    var artists:List<Artist>,
    var pic:String,
    var time:Long,
    var play_url:String,
    var mv_id:String,
    var mv_url:String,
    var is_local:Boolean,
    var enable_media:Media?,
    var data:MutableMap<String,Any>
)
{
    companion object{
        fun fromJson(json:String):Media{
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
            val adapter = moshi.adapter(Media::class.java)
            return adapter.fromJson(json)!!
        }
    }

    fun build():MediaItem{
        val bundle = Bundle().apply {
            putString(ExtraKey.Media.name,toJson())
        }
        val builder = MediaItem.Builder()
        builder.setUri(play_url)
        if(enable_media != null){
            builder.setUri(enable_media!!.play_url)
        }
        builder.setMediaMetadata(MediaMetadata.Builder()
            .setExtras(bundle)
            .setAlbumTitle(album)
            .setAlbumArtist(artists.toName())
            .setArtist(artists.toName())
            .setTitle(name)
            .setArtworkUri(Uri.parse(pic))
            .build())
        return builder.build()
    }

    fun filename():String{
        return "${platform.name}-${id}.aac"
    }

    fun toJson():String{
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val adapter = moshi.adapter(Media::class.java)
        return adapter.toJson(this)
    }
}



fun List<Media>.copy():List<Media>{
    val list = mutableListOf<Media>()
    forEach {
        list.add(it.copy())
    }
    return list
}

fun List<Media>.getIndex(media: Media):Int{
    for (i in 0 until count()){
        if(get(i).platform == media.platform
            && get(i).id == media.id){
            return i
        }
    }
    return -1
}