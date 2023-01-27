package com.jqwong.music.service

import com.jqwong.music.repository.HistoryRepository
import com.jqwong.music.repository.entity.History

class HistoryService:IService {

    private var _Repo:HistoryRepository

    init {
        _Repo = HistoryRepository()
    }


    fun Add(key:String){
        _Repo.Add(key)
    }


    fun GetAll(max:Int = 5):List<History>{
        return _Repo.Load(max)
    }


    fun Delete(key:String){
        _Repo.Delete(key)
    }
}