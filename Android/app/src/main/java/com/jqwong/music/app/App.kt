package com.jqwong.music.app

import android.app.Application
import com.jqwong.music.dal.dao.DaoMaster
import com.jqwong.music.dal.dao.DaoSession
import com.jqwong.music.model.Media
import com.jqwong.music.utils.AudioPlayerUtil

/**
 * @author: Jq
 * @date: 4/29/2023
 */
class App: Application() {

    companion object{
        var session:DaoSession? = null
    }

    override fun onCreate() {
        super.onCreate()
        initDatabase()
        AudioPlayerUtil.init(this)
    }

    fun initDatabase(){
        // 1、获取需要连接的数据库
        val helper = DaoMaster.DevOpenHelper(this,"music.db")
        val db = helper.writableDb
        // 2、创建数据库连接
        val master = DaoMaster(db)
        // 3、创建数据库会话
        session = master.newSession()
    }
}