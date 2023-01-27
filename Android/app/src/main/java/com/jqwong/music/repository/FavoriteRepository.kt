package com.jqwong.music.repository

import android.util.Log
import com.jqwong.music.app.App
import com.jqwong.music.repository.dao.FavoriteDao
import com.jqwong.music.repository.dao.FavoriteDao.Properties
import com.jqwong.music.repository.dao.MediaDao
import com.jqwong.music.repository.entity.Favorite
import com.jqwong.music.repository.entity.Media

class FavoriteRepository {

    private val TAG = "FavoriteRepository"

    private var _Dao: FavoriteDao

    private var _MediaDao:MediaDao

    init {
        _Dao = App.mSession.favoriteDao
        _MediaDao = App.mSession.mediaDao
    }


    fun Exit(userId: Long,media: Media):Favorite?{
        // then check query is exit favorite
        val data = _Dao.queryBuilder()
            .where(Properties.UserId.eq(userId),Properties.Rid.eq(media.rid))
            .build().list()
        if (data.size != 0)
            return data.get(0)
        else
            return null
    }


    fun Add(userId:Long,media:Media){
        val dataOfMedia = _MediaDao.queryBuilder()
            .where(MediaDao.Properties.Rid.eq(media.rid))
            .build().list()
        if(dataOfMedia.size == 0){
            _MediaDao.insert(media)
        }

        _Dao.insert(Favorite(null,userId,media.rid.toLong(),false))
    }


    fun Load(userId: Long,page:Int,itemSize:Int):List<Media>{

        /**
         * select media.*,favorite
         * where media.rid = favorite.rid
         * and favorite.userId = userId
         * and favorite.Delete = false
         * order by favorite.id desc
         * limit 10
         * offset 0
         */
        val sql = """
            select ${MediaDao.TABLENAME}.* from ${MediaDao.TABLENAME},${FavoriteDao.TABLENAME}
             where ${MediaDao.TABLENAME}.${MediaDao.Properties.Rid.columnName} = ${FavoriteDao.TABLENAME}.${FavoriteDao.Properties.Rid.columnName}
             and ${FavoriteDao.Properties.UserId.columnName} = '$userId'
             and [${FavoriteDao.Properties.Delete.columnName}] = false
             order by ${FavoriteDao.TABLENAME}.${FavoriteDao.Properties.Id.columnName} desc
             limit $itemSize
             offset ${(page-1)*itemSize}
        """.trimIndent()

        var list = mutableListOf<Media>()
        var cursor = App.mSession.database.rawQuery(sql,null)
        while (cursor.moveToNext()){
            val id = cursor.getLong(0)
            val url = ""
            val album = ""
            val artist = cursor.getString(3)
            val name = cursor.getString(4)
            val rid = cursor.getString(5)
            val pic = cursor.getString(6)
            val time = cursor.getLong(7)
            val strTime = cursor.getString(8)
            val artistId = cursor.getLong(9)
            val media = Media(id,url,album,artist,name,rid,pic,time,strTime,artistId)
            list.add(media)
        }
        cursor.close()

        return list
    }


    fun Load(userId: Long):List<Favorite>{
        return _Dao.queryBuilder()
            .where(FavoriteDao.Properties.UserId.eq(userId))
            .list()
    }


    fun Delete(userId: Long,rid:Int){
        val sql = """
            update ${FavoriteDao.TABLENAME} set [${FavoriteDao.Properties.Delete.columnName}] = true
            where ${FavoriteDao.Properties.UserId.columnName} = '$userId'
            and ${FavoriteDao.Properties.Rid.columnName} = $rid
        """.trimIndent()
        App.mSession.database.execSQL(sql)
    }


    fun Sync(userId: Long,list:List<Favorite>){
        val sql = """
            delete from ${FavoriteDao.TABLENAME}
            where ${FavoriteDao.Properties.UserId.columnName} = $userId
        """.trimIndent()
        App.mSession.database.execSQL(sql)
        list.forEach {
            var item = it
            item.id = null
            _Dao.insert(item)
        }
    }
}