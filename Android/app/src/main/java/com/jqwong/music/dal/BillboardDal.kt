package com.jqwong.music.dal

import android.util.Log
import com.jqwong.music.app.App
import com.jqwong.music.dal.dao.BillboardDao
import com.jqwong.music.entity.Billboard
import com.jqwong.music.model.BillboardList
import com.jqwong.music.utils.DateTimeUtil
import org.greenrobot.greendao.AbstractDao

/**
 * @author: Jq
 * @date: 4/30/2023
 */
class BillboardDal:BaseDal<Billboard,Long>(){

    fun insert(t:List<BillboardList>){
        val time = DateTimeUtil.getDate()
        t.forEach {
            val parent = it.name
            it.list.forEach {
                val item = Billboard(null,it.name,it.sourceid,it.id,parent,time)
                _dao.insert(item)
            }
        }
    }

    fun query():List<BillboardList>{
        val list = _dao.queryBuilder().build().list()
        val map = HashMap<String,MutableList<com.jqwong.music.model.Billboard>>()
        list.forEach {
            val item = com.jqwong.music.model.Billboard(it.sourceId,it.bId,it.name,it.time)
            if(map.containsKey(it.parent)){
                map.get(it.parent)?.add(item)
            }
            else{
                map.put(it.parent, mutableListOf(item))
            }
        }

        val data = mutableListOf<BillboardList>()
        map.forEach { s, billboards ->
            data.add(BillboardList(s,billboards))
        }
        return data
    }

    override fun getDao(): AbstractDao<Billboard, Long> {
        return App.session!!.billboardDao
    }
}