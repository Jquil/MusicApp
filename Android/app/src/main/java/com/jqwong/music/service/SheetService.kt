package com.jqwong.music.service

import com.jqwong.music.api.service.HttpCall
import com.jqwong.music.api.service.MusicApiService
import com.jqwong.music.api.service.RemoteApiService
import com.jqwong.music.repository.SheetRepository
import com.jqwong.music.repository.entity.Sheet
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

class SheetService:IService {

    private var _Repo:SheetRepository

    init {
        _Repo = SheetRepository()
    }


    fun Add(sheetName:String,userId: Long,token:String="",syncToRemote:Boolean = true){
        val inToken = if(token == "") UUID.randomUUID().toString() else token;
        val sheet = Sheet(null,sheetName,userId,false,inToken)
        _Repo.Add(sheet)
        if(syncToRemote){
            RemoteService().AddSheet(sheet)
        }
    }


    fun Delete(sheet:Sheet){
        sheet.Delete = true
        _Repo.Update(sheet)
        RemoteService().UpdateSheet(sheet)
    }


    fun Update(sheet: Sheet){
        _Repo.Update(sheet)
        RemoteService().UpdateSheet(sheet)
    }

    fun Load(userId:Long):List<Sheet>{
        return _Repo.Load(userId)
    }

}