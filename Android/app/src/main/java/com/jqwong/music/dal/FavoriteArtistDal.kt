package com.jqwong.music.dal

import com.jqwong.music.app.App
import com.jqwong.music.dal.dao.FavoriteArtistDao
import com.jqwong.music.entity.FavoriteArtist
import com.jqwong.music.model.ArtistInfo
import org.greenrobot.greendao.AbstractDao

/**
 * @author: Jq
 * @date: 5/1/2023
 */
class FavoriteArtistDal:BaseDal<FavoriteArtist,Long>() {

    fun queryByArtistId(id:Long):List<FavoriteArtist>{
        return _dao
            .queryBuilder()
            .where(FavoriteArtistDao.Properties.AId.eq(id))
            .list()
    }


    fun delete(artistInfo: ArtistInfo){
        val sql = """
            delete from ${FavoriteArtistDao.TABLENAME}
            where ${FavoriteArtistDao.Properties.AId.columnName} = ${artistInfo.id}
        """.trimIndent()
        App.session!!.database.execSQL(sql)
    }

    override fun getDao(): AbstractDao<FavoriteArtist, Long> {
        return App.session!!.favoriteArtistDao
    }

}