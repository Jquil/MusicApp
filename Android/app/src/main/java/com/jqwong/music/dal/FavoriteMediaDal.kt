package com.jqwong.music.dal

import com.jqwong.music.app.App
import com.jqwong.music.dal.dao.FavoriteMediaDao
import com.jqwong.music.dal.dao.MediaDao
import com.jqwong.music.entity.FavoriteMedia
import com.jqwong.music.entity.Media
import org.greenrobot.greendao.AbstractDao

/**
 * @author: Jq
 * @date: 4/30/2023
 */
class FavoriteMediaDal:BaseDal<FavoriteMedia,Long>() {

    fun queryMedias(userUUID: String, page:Int, pageSize:Int):List<Media>{
        val sql = """
            select ${MediaDao.TABLENAME}.* from ${MediaDao.TABLENAME},${FavoriteMediaDao.TABLENAME}
             where ${MediaDao.TABLENAME}.${MediaDao.Properties.Rid.columnName} = ${FavoriteMediaDao.TABLENAME}.${FavoriteMediaDao.Properties.Rid.columnName}
             and ${FavoriteMediaDao.Properties.UserUUID.columnName} = '$userUUID'
             order by ${FavoriteMediaDao.TABLENAME}.${FavoriteMediaDao.Properties.Id.columnName} desc
             limit $pageSize
             offset ${(page - 1) * pageSize}
        """.trimIndent()
        val list = mutableListOf<Media>()
        val cursor = App.session!!.database.rawQuery(sql,null)
        while (cursor.moveToNext()){
            val id = cursor.getLong(0)
            val musicIid = cursor.getString(1)
            val rid = cursor.getInt(2)
            val artist = cursor.getString(3)
            val pic = cursor.getString(4)
            val album = cursor.getString(5)
            val albumId = cursor.getInt(6)
            val name = cursor.getString(7)
            val pic120 = cursor.getString(8)
            val strTime = cursor.getString(9)
            val time = cursor.getLong(10)
            val url = cursor.getString(11)
            val artistId = cursor.getLong(12)
            val media = Media(id,musicIid,rid,artist,pic,album,albumId,name,pic120,strTime,time,url,artistId)
            list.add(media)
        }
        cursor.close()
        return list
    }

    fun query(userUUID:String, rid:String):List<FavoriteMedia>{
        return _dao.queryBuilder()
            .where(FavoriteMediaDao.Properties.UserUUID.eq(userUUID),FavoriteMediaDao.Properties.Rid.eq(rid))
            .list()
    }

    fun delete(userUUID:String,rid:String){
        val list = query(userUUID, rid)
        if(list != null){
            list.forEach {
                _dao.delete(it)
            }
        }
    }

    override fun getDao(): AbstractDao<FavoriteMedia, Long> {
        return App.session!!.favoriteMediaDao
    }
}