package com.jqwong.music.service

import android.util.Log
import com.jqwong.music.api.MemfireDB
import com.jqwong.music.entity.*
import com.jqwong.music.model.ArtistInfo
import com.jqwong.music.model.Call
import com.jqwong.music.service.factory.NullOnEmptyConverterFactory
import com.jqwong.music.service.interceptor.MemfireDBInterceptor
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author: Jq
 * @date: 4/29/2023
 */
class MemfireDBService {
    private val TAG = "MemfireDBService"

    // [postgrest](https://postgrest.org/en/stable/api.html)

    companion object{
        val h = Retrofit.Builder().baseUrl("https://cf937oi5g6h66drd9h1g.baseapi.memfiredb.com/rest/v1/")
            .client(OkHttpClient.Builder().addInterceptor(MemfireDBInterceptor()).build())
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(MemfireDB::class.java)
    }

    fun <T> callObservable(observable: Observable<T>,call:Call<T>){
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:Observer<T>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    call.error(e)
                }

                override fun onComplete() {
                }

                override fun onNext(t: T) {
                    if(t != null)
                        call.success(t)
                    else
                        call.error(Exception("not found data"))
                }
            })
    }
    fun callObservable2(observable: Observable<Response<Void>>,call:Call<Boolean>){
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:Observer<Response<Void>>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    call.error(e)
                }

                override fun onComplete() {
                }

                override fun onNext(t: Response<Void>) {
                    call.success(true)
                }
            })
    }

    /**
     * 根据用户名查询用户
     * @param userName String
     */
    fun queryUserByName(userName:String, call: Call<User?>){
        val inName = "eq.${userName}"
        callObservable(h.queryUserByName(inName),object:Call<List<User>>{
            override fun success(data: List<User>) {
                data.forEach {
                    if (!it.Delete){
                        call.success(it)
                        return
                    }
                }
                call.success(null)
            }

            override fun error(e: Throwable) {
                call.error(e)
            }

        })
    }


    /**
     * 添加用户
     * @param user User
     */
    fun insertUser(user:User,call:Call<Boolean>){
        callObservable2(h.insertUser(user),call)
    }


    /**
     * 根据用户UUID获取自建歌单
     * @param uuid String
     * @param call Call<List<Sheet>>
     */
    fun querySheetByUserUUID(uuid:String,call:Call<List<Sheet>>){
        val inUUID = "eq.${uuid}"
        callObservable(h.querySheetByUserUUID(inUUID),call)
    }


    /**
     * 添加歌单
     * @param sheet Sheet
     * @param call Call<Boolean>
     */
    fun insertSheet(sheet: Sheet,call: Call<Boolean>){
        callObservable2(h.insertSheet(sheet),call)
    }


    /**
     * 修改歌单信息
     * @param sheet Sheet
     * @param call Call<Boolean>
     */
    fun updateSheet(sheet: Sheet,call: Call<Boolean>){
        callObservable2(h.updateSheet(sheet),call)
    }


    /**
     * 删除歌单
     * @param sheet Sheet
     * @param call Call<Boolean>
     */
    fun deleteSheet(sheet: Sheet,call:Call<Boolean>){
        val inToken = "eq.${sheet.token}"
        val inUser = "eq.${sheet.UserUUID}"
        callObservable2(h.deleteSheet(inToken,inUser),call)
    }


    /**
     * 删除歌单所有歌曲
     * @param sheetToken String
     * @param call Call<Boolean>
     */
    fun deleteSheetInfo(sheetToken:String,call:Call<Boolean>){
        val inToken = "in.(${sheetToken})"
        callObservable2(h.deleteSheetInfo(inToken),call)
    }

    /**
     * 删除歌单所选歌曲
     * @param rid String
     * @param userUUID String
     * @param call Call<Boolean>
     */
    fun deleteSheetInfo(rid: String,userUUID: String,call: Call<Boolean>){
        val inRid = "in.(${rid})"
        val inUser = "eq.${userUUID}"
        callObservable2( h.deleteSheetInfo(inRid,inUser),call)
    }


    /**
     * 查询喜爱音乐
     * @param userUUID String
     * @param rid String
     * @param call Call<List<FavoriteMedia>>
     */
    fun queryFavoriteMedia(userUUID:String,rid:String,call:Call<List<FavoriteMedia>>){
        val inUserUUID = "eq.${userUUID}"
        val inRid = "eq.${rid}"
        callObservable(h.queryFavoriteMedia(inUserUUID, inRid),call)
    }


    /**
     * 查询所有喜爱音乐
     * @param userUUID String
     * @param call Call<List<FavoriteMedia>>
     */
    fun queryFavoriteMedia(userUUID: String,call: Call<List<FavoriteMedia>>){
        callObservable(h.queryFavoriteMedia("eq.${userUUID}"),call)
    }


    /**
     * 添加喜爱音乐
     * @param media FavoriteMedia
     * @param call Call<Boolean>
     */
    fun insertFavoriteMedia(media:FavoriteMedia,call:Call<Boolean>){
        val callQuery = object:Call<List<FavoriteMedia>>{
            override fun success(data: List<FavoriteMedia>) {
                if(data.isEmpty()){
                    callObservable2(h.insertFavoriteMedia(media),call)
                }
                else{
                    call.success(true)
                }
            }

            override fun error(e: Throwable) {
                call.error(e)
            }
        }
        queryFavoriteMedia(media.UserUUID,media.rid.toString(),callQuery)
    }


    /**
     * 根绝Rid查询音乐
     * @param rid String
     * @param call Call<Media>
     */
    fun queryMediasByRid(rid:String, call:Call<List<Media>>){
        val inRid = "in.(${rid})"
        callObservable(h.queryMediasByRid(inRid),call)
    }


    /**
     * 插入音乐
     * @param media Media
     * @param call Call<Boolean>
     */
    fun insertMedia(media:Media,call:Call<Boolean>){
        val callQuery = object:Call<List<Media>>{
            override fun success(data: List<Media>) {
                if(data == null || data.size == 0 || data.isEmpty()){
                    callObservable2(h.insertMedia(media),call)
                }
                else{
                    call.success(true)
                }
                call.success(true)
            }

            override fun error(e: Throwable) {
                call.error(e)
            }
        }
        queryMediasByRid(media.Rid.toString(),callQuery)
    }


    /**
     * 删除喜爱音乐
     * @param userUUID String
     * @param rid String
     * @param call Call<Boolean>
     */
    fun deleteFavoriteMedia(userUUID: String,rid: String,call: Call<Boolean>){
        val inUserUUID = "eq.${userUUID}"
        val inRid = "eq.${rid}"
        callObservable2(h.deleteFavoriteMedia(inRid,inUserUUID),call)
    }


    /**
     * 添加歌单歌曲
     * @param sheetInfo SheetInfo
     * @param call Call<Boolean>
     */
    fun insertSheetInfo(sheetInfo: SheetInfo,call: Call<Boolean>){
        callObservable2(h.insertSheetInfo(sheetInfo),call)
    }


    /**
     * 添加喜爱歌手
     * @param userUUID String
     * @param artistInfo ArtistInfo
     * @param call Call<Boolean>
     */
    fun insertFavoriteArtist(userUUID: String,artistInfo: ArtistInfo,call:Call<Boolean>){
        /**
         * first to query exit favorite
         * if not exit, then insert artist (check exit artist)
         *              and insert favorite artist
         */

        val callInsertFavorite = object:Call<Boolean>{
            override fun success(data: Boolean) {
                insertFavoriteArtist(artistInfo.id,userUUID,object:Call<Boolean>{
                    override fun success(data: Boolean) {
                        call.success(true)
                    }

                    override fun error(e: Throwable) {
                        call.error(e)
                    }

                })
            }

            override fun error(e: Throwable) {
                call.error(e)
            }

        }
        val callQueryExitArtist = object:Call<List<Artist>>{
            override fun success(data: List<Artist>) {
                if(data.isEmpty()){
                    insertArtist(artistInfo,callInsertFavorite)
                }
                else{
                    callInsertFavorite.success(true)
                }
            }

            override fun error(e: Throwable) {
                call.error(e)
            }

        }
        val callQueryExitFavorite = object:Call<List<FavoriteArtist2>>{
            override fun success(data: List<FavoriteArtist2>) {
                if(data.isEmpty()){
                    queryAristById(artistInfo.id.toString(),callQueryExitArtist)
                }
                else{
                    call.success(true)
                }
            }

            override fun error(e: Throwable) {
                call.error(e)
            }

        }
        queryFavoriteAritsts(userUUID,artistInfo.id,callQueryExitFavorite)
    }


    /**
     * 查询歌手信息
     * @param artistId String
     * @param call Call<List<Artist>>
     */
    fun queryAristById(artistId:String,call:Call<List<Artist>>){
        callObservable(h.queryArtistById("in.(${artistId})"),call)
    }


    /**
     * 添加歌手
     * @param artistInfo ArtistInfo
     * @param call Call<Boolean>
     */
    private fun insertArtist(artistInfo: ArtistInfo,call: Call<Boolean>){
        callObservable2(h.insertArtist(Artist(null,artistInfo.id,artistInfo.name,artistInfo.pic70)),call)
    }


    /**
     * 查询喜爱歌手
     * @param userUUID String
     * @param artistId String
     * @param call Call<List<FavoriteArtist2>>
     */
    fun queryFavoriteAritsts(userUUID: String,artistId: Long,call:Call<List<FavoriteArtist2>>){
        val inUserUUID = "eq.${userUUID}"
        val inArtistId = "in.(${artistId})"
        callObservable(h.queryFavoriteArtists(inArtistId,inUserUUID),call)
    }


    /**
     * 查询用户所有喜爱歌手
     * @param userUUID String
     * @param call Call<List<FavoriteArtist2>>
     */
    fun queryFavoriteArtists(userUUID: String,call:Call<List<FavoriteArtist2>>){
        callObservable(h.queryFavoriteArtists("eq.${userUUID}"),call)
    }


    /**
     * 添加喜爱歌手
     * @param artistId Long
     * @param userUUID String
     * @param call Call<Boolean>
     */
    private fun insertFavoriteArtist(artistId: Long,userUUID: String,call:Call<Boolean>){
        callObservable2(h.insertFavoriteArtist(FavoriteArtist2(null,artistId,userUUID)),call)
    }


    /**
     * 删除喜爱音乐
     * @param artistInfo ArtistInfo
     * @param call Call<Boolean>
     */
    fun deleteFavoriteArtist(artistId: Long,userUUID: String,call: Call<Boolean>){
        val inUser = "eq.${userUUID}"
        val inId = "in.(${artistId})"
        callObservable2(h.deleteFavoriteArtist(inId,inUser),call)
    }


    /**
     * 获取用户歌单信息
     * @param userUUID String
     * @param call Call<List<SheetInfo>>
     */
    fun querySheetInfoByUserUUID(userUUID: String,call:Call<List<SheetInfo>>){
        callObservable(h.querySheetInfoByUserUUID("eq.${userUUID}"),call)
    }
}