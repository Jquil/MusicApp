package com.jqwong.music.service

import com.jqwong.music.app.App
import com.jqwong.music.model.ArtistInfo
import com.jqwong.music.model.Media
import com.jqwong.music.repository.ArtistRepository
import com.jqwong.music.repository.FavoriteRepository
import com.jqwong.music.repository.SheetInfoRepository
import com.jqwong.music.repository.entity.Artist
import com.jqwong.music.repository.entity.Favorite
import com.jqwong.music.repository.entity.Sheet
import com.jqwong.music.repository.entity.SheetInfo

class CollectService {

    private val _FavoriteRepo:FavoriteRepository
    private val _SheetInfoRepo:SheetInfoRepository
    private val _ArtistRepo:ArtistRepository


    init {
        _SheetInfoRepo = SheetInfoRepository()
        _FavoriteRepo = FavoriteRepository()
        _ArtistRepo = ArtistRepository()
    }


    fun Favorite(userId:Long,media:Media){
        val media = com.jqwong.music.repository.entity.Media(
            null,
            media.url,
            media.album,
            media.artist,
            media.name,
            media.rid,
            media.pic,
            media.time,
            media.strTime,
            media.artistid
        )
        val exitItem = _FavoriteRepo.Exit(userId,media)
        if(exitItem == null){
            _FavoriteRepo.Add(userId,media)
            RemoteService().Favorite(userId, media)
        }
    }


    fun LoadFavoriteList(userId: Long,page:Int,itemSize:Int):List<com.jqwong.music.repository.entity.Media>{
        return _FavoriteRepo.Load(userId, page, itemSize)
    }


    fun Collect(userId: Long,sheet:Sheet,media: Media){
        val inMedia = com.jqwong.music.repository.entity.Media(
            null,
            media.url,
            media.album,
            media.artist,
            media.name,
            media.rid,
            media.pic,
            media.time,
            media.strTime,
            media.artistid
        )

        val exitItem = _SheetInfoRepo.Exit(userId,sheet)
        if(exitItem == null){
            _SheetInfoRepo.Add(userId,sheet,inMedia)
            RemoteService().Collect(SheetInfo(null,userId,media.rid.toLong(),sheet.token,false),inMedia)
        }
    }


    fun LoadSheetMediaList(userId:Long, sheetToken:String, page:Int, itemSize:Int):List<com.jqwong.music.repository.entity.Media>{
        return _SheetInfoRepo.Load(userId, sheetToken,page,itemSize)
    }


    fun DeleteOfSheetMedia(userId:Long,sheetToken: String,rid:Int){
        _SheetInfoRepo.Delete(userId,sheetToken,rid)
        RemoteService().DeleteOfCollect(SheetInfo(null,userId,rid.toLong(),sheetToken,false))
    }


    fun DeleteOfFavorite(userId: Long,rid: Int){
        _FavoriteRepo.Delete(userId, rid)
        RemoteService().DeleteOfFavorite(userId,rid.toLong())
    }


    fun DeleteAllOfSheetMedia(userId: Long,sheetToken: String){
        _SheetInfoRepo.DeleteAll(userId, sheetToken)
    }


    fun Artist(userId: Long,artistInfo: ArtistInfo){
        val artist = com.jqwong.music.repository.entity.Artist(null,userId,artistInfo.id.toLong(),artistInfo.name,artistInfo.pic70)
        _ArtistRepo.Add(artist)
        RemoteService().AddArtist(artist)
    }


    fun LoadArtistList(userId:Long):List<ArtistInfo>{
        val list = _ArtistRepo.Load(userId)
        var data = mutableListOf<ArtistInfo>()
        list.forEach {
            data.add(ArtistInfo(it.name,it.pic,it.artistId))
        }
        return data
    }


    fun DeleteOfArtist(userId: Long,artistId:Long){
        _ArtistRepo.Delete(userId, artistId)
        RemoteService().DeleteArtist(userId, artistId)
    }


    fun SyncMedia(list:List<com.jqwong.music.repository.entity.Media>){
        val dao = App.mSession.mediaDao
        dao.deleteAll()
        list.forEach {
            var item = it
            item.id = null
            dao.insert(item)
        }
    }


    fun SyncSheetInfo(userId: Long, list:List<SheetInfo>){
        _SheetInfoRepo.Sync(userId,list)
    }


    fun SyncArtist(userId: Long,list:List<Artist>){
        _ArtistRepo.Sync(userId, list)
    }


    fun SyncFavorite(userId: Long,list:List<Favorite>){
        _FavoriteRepo.Sync(userId,list)
    }

}