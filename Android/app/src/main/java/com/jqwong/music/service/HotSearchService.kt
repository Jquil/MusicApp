package com.jqwong.music.service

import android.util.Log
import com.jqwong.music.api.service.HttpCall
import com.jqwong.music.api.service.MusicApiService
import com.jqwong.music.model.MusicBaseData
import com.jqwong.music.repository.HotSearchRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.Date

class HotSearchService {

    private var _Repo:HotSearchRepository

    init {
        _Repo = HotSearchRepository()
    }

    fun GetHotSearchList(call:HttpCall<List<String>>){

        val date:String = SimpleDateFormat("dd/M/yyyy").format(Date())
        val list = _Repo.Load()
        if(list.count() != 0 && list[0].Date.equals(date))
        {
            val _data = mutableListOf<String>()
            list.forEach {
                _data.add(it.key)
            }
            call.OnSuccess(_data)
            return
        }


        _Repo.DeleteAll()
        MusicApiService.h.GetHotSearch()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : MusicApiService.MusicObserver<List<String>> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    call.onError(e)
                }

                override fun onComplete() {
                }

                override fun onNext(t: MusicBaseData<List<String>>) {
                    call.OnSuccess(t.data)
                    t.data.forEach {
                        _Repo.Add(it,date)
                    }
                }
            })
    }
}