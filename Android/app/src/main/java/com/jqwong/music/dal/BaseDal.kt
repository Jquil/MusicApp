package com.jqwong.music.dal

import org.greenrobot.greendao.AbstractDao

/**
 * @author: Jq
 * @date: 4/30/2023
 */
abstract class BaseDal<T,K> {

    protected val _dao:AbstractDao<T,K>

    protected var TAG = ""

    init {
        _dao = getDao()
        TAG = this::class.java.simpleName
    }

    abstract fun getDao():AbstractDao<T,K>

    fun loadAll():List<T>{
        return _dao.loadAll()
    }

    fun deleteAll(){
        _dao.deleteAll()
    }

    fun delete(entity:T){
        _dao.delete(entity)
    }

    fun update(entity:T){
        _dao.update(entity)
    }

    open fun insert(t:T){
        _dao.insert(t)
    }

    open fun insertInTx(t:List<T>){
        _dao.insertInTx(t)
    }
}