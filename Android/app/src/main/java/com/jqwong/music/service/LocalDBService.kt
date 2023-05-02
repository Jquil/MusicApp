package com.jqwong.music.service

import android.util.Log
import com.jqwong.music.dal.*
import com.jqwong.music.entity.*
import com.jqwong.music.model.ArtistInfo
import com.jqwong.music.model.BillboardList
import com.jqwong.music.model.Call
import com.jqwong.music.utils.CommonUtil
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

/**
 * @author: Jq
 * @date: 4/30/2023
 */
class LocalDBService {

    val TAG = "LocalDBService"

    val map = HashMap<String,Any>()

    init {
        map.put(CommonUtil.getName<Billboard>(),BillboardDal())
        map.put(CommonUtil.getName<SearchRecord>(),SearchDal())
        map.put(CommonUtil.getName<FavoriteArtist>(),FavoriteArtistDal())
        map.put(CommonUtil.getName<FavoriteMedia>(),FavoriteMediaDal())
        map.put(CommonUtil.getName<HotSearchRecord>(),HotSearchDal())
        map.put(CommonUtil.getName<Media>(),MediaDal())
        map.put(CommonUtil.getName<SearchRecord>(),SearchDal())
        map.put(CommonUtil.getName<Sheet>(),SheetDal())
        map.put(CommonUtil.getName<SheetInfo>(),SheetInfoDal())
    }


    inline fun <reified T:Any>insert(data:T,call:Call<Boolean>){
        try {
            val name = CommonUtil.getName<T>()
            val dal = map.get(name) as BaseDal<T,Long>
            Observable.create<Boolean> {
                try {
                    dal.insert(data)
                    it.onNext(true)
                }
                catch (e:java.lang.Exception){
                    it.onError(e)
                }
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object:Observer<Boolean>{
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        call.error(e)
                    }

                    override fun onComplete() {

                    }

                    override fun onNext(t: Boolean) {
                        call.success(t)
                    }
                })
        }
        catch (e:Exception){
            call.error(e)
        }
    }
    inline fun <reified T:Any>insert(data:List<T>,call:Call<Boolean>){
        try {
            val name = CommonUtil.getName<T>()
            val dal = map.get(name) as BaseDal<T,Long>
            Observable.create<Boolean> {
                try {
                    dal.insertInTx(data)
                    it.onNext(true)
                }
                catch (e:java.lang.Exception){
                    it.onError(e)
                }
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object:Observer<Boolean>{
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        call.error(e)
                    }

                    override fun onComplete() {

                    }

                    override fun onNext(t: Boolean) {
                        call.success(t)
                    }
                })
        }
        catch (e:Exception){
            call.error(e)
        }
    }
    inline fun <reified T:Any>delete(data:T,call:Call<Boolean>){
        try {
            val name = CommonUtil.getName<T>()
            val dal = map.get(name) as BaseDal<T,Long>
            Observable.create<Boolean> {
                try {
                    dal.delete(data)
                    it.onNext(true)
                }
                catch (e:java.lang.Exception){
                    it.onError(e)
                }
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object:Observer<Boolean>{
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        call.error(e)
                    }

                    override fun onComplete() {

                    }

                    override fun onNext(t: Boolean) {
                        call.success(t)
                    }
                })
        }
        catch (e:Exception){
            call.error(e)
        }
    }
    inline fun <reified T:Any>deleteAll(call:Call<Boolean>){
        try {
            val name = CommonUtil.getName<T>()
            val dal = map.get(name) as BaseDal<T,Long>
            Observable.create<Boolean> {
                try {
                    dal.deleteAll()
                    it.onNext(true)
                }
                catch (e:java.lang.Exception){
                    it.onError(e)
                }
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object:Observer<Boolean>{
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        call.error(e)
                    }

                    override fun onComplete() {

                    }

                    override fun onNext(t: Boolean) {
                        call.success(t)
                    }
                })
        }
        catch (e:Exception){
            call.error(e)
        }
    }
    inline fun <reified T:Any>update(data:T,call:Call<Boolean>){
        try {
            val name = CommonUtil.getName<T>()
            val dal = map.get(name) as BaseDal<T,Long>
            Observable.create<Boolean> {
                try {
                    dal.update(data)
                    it.onNext(true)
                }
                catch (e:java.lang.Exception){
                    it.onError(e)
                }
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object:Observer<Boolean>{
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        call.error(e)
                    }

                    override fun onComplete() {

                    }

                    override fun onNext(t: Boolean) {
                        call.success(t)
                    }
                })
        }
        catch (e:Exception){
            call.error(e)
        }
    }
    inline fun <reified T:Any>loadAll(call:Call<List<T>>){
        try {
            val name = CommonUtil.getName<T>()
            val dal = map.get(name) as BaseDal<T,Long>
            val observable = Observable.create<List<T>>{
                try {
                    var data = dal.loadAll()
                    if(data == null){
                        data = listOf()
                    }
                    it.onNext(data)
                }
                catch (e:java.lang.Exception){
                    it.onError(e)
                }
            }
            observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object:Observer<List<T>>{
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        call.error(e)
                    }

                    override fun onComplete() {

                    }

                    override fun onNext(t: List<T>) {
                        call.success(t)
                    }

                })
        }
        catch (e:Exception){
            call.error(e)
        }
    }

    /**
     * 查询榜单
     * @param call Call<List<BillboardList>>
     */
    fun queryBillBoards(call: Call<List<BillboardList>>){
        val observable = Observable.create<List<BillboardList>>{
            try {
                val dal = map.get(CommonUtil.getName<Billboard>()) as BillboardDal
                var data = dal.query()
                if(data == null){
                    data = listOf()
                }
                it.onNext(data)
            }
            catch (e:java.lang.Exception){
                it.onError(e)
            }
        }
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:Observer<List<BillboardList>>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    call.error(e)
                }

                override fun onComplete() {

                }

                override fun onNext(t: List<BillboardList>) {
                    call.success(t)
                }

            })
    }


    /**
     * 插入榜单
     * @param t List<BillboardList>
     */
    fun insertBillBoards(t:List<BillboardList>, call:Call<Boolean>){
        val observable = Observable.create<Boolean> {
            try {
                val dal = map.get(CommonUtil.getName<Billboard>()) as BillboardDal
                dal.insert(t)
                it.onNext(true)
            }
            catch (e:java.lang.Exception){
                it.onError(e)
            }

        }
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:Observer<Boolean>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    call.error(e)
                }

                override fun onComplete() {

                }

                override fun onNext(t: Boolean) {
                    call.success(t)
                }

            })
    }


    /**
     * 获取搜索记录
     * @param limit Int
     * @param call Call<List<SearchRecord>>
     */
    fun querySearchRecord(limit:Int,call:Call<List<SearchRecord>>){
        val observable = Observable.create<List<SearchRecord>>{
            try {
                val dal = map.get(CommonUtil.getName<SearchRecord>()) as SearchDal
                var data = dal.query(limit)
                if(data == null){
                    data = listOf()
                }
                it.onNext(data)
            }
            catch (e:java.lang.Exception){
                it.onError(e)
            }
        }
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:Observer<List<SearchRecord>>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    call.error(e)
                }

                override fun onComplete() {

                }

                override fun onNext(t: List<SearchRecord>) {
                    call.success(t)
                }

            })
    }


    /**
     * 获取喜爱的音乐
     * @param userUUD String
     * @param rid String
     * @param call Call<List<FavoriteMedia>>
     */
    fun queryFavoriteMedia(userUUD:String,rid:String,call:Call<List<FavoriteMedia>>){
        val observable = Observable.create<List<FavoriteMedia>>{
            try {
                val dal = map.get(CommonUtil.getName<FavoriteMedia>()) as FavoriteMediaDal
                var data = dal.query(userUUD,rid)
                if(data == null){
                    data = listOf()
                }
                it.onNext(data)
            }
            catch (e:java.lang.Exception){
                it.onError(e)
            }
        }
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:Observer<List<FavoriteMedia>>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    call.error(e)
                }

                override fun onComplete() {

                }

                override fun onNext(t: List<FavoriteMedia>) {
                    call.success(t)
                }

            })
    }


    /**
     * 添加喜爱音乐
     * @param favoriteMedia FavoriteMedia
     * @param call Call<Boolean>
     */
    fun insertFavoriteMedia(favoriteMedia: FavoriteMedia,call: Call<Boolean>){
        val callQuery = object:Call<List<FavoriteMedia>>{
            override fun success(data: List<FavoriteMedia>) {
                if(data.size == 0){
                    val observable = Observable.create<Boolean> {
                        try {
                            val dal = map.get(CommonUtil.getName<FavoriteMedia>()) as FavoriteMediaDal
                            dal.insert(favoriteMedia)
                            it.onNext(true)
                        }
                        catch (e:java.lang.Exception){
                            it.onError(e)
                        }

                    }
                    observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object:Observer<Boolean>{
                            override fun onSubscribe(d: Disposable) {

                            }

                            override fun onError(e: Throwable) {
                                call.error(e)
                            }

                            override fun onComplete() {

                            }

                            override fun onNext(t: Boolean) {
                                call.success(t)
                            }

                        })
                }
                else{
                    call.success(true)
                }
            }

            override fun error(e: Throwable) {
                call.error(e)
            }

        }
        queryFavoriteMedia(favoriteMedia.UserUUID,favoriteMedia.rid.toString(),callQuery)
    }


    /**
     * 根绝Rid查询音乐
     * @param rid String
     * @param call Call<List<Media>>
     */
    fun queryMediaByRid(rid:String,call: Call<List<Media>>){
        val observable = Observable.create<List<Media>>{
            try {
                val dal = map.get(CommonUtil.getName<Media>()) as MediaDal
                var data = dal.queryByRid(rid)
                if(data == null){
                    data = listOf()
                }
                it.onNext(data)
            }
            catch (e:java.lang.Exception){
                it.onError(e)
            }
        }
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:Observer<List<Media>>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    call.error(e)
                }

                override fun onComplete() {

                }

                override fun onNext(t: List<Media>) {
                    call.success(t)
                }

            })
    }


    /**
     * 插入音乐
     * @param media Media
     * @param call Call<Boolean>
     */
    fun insertMedia(media: Media,call: Call<Boolean>){
        val callQuery = object:Call<List<Media>>{
            override fun success(data: List<Media>) {
                if(data.size == 0){
                    val observable = Observable.create<Boolean> {
                        try {
                            val dal = map.get(CommonUtil.getName<Media>()) as MediaDal
                            dal.insert(media)
                            it.onNext(true)
                        }
                        catch (e:java.lang.Exception){
                            it.onError(e)
                        }

                    }
                    observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object:Observer<Boolean>{
                            override fun onSubscribe(d: Disposable) {

                            }

                            override fun onError(e: Throwable) {
                                call.error(e)
                            }

                            override fun onComplete() {

                            }

                            override fun onNext(t: Boolean) {
                                call.success(t)
                            }

                        })
                }
                else{
                    call.success(true)
                }
            }

            override fun error(e: Throwable) {
                call.error(e)
            }

        }
        queryMediaByRid(media.rid.toString(),callQuery)
    }


    /**
     * 分页查询喜爱音乐
     * @param userUUID String
     * @param page Int
     * @param pageSize Int
     * @param call Call<List<Media>>
     */
    fun queryFavoriteMedias(userUUID:String,page:Int,pageSize:Int,call:Call<List<com.jqwong.music.model.Media>>){
        val observable = Observable.create<List<Media>>{
            try {
                val dal = map.get(CommonUtil.getName<FavoriteMedia>()) as FavoriteMediaDal
                var data = dal.queryMedias(userUUID, page, pageSize)
                if(data == null){
                    data = listOf()
                }
                it.onNext(data)
            }
            catch (e:java.lang.Exception){
                it.onError(e)
            }
        }
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:Observer<List<Media>>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    call.error(e)
                }

                override fun onComplete() {

                }

                override fun onNext(t: List<Media>) {
                    val list = mutableListOf<com.jqwong.music.model.Media>()
                    t.forEach {
                        list.add(com.jqwong.music.model.Media(it.musicRid,it.rid,it.artist,it.pic,it.album,it.albumId,it.name,it.pic120,it.songTimeMinutes,it.time,it.url,it.artistId))
                    }
                    call.success(list)
                }

            })
    }


    /**
     * 删除喜爱音乐
     * @param userUUID String
     * @param rid String
     * @param call Call<Boolean>
     */
    fun deleteFavoriteMedia(userUUID: String,rid:String,call: Call<Boolean>){
        val observable = Observable.create<Boolean> {
            try {
                val dal = map.get(CommonUtil.getName<FavoriteMedia>()) as FavoriteMediaDal
                dal.delete(userUUID, rid)
                it.onNext(true)
            }
            catch (e:java.lang.Exception){
                it.onError(e)
            }

        }
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:Observer<Boolean>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    call.error(e)
                }

                override fun onComplete() {

                }

                override fun onNext(t: Boolean) {
                    call.success(t)
                }

            })
    }


    /**
     * 加载歌单歌曲
     * @param token String
     * @param page Int
     * @param pageSize Int
     * @param call Call<List<Media>>
     */
    fun querySheetMedias(token:String,page:Int,pageSize: Int,call: Call<List<com.jqwong.music.model.Media>>){
        val observable = Observable.create<List<Media>>{
            try {
                val dal = map.get(CommonUtil.getName<SheetInfo>()) as SheetInfoDal
                var data = dal.queryMedias(token, page, pageSize)
                if(data == null){
                    data = listOf()
                }
                it.onNext(data)
            }
            catch (e:java.lang.Exception){
                it.onError(e)
            }
        }
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:Observer<List<Media>>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    call.error(e)
                }

                override fun onComplete() {

                }

                override fun onNext(t: List<Media>) {
                    val list = mutableListOf<com.jqwong.music.model.Media>()
                    t.forEach {
                        list.add(com.jqwong.music.model.Media(it.musicRid,it.rid,it.artist,it.pic,it.album,it.albumId,it.name,it.pic120,it.songTimeMinutes,it.time,it.url,it.artistId))
                    }
                    call.success(list)
                }

            })
    }


    /**
     * 插入喜爱歌手
     * @param artistInfo ArtistInfo
     * @param call Call<Boolean>
     */
    fun insertFavoriteArtist(artistInfo: ArtistInfo,call:Call<Boolean>){
        val entity = FavoriteArtist(null,artistInfo.name,artistInfo.pic70,artistInfo.id)
        val observable = Observable.create<Boolean> {
            try {
                val dal = map.get(CommonUtil.getName<FavoriteArtist>()) as FavoriteArtistDal
                val list = dal.queryByArtistId(artistInfo.id)
                if(list.isEmpty()){
                    dal.insert(entity)
                }
                it.onNext(true)
            }
            catch (e:java.lang.Exception){
                it.onError(e)
            }

        }
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:Observer<Boolean>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    call.error(e)
                }

                override fun onComplete() {

                }

                override fun onNext(t: Boolean) {
                    call.success(t)
                }

            })
    }


    /**
     * 删除歌单所有歌曲
     * @param token String
     * @param call Call<Boolean>
     */
    fun deleteSheetInfoByToken(token: String,call:Call<Boolean>){
        Observable.create<Boolean> {
            try{
                val dal = map.get(CommonUtil.getName<SheetInfo>()) as SheetInfoDal
                dal.deleteByToken(token)
                it.onNext(true)
            }
            catch (e:java.lang.Exception){
                it.onError(e)
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:Observer<Boolean>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    call.error(e)
                }

                override fun onComplete() {

                }

                override fun onNext(t: Boolean) {
                    call.success(t)
                }

            })

    }

    /**
     * 删除歌单歌曲
     * @param rid Int
     * @param userUUID String
     * @param call Call<Boolean>
     */
    fun deleteSheetInfo(rid:Int,userUUID: String,call:Call<Boolean>){
        Observable.create<Boolean> {
            try {
                val dal = map.get(CommonUtil.getName<SheetInfo>()) as SheetInfoDal
                dal.deleteByRidAndUserUUID(rid,userUUID)
                it.onNext(true)
            }
            catch (e:Exception){
                it.onError(e)
            }
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:Observer<Boolean>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    call.error(e)
                }

                override fun onComplete() {
                }

                override fun onNext(t: Boolean) {
                    call.success(t)
                }

            })
    }


    /**
     * 删除喜爱歌手
     * @param artistInfo ArtistInfo
     * @param call Call<Boolean>
     */
    fun deleteFavoriteArtist(artistInfo: ArtistInfo,call: Call<Boolean>){
        val observable = Observable.create<Boolean> {
            try {
                val dal = map.get(CommonUtil.getName<FavoriteArtist>()) as FavoriteArtistDal
                dal.delete(artistInfo)
                it.onNext(true)
            }
            catch (e:Exception){
                it.onError(e)
            }
        }
        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:Observer<Boolean>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    call.error(e)
                }

                override fun onComplete() {
                }

                override fun onNext(t: Boolean) {
                    call.success(t)
                }

            })
    }


    /**
     * 重置
     * @param call Call<Boolean>
     */
    fun reset(call: Call<Boolean>){
        Observable.create<Boolean> {
            try {
                map.forEach { s, any ->
                    (any as BaseDal<Any,Any>).deleteAll()
                }
                it.onNext(true)
            }
            catch (e:java.lang.Exception){
                it.onError(e)
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:Observer<Boolean>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    call.error(e)
                }

                override fun onComplete() {
                }

                override fun onNext(t: Boolean) {
                    call.success(t)
                }

            })
    }
}