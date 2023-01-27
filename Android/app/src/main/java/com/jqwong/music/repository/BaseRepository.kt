package com.jqwong.music.repository

abstract class BaseRepository {

    protected var TAG:String = ""

    init {
        TAG = javaClass.name
    }
}