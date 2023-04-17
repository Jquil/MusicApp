package com.jqwong.music.service

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jqwong.music.api.service.HttpCall
import com.jqwong.music.api.service.MusicApiService
import com.jqwong.music.model.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Request

class SearchService {

    private val TAG = "SearchService"


    fun GetSearchResult(key:String,page:Int,size:Int,call:HttpCall<List<Song>>){
        MusicApiService.h.GetSearchResult(key,page,size)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: MusicApiService.MusicObserver<SongList>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    call.onError(e)
                }

                override fun onComplete() {
                }

                override fun onNext(t: MusicBaseData<SongList>) {
                    call.OnSuccess(t.data.list)
                }
            })
    }


    fun GetPlayUrl(rid:Int,callback:HttpCall<String>){

        val mapCache = GlobalCache.Urls.get(rid)
        if(mapCache != null)
        {
            callback.OnSuccess(mapCache)
            return
        }

        val observable = Observable.create<String> {
            val req = Request.Builder().url("http://www.kuwo.cn/api/v1/www/music/playUrl?mid=$rid&type=convert_url3").build()
            try {
                val res = MusicApiService.c.newCall(req).execute()
                val json = res.body()!!.string()
                val resultType = object : TypeToken<MusicBaseData<PlayUrl>>() {}.type
                val data = Gson().fromJson<MusicBaseData<PlayUrl>>(json,resultType)
                it.onNext(data.data.url)
                it.onComplete()
            }
            catch (e:java.lang.Exception){
                it.onError(e)
            }
        }
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:MusicApiService.BaseObserver<String>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    callback.onError(e)
                }

                override fun onComplete() {
                }

                override fun onNext(t: String) {
                    GlobalCache.Urls.put(rid,t)
                    callback.OnSuccess(t)
                }

            })
    }


    fun GetLyric(rid:Int,callback:HttpCall<Lyric>){
        val observable = Observable.create<Lyric> {
            val req = Request.Builder().url("http://m.kuwo.cn/newh5/singles/songinfoandlrc?musicId=${rid}&httpsStatus=1&reqId=f9204c10-1df1-11ec-8b4f-9f163660962a").build()
            //Log.e(TAG,"http://m.kuwo.cn/newh5/singles/songinfoandlrc?musicId=${rid}&httpsStatus=1&reqId=f9204c10-1df1-11ec-8b4f-9f163660962a")
            try {
                val res = MusicApiService.c.newCall(req).execute()
                val body = res.body()!!.string()
                val data = Gson().fromJson(body,Lyric::class.java)
                if(data.data != null && data.data.lrclist == null){
                    it.onError(Exception("LyricList is null"))
                }
                else{
                    it.onNext(data)
                    it.onComplete()
                }
            }
            catch (e:java.lang.Exception){
                Log.e(TAG,"" + e.message.toString())
                it.onError(e)
            }
        }



        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:MusicApiService.BaseObserver<Lyric>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    callback.onError(e)
                }

                override fun onComplete() {
                }

                override fun onNext(t: Lyric) {
                    callback.OnSuccess(t)
                }
            })
    }


    fun GetArtistInfo(artistId:String,callback: HttpCall<ArtistInfo>){
        MusicApiService.h.GetArtistInfo(artistId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:MusicApiService.MusicObserver<ArtistInfo>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: MusicBaseData<ArtistInfo>) {
                    callback.OnSuccess(t.data)
                }

                override fun onError(e: Throwable) {
                    callback.onError(e)
                }

                override fun onComplete() {
                }
            })
    }


    fun GetArtistSong(artistId: String,page:Int,size:Int,call:HttpCall<List<Song>>){
        MusicApiService.h.GetArtistSong(artistId,page,size)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:MusicApiService.MusicObserver<SongList>{
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: MusicBaseData<SongList>) {
                    call.OnSuccess(t.data.list)
                }

                override fun onError(e: Throwable) {
                    call.onError(e)
                }

                override fun onComplete() {
                }
            })
    }


    fun GetBangMenu(call:HttpCall<List<Bang>>){
        MusicApiService.h.GetBangMenu()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: MusicApiService.MusicObserver<List<Bang>> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    call.onError(e)
                }

                override fun onComplete() {

                }

                override fun onNext(t: MusicBaseData<List<Bang>>) {
                    call.OnSuccess(t.data)
                }

            })
    }


    fun GetBangSong(sourceId:String,page:Int,size:Int,call:HttpCall<List<Song>>){
        MusicApiService.h.GetBangSong(sourceId, page, size)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:MusicApiService.MusicObserver<MusicList>{
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: MusicBaseData<MusicList>) {
                    call.OnSuccess(t.data.musicList)
                }

                override fun onError(e: Throwable) {
                    call.onError(e)
                }

                override fun onComplete() {
                }
            })
    }
}