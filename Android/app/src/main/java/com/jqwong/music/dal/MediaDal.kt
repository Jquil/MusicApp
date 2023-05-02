package com.jqwong.music.dal

import com.jqwong.music.app.App
import com.jqwong.music.dal.dao.MediaDao
import com.jqwong.music.entity.Media
import org.greenrobot.greendao.AbstractDao

/**
 * @author: Jq
 * @date: 4/30/2023
 */
class MediaDal:BaseDal<Media,Long>() {

    fun queryByRid(rid:String):List<Media>{
        return _dao.queryBuilder()
            .where(MediaDao.Properties.Rid.eq(rid))
            .list()
    }

    override fun getDao(): AbstractDao<Media, Long> {
        return App.session!!.mediaDao
    }
}