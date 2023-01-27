package com.jqwong.music.repository

import com.jqwong.music.app.App
import com.jqwong.music.repository.dao.ArtistDao
import com.jqwong.music.repository.dao.FavoriteDao
import com.jqwong.music.repository.entity.Artist

class ArtistRepository {

    private val _Dao: ArtistDao

    init {
        _Dao = App.mSession.artistDao
    }


    fun Add(artist: Artist){
        val list = _Dao.queryBuilder()
            .where(ArtistDao.Properties.ArtistId.eq(artist.ArtistId))
            .build().list()
        if(list.size != 0)
            return
        _Dao.insert(artist)
    }



    fun Delete(userId:Long,artistId:Long){
        val sql = """
            delete from ${ArtistDao.TABLENAME}
            where ${ArtistDao.Properties.ArtistId.columnName} = $artistId
            and ${ArtistDao.Properties.UserId.columnName} = $userId
        """.trimIndent()
        App.mSession.database.execSQL(sql)
    }


    fun Load(userId:Long):List<Artist>{
        return _Dao.queryBuilder()
            .where(ArtistDao.Properties.UserId.eq(userId))
            .build().list()
    }


    fun Sync(userId: Long,list:List<Artist>){
        val sql = """
            delete from ${ArtistDao.TABLENAME}
            where ${ArtistDao.Properties.UserId.columnName} = $userId
        """.trimIndent()
        App.mSession.database.execSQL(sql)
        list.forEach {
            var item = it
            item.Id = null
            _Dao.insert(item)
        }
    }
}