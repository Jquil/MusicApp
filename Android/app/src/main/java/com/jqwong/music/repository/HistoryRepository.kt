package com.jqwong.music.repository

import com.jqwong.music.app.App
import com.jqwong.music.repository.dao.HistoryDao
import com.jqwong.music.repository.dao.HistoryDao.Properties
import com.jqwong.music.repository.entity.History

class HistoryRepository {

    private var _Dao:HistoryDao


    init {
        _Dao = App.mSession.historyDao
    }


    fun Load(max:Int):List<History>{
        var data = _Dao.queryBuilder().orderDesc(Properties.Id).limit(max).list()
        return data
    }


    fun Add(key:String){
        _Dao.insert(History(null,key))
    }


    fun Delete(key:String){
        _Dao.deleteInTx(_Dao.queryBuilder().where(Properties.Key.eq(key)).list())
    }
}