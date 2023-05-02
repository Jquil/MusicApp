package com.jqwong.music.dal

import com.jqwong.music.app.App
import com.jqwong.music.dal.dao.MediaDao
import com.jqwong.music.dal.dao.SheetInfoDao
import com.jqwong.music.entity.Media
import com.jqwong.music.entity.SheetInfo
import org.greenrobot.greendao.AbstractDao

/**
 * @author: Jq
 * @date: 4/30/2023
 */
class SheetInfoDal:BaseDal<SheetInfo,Long>() {

    fun query(token:String,rid:String):List<SheetInfo>{
        return _dao.queryBuilder()
            .where(SheetInfoDao.Properties.Rid.eq(rid),SheetInfoDao.Properties.SheetToken.eq(token))
            .list()
    }

    fun deleteByToken(token:String){
        val sql = """
            delete from ${SheetInfoDao.TABLENAME}
            where ${SheetInfoDao.Properties.SheetToken.columnName} = "${token}"
        """.trimIndent()
        App.session!!.database.execSQL(sql)
    }

    fun deleteByRidAndUserUUID(rid:Int,userUUID: String){
        val sql = """
            delete from ${SheetInfoDao.TABLENAME}
            where ${SheetInfoDao.Properties.Rid.columnName} = ${rid}
            and ${SheetInfoDao.Properties.UserUUID.columnName} = "${userUUID}"
        """.trimIndent()
        App.session!!.database.execSQL(sql)
    }


    override fun insert(sheetInfo:SheetInfo){
        val list = query(sheetInfo.SheetToken,sheetInfo.rid)
        if(list.isEmpty()){
            _dao.insert(sheetInfo)
        }
    }

    fun queryMedias(token: String, page:Int, pageSize:Int):List<Media>{
        val sql = """
            select ${MediaDao.TABLENAME}.* from ${MediaDao.TABLENAME},${SheetInfoDao.TABLENAME}
            where ${SheetInfoDao.TABLENAME}.${SheetInfoDao.Properties.SheetToken.columnName} = '$token'
            and ${SheetInfoDao.TABLENAME}.${SheetInfoDao.Properties.Rid.columnName} = ${MediaDao.TABLENAME}.${MediaDao.Properties.Rid.columnName}
            order by ${SheetInfoDao.TABLENAME}.${SheetInfoDao.Properties.Id.columnName} desc
            limit $pageSize
            offset ${(page-1) * pageSize} 
        """.trimIndent()

        var list = mutableListOf<Media>()
        var cursor = App.session!!.database.rawQuery(sql,null)
        while (cursor.moveToNext()) {
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

    override fun getDao(): AbstractDao<SheetInfo, Long> {
        return App.session!!.sheetInfoDao
    }
}