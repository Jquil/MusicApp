package com.jqwong.music.repository

import android.util.Log
import com.jqwong.music.app.App
import com.jqwong.music.repository.dao.MediaDao
import com.jqwong.music.repository.dao.SheetInfoDao
import com.jqwong.music.repository.entity.Media
import com.jqwong.music.repository.entity.Sheet
import com.jqwong.music.repository.entity.SheetInfo

class SheetInfoRepository: BaseRepository() {

    private var _Dao: SheetInfoDao

    private var _MediaDao: MediaDao

    init {
        _Dao = App.mSession.sheetInfoDao
        _MediaDao = App.mSession.mediaDao
    }


    fun Exit(userId: Long,sheetToken: String,rid: String):SheetInfo?{
        // then check query is exit sheet
        val data = _Dao.queryBuilder()
            .where(SheetInfoDao.Properties.UserId.eq(userId),
                   SheetInfoDao.Properties.SheetToken.eq(sheetToken),
                   SheetInfoDao.Properties.Rid.eq(rid),
                   SheetInfoDao.Properties.Delete.eq(false))
            .build().list()
        if(data.size == 0)
            return null
        else
            return data.get(0)
    }


    fun Add(userId:Long,sheet:Sheet,media: Media){
        val dataOfMedia = _MediaDao.queryBuilder()
            .where(MediaDao.Properties.Rid.eq(media.rid))
            .build().list()

        if(dataOfMedia.size == 0){
            _MediaDao.insert(media)
        }

        _Dao.insert(SheetInfo(null,userId,media.rid.toLong(),sheet.Token,false))
    }


    fun Load(userId: Long,sheetToken:String,page:Int,itemSize:Int):List<Media>{

        /**
         * select media.* from media,sheetInfo
         * where sheetInfo.rid = media.rid
         * and sheetInfo.Delete = false
         * and sheetInfo.SheetToken = sheetToken
         * order by media.id desc
         * limit 10
         * offset 0
         */

        val sql = """
            select ${MediaDao.TABLENAME}.* from ${MediaDao.TABLENAME},${SheetInfoDao.TABLENAME}
            where ${SheetInfoDao.TABLENAME}.${SheetInfoDao.Properties.SheetToken.columnName} = '$sheetToken'
            and ${SheetInfoDao.TABLENAME}.${SheetInfoDao.Properties.UserId.columnName} = $userId
            and ${SheetInfoDao.TABLENAME}.[${SheetInfoDao.Properties.Delete.columnName}] = false
            and ${SheetInfoDao.TABLENAME}.${SheetInfoDao.Properties.Rid.columnName} = ${MediaDao.TABLENAME}.${MediaDao.Properties.Rid.columnName}
            order by ${SheetInfoDao.TABLENAME}.${SheetInfoDao.Properties.Id.columnName} desc
            limit $itemSize
            offset ${(page-1) * itemSize} 
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


    fun Load(userId: Long):List<SheetInfo>{
        return _Dao.queryBuilder()
            .where(SheetInfoDao.Properties.UserId.eq(userId))
            .list()
    }



    fun Delete(userId:Long,sheetToken: String,rid:Int){
        try {
            val sql = """
            update ${SheetInfoDao.TABLENAME} set [${SheetInfoDao.Properties.Delete.columnName}] = true
            where ${SheetInfoDao.Properties.UserId.columnName} = '$userId'
            and ${SheetInfoDao.Properties.SheetToken.columnName} = '$sheetToken'
            and ${SheetInfoDao.Properties.Rid.columnName} = $rid
        """.trimIndent()
            App.mSession.database.execSQL(sql)
        }catch (e:Exception){
            Log.e(TAG,e.message.toString())
        }
    }


    fun DeleteAll(userId: Long,sheetToken: String){
        val sql = """
            update ${SheetInfoDao.TABLENAME} set [${SheetInfoDao.Properties.Delete.columnName}] = true
            where ${SheetInfoDao.Properties.SheetToken.columnName} = $sheetToken
            and ${SheetInfoDao.Properties.UserId.columnName} = $userId
        """.trimIndent()
        App.mSession.database.execSQL(sql)
    }



    fun Sync(userId: Long, list:List<SheetInfo>){
        var sql = """
            delete from ${SheetInfoDao.TABLENAME}
            where ${SheetInfoDao.Properties.UserId.columnName} = $userId
        """.trimIndent()
        App.mSession.database.execSQL(sql)
        list.forEach {
            var item = it
            item.id = null
            _Dao.insert(item)
        }
    }
}