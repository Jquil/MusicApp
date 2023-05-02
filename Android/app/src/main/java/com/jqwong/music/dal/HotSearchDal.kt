package com.jqwong.music.dal

import com.jqwong.music.app.App
import com.jqwong.music.entity.HotSearchRecord
import com.jqwong.music.utils.DateTimeUtil
import org.greenrobot.greendao.AbstractDao

/**
 * @author: Jq
 * @date: 4/30/2023
 */
class HotSearchDal:BaseDal<HotSearchRecord,Long>() {

    override fun getDao(): AbstractDao<HotSearchRecord, Long> {
        return App.session!!.hotSearchRecordDao
    }
}