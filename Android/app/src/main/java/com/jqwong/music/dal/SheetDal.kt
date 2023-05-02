package com.jqwong.music.dal

import com.jqwong.music.app.App
import com.jqwong.music.entity.Sheet
import org.greenrobot.greendao.AbstractDao

/**
 * @author: Jq
 * @date: 4/30/2023
 */
class SheetDal:BaseDal<Sheet,Long>() {

    override fun getDao(): AbstractDao<Sheet, Long> {
        return App.session!!.sheetDao
    }
}