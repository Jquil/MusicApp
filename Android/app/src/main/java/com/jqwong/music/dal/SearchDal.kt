package com.jqwong.music.dal

import com.jqwong.music.app.App
import com.jqwong.music.dal.dao.SearchRecordDao
import com.jqwong.music.entity.SearchRecord
import com.jqwong.music.utils.DateTimeUtil
import org.greenrobot.greendao.AbstractDao

/**
 * @author: Jq
 * @date: 4/30/2023
 */
class SearchDal:BaseDal<SearchRecord,Long>() {

    fun query(limit:Int):List<SearchRecord>{
        return _dao.queryBuilder()
            .limit(limit)
            .orderDesc(SearchRecordDao.Properties.Id)
            .build()
            .list()
    }

    override fun getDao(): AbstractDao<SearchRecord, Long> {
        return App.session!!.searchRecordDao
    }
}