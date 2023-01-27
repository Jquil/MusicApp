package com.jqwong.music.service

import android.util.Log
import com.jqwong.music.api.service.HttpCall
import com.jqwong.music.api.service.MusicApiService
import com.jqwong.music.api.service.RemoteApiService
import com.jqwong.music.model.ArtistInfo
import com.jqwong.music.repository.entity.Artist
import com.jqwong.music.repository.entity.Favorite
import com.jqwong.music.repository.entity.Media
import com.jqwong.music.repository.entity.Sheet
import com.jqwong.music.repository.entity.SheetInfo
import com.jqwong.music.repository.entity.User
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class RemoteService {

    private val TAG = "RemoteService"


    fun Login(name:String,pass:String,call:HttpCall<RemoteResult<User>>){
        var inName = "eq.${name}"
        //var inPass = "eq.${pass}"
        //Log.e(TAG,"${inName}--${inPass}")
        var result = RemoteResult<User>(RemoteResultCode.Failed,null,"")
        RemoteApiService.h.QueryUserOfName(inName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:MusicApiService.BaseObserver<List<User>>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: List<User>) {
                    /**
                     * first to match user
                     * if not exit user then to register
                     */
                    var isExitUser = false
                    t.forEach {
                        if(it.Name == name)
                            isExitUser = true

                        Log.e(TAG,"${it.Name} -- $name -- ${it.Name == name}")

                        if(it.Name == name && it.Password == pass && !it.Delete){
                            result.code = RemoteResultCode.OK
                            result.data = it
                            result.desc = "Login success"
                            return@forEach
                        }
                    }

                    if(isExitUser && result.code == RemoteResultCode.Failed){
                        result.desc = "Sorry, your password is error"
                    }

                    if (!isExitUser){
                        try {
                            RemoteApiService.h.Register(User(null,name,pass,false))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object:MusicApiService.BaseObserver<Response<Void>>{
                                    override fun onSubscribe(d: Disposable) {

                                    }

                                    override fun onNext(t: Response<Void>) {
                                        Login(name,pass,call)
                                    }

                                    override fun onError(e: Throwable) {
                                        call.onError(e)
                                    }

                                    override fun onComplete() {
                                    }
                                })
                        }
                        catch (e:java.lang.Exception){
                            call.onError(e)
                        }
                    }
                    else{
                        call.OnSuccess(result)
                    }
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG,e.message.toString())
                    call.onError(e)
                }

                override fun onComplete() {

                }
            })
    }


    fun AddSheet(sheet: Sheet){
        RemoteApiService.h.AddSheet(sheet)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }


    fun UpdateSheet(sheet: Sheet){
        val inToken = "eq.${sheet.Token}"
        RemoteApiService.h.QuerySheetOfToken(inToken)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object:MusicApiService.BaseObserver<List<Sheet>>{
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: List<Sheet>) {
                        if(t.size != 0){
                            var item = t.get(0)
                            item.Delete = sheet.Delete
                            item.name = sheet.Name
                            RemoteApiService.h.UpdateSheet(sheet)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe()
                        }
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {

                    }
                }
            )
    }



    fun AddArtist(artist:Artist){
        RemoteApiService.h.AddArtist(artist)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }


    fun DeleteArtist(userId: Long,artistId:Long){
        val inUserId = "eq.${userId}"
        RemoteApiService.h.QueryAristOfUserId(inUserId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:MusicApiService.BaseObserver<List<Artist>>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: List<Artist>) {
                    t.forEach {
                        if(it.ArtistId == artistId){
                            val inArtistId = "eq.${it.artistId}"
                            RemoteApiService.h.DeleteArtist(inUserId,inArtistId)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe()
                            return@forEach
                        }
                    }
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }
            })
    }


    fun AddMedia(media: Media){
        val inRid = "eq.${media.rid}"
        RemoteApiService.h.QueryMediaOfRid(inRid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:MusicApiService.BaseObserver<List<Media>>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: List<Media>) {
                    if(t.size == 0){
                        var inMedia = media
                        inMedia.Id = null
                        RemoteApiService.h.AddMedia(inMedia)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe()
                    }


                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }
            })
    }


    fun Favorite(userId: Long,media: Media){
        val favorite = Favorite(
            null,userId,media.rid.toLong(),false
        )
        RemoteApiService.h.AddFavorite(favorite)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
        AddMedia(media)
    }


    fun DeleteOfFavorite(userId: Long,rid:Long){
        val inUserId = "eq.$userId"
        val inRid = "eq.$rid"
        RemoteApiService.h.DeleteFavorite(inUserId,inRid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }


    fun Collect(sheetInfo: SheetInfo,media: Media){
        var item = sheetInfo
        item.Id = null
        RemoteApiService.h.AddSheetInfo(sheetInfo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
        AddMedia(media)
    }


    fun DeleteOfCollect(sheetInfo: SheetInfo){
        val inUserId = "eq.${sheetInfo.UserId}"
        val inRid = "eq.${sheetInfo.rid}"
        val inToken = "eq.${sheetInfo.SheetToken}"
        RemoteApiService.h.QuerySheetInfo(inUserId,inToken,inRid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:MusicApiService.BaseObserver<List<SheetInfo>>{
                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: List<SheetInfo>) {
                    if(t.size != 0){
                        var item = t.get(0)
                        item.delete = true
                        RemoteApiService.h.UpdateSheetInfo(sheetInfo)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe()
                    }
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }
            })
    }


    fun SyncArtist(userId: Long,call:HttpCall<Boolean>){
        val inUserId = "eq.${userId}"
        val service = CollectService()
        RemoteApiService.h.QueryAristOfUserId(inUserId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:MusicApiService.BaseObserver<List<Artist>>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onError(e: Throwable) {
                    call.onError(e)
                }

                override fun onComplete() {
                }

                override fun onNext(t: List<Artist>) {
                    service.SyncArtist(userId,t)
                    call.OnSuccess(true)
                }
            })
    }


    fun SyncSheet(userId: Long,call:HttpCall<Boolean>){
        val service = SheetService()
        val dataOfDB = service.Load(userId)
        val inUserId = "eq.${userId}"
        RemoteApiService.h.QuerySheetOfUserId(inUserId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:MusicApiService.BaseObserver<List<Sheet>>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: List<Sheet>) {
                    t.forEach {
                        val token = it.Token
                        if(it.token != null){
                            var exit = false
                            dataOfDB.forEach {
                                if (it.token == token){
                                    exit = true
                                    return@forEach
                                }
                            }
                            if(!exit && !it.Delete){
                                service.Add(it.Name,it.UserId,it.Token,syncToRemote = false)
                            }
                        }
                    }
                    call.OnSuccess(true)
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }
            })
    }


    fun SyncFavorite(userId:Long,call:HttpCall<Boolean>){
        val inUserId = "eq.$userId"
        val service = CollectService()
        RemoteApiService.h.QueryFavoriteOfUserId(inUserId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:MusicApiService.BaseObserver<List<Favorite>>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: List<Favorite>) {
                    service.SyncFavorite(userId,t)
                    call.OnSuccess(true)
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }
            })
    }


    fun SyncSheetInfo(userId: Long,call: HttpCall<Boolean>){
        val inUserId = "eq.$userId"
        RemoteApiService.h.QuerySheetInfo(inUserId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:MusicApiService.BaseObserver<List<SheetInfo>>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: List<SheetInfo>) {
                    CollectService().SyncSheetInfo(userId, t)
                    call.OnSuccess(true)
                }

                override fun onError(e: Throwable) {
                    call.onError(e)
                }

                override fun onComplete() {
                }
            })
    }


    fun SyncMedia(call: HttpCall<Boolean>){
        val service = CollectService()
        RemoteApiService.h.QueryMedia()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object:MusicApiService.BaseObserver<List<Media>>{
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: List<Media>) {
                    service.SyncMedia(t)
                    call.OnSuccess(true)
                }

                override fun onError(e: Throwable) {
                    call.onError(e)
                }

                override fun onComplete() {
                }
            })

    }


    class RemoteResult<T>(var code:RemoteResultCode,var data:T?,var desc:String)


    enum class RemoteResultCode{
        OK,
        Failed
    }
}