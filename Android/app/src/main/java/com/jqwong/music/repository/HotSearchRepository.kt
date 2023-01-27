package com.jqwong.music.repository

import com.jqwong.music.app.App
import com.jqwong.music.repository.dao.HotSearchDao
import com.jqwong.music.repository.entity.HotSearch

class HotSearchRepository {

    private var _Dao:HotSearchDao

    init {
        _Dao = App.mSession.hotSearchDao
    }

    fun Load():List<HotSearch>{
        return _Dao.loadAll().toList()
    }


    fun DeleteAll(){
        _Dao.deleteAll()
    }


    fun Add(key:String,date:String){
        _Dao.insert(HotSearch(null,key,date))
    }

}