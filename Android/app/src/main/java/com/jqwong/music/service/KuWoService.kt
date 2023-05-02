package com.jqwong.music.service

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jqwong.music.api.KuWo
import com.jqwong.music.model.*
import com.jqwong.music.service.interceptor.KuWoInterceptor
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author: Jq
 * @date: 4/29/2023
 */
class KuWoService {
    // [kuwo music api document](https://qiuyaohong.github.io/kuwoMusicApi/#/)

    companion object{
        val h = Retrofit.Builder().baseUrl("https://www.kuwo.cn/api/www/")
            .client(OkHttpClient.Builder().addInterceptor(KuWoInterceptor()).build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(KuWo::class.java)

        val c = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .build()
    }


    /**
     * 获取热搜
     * @param call Call<List<String>>
     */
    fun getHotSearch(call: Call<List<String>>){
        h.getHotSearch()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:Observer<BaseMusicData<List<String>>>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    call.error(e)
                }

                override fun onComplete() {
                }

                override fun onNext(t: BaseMusicData<List<String>>) {
                    call.success(t.data)
                }
            })
    }

    /**
     * 获取搜索结果
     * @param call Call<List<Media>>
     */
    fun getSearchResult(key:String,page:Int,size:Int,call:Call<List<Media>>){
        h.getSearchResult(key, page, size)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:Observer<BaseMusicData<MediaList>>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    call.error(e)
                }

                override fun onComplete() {
                }

                override fun onNext(t: BaseMusicData<MediaList>) {
                    call.success(t.data.list)
                }
            })
    }


    /**
     * 加载歌手音乐
     * @param artistId String
     * @param page Int
     * @param size Int
     * @param call Call<List<Media>>
     */
    fun getArtistMusicList(artistId:String,page:Int,size:Int,call:Call<List<Media>>){
        h.getArtistMusicList(artistId, page, size)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:Observer<BaseMusicData<MediaList>>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    call.error(e)
                }

                override fun onComplete() {
                }

                override fun onNext(t: BaseMusicData<MediaList>) {
                    call.success(t.data.list)
                }
            })
    }


    /**
     * 加载歌手信息
     * @param artistId String
     * @param call Call<ArtistInfo>
     */
    fun getArtistInfo(artistId: String,call:Call<ArtistInfo>){
        h.getArtistInfo(artistId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:Observer<BaseMusicData<ArtistInfo>>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    call.error(e)
                }

                override fun onComplete() {
                }

                override fun onNext(t: BaseMusicData<ArtistInfo>) {
                    call.success(t.data)
                }
            })
    }


    /**
     * 获取排行榜
     * @param call Call<BillboardList>
     */
    fun getBillboardList(call:Call<List<BillboardList>>){
        h.getBillboardList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:Observer<BaseMusicData<List<BillboardList>>>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    call.error(e)
                }

                override fun onComplete() {
                }

                override fun onNext(t: BaseMusicData<List<BillboardList>>) {
                    call.success(t.data)
                }

            })
    }


    /**
     * 获取榜单音乐
     * @param sourceId String
     * @param page Int
     * @param size Int
     * @param call Call<List<Media>>
     */
    fun getBillboardMusicList(sourceId:String,page:Int,size:Int,call:Call<List<Media>>){
        h.getBillboardMusicList(sourceId, page, size)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:Observer<BaseMusicData<BillboardMediaList>>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    call.error(e)
                }

                override fun onComplete() {
                }

                override fun onNext(t: BaseMusicData<BillboardMediaList>) {
                    if(t == null || t.data == null || t.data.musicList == null){
                        call.success(listOf())
                        return
                    }
                    call.success(t.data.musicList)
                }

            })
    }


    /**
     * 获取播放地址
     * @param rid String
     * @param call Call<String>
     */
    fun getPlayUrl(rid:String,call:Call<String>){
        val url = "http://www.kuwo.cn/api/v1/www/music/playUrl?mid=$rid&type=convert_url3"
        val observable = Observable.create<String> {
            try {
                val req = Request.Builder().url(url).build()
                val res = c.newCall(req).execute()
                val json = res.body()!!.string()
                val resultType = object : TypeToken<BaseMusicData<PlayUrl>>() {}.type
                val data = Gson().fromJson<BaseMusicData<PlayUrl>>(json,resultType)
                it.onNext(data.data.url)
                it.onComplete()
            }
            catch (e:java.lang.Exception){
                it.onError(e)
            }
        }
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:Observer<String>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    call.error(e)
                }

                override fun onComplete() {
                }

                override fun onNext(t: String) {
                    call.success(t)
                }
            })
    }


    /**
     * 获取歌词
     * @param rid String
     * @param call Call<Lyric>
     */
    fun getLyric(rid: String,call:Call<Lyric>){
        val url = "http://m.kuwo.cn/newh5/singles/songinfoandlrc?musicId=${rid}&httpsStatus=1&reqId=f9204c10-1df1-11ec-8b4f-9f163660962a"
        val observable = Observable.create<Lyric> {
            val req = Request.Builder().url(url).build()
            try {
                val res = c.newCall(req).execute()
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
                it.onError(e)
            }
        }

        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:Observer<Lyric>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    call.error(e)
                }

                override fun onComplete() {
                }

                override fun onNext(t: Lyric) {
                    call.success(t)
                }

            })
    }
}