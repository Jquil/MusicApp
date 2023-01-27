package com.jqwong.music.repository

import com.jqwong.music.app.App
import com.jqwong.music.repository.dao.SheetDao
import com.jqwong.music.repository.entity.Sheet

class SheetRepository {

    private var _Dao:SheetDao

    init {
        _Dao = App.mSession.sheetDao
    }


    fun Add(sheet: Sheet):Long{
        return _Dao.insert(sheet)
    }


    fun Load(userId: Long):List<Sheet>{
        val data = _Dao.queryBuilder().where(SheetDao.Properties.UserId.eq(userId),SheetDao.Properties.Delete.eq(false)).list()
        return data
    }


    fun Update(sheet:Sheet){
        _Dao.update(sheet)
    }
}