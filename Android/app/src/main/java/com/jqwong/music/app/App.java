package com.jqwong.music.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;


//import androidx.media3.exoplayer.ExoPlayer;

import androidx.media3.exoplayer.ExoPlayer;

import com.jqwong.music.model.GlobalCache;
import com.jqwong.music.model.Tip;
import com.jqwong.music.page.activity.LoginActivity;
import com.jqwong.music.repository.dao.DaoMaster;
import com.jqwong.music.repository.dao.DaoSession;
import com.jqwong.music.utils.audio.AudioPlayerUtil;
//import com.jqwong.music.service.MediaPlaybackService;

public class App extends Application {

    public static DaoSession mSession;

    public static Context Context;

    public  static Tip Tip = new Tip();


    @Override
    public void onCreate() {
        super.onCreate();
        InitDataBase();
        AudioPlayerUtil.Companion.Init(this);
        Context = this;
    }



    private void InitDataBase(){
        // 1、获取需要连接的数据库
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, "music.db");
        SQLiteDatabase db = devOpenHelper.getWritableDatabase();
        // 2、创建数据库连接
        DaoMaster daoMaster = new DaoMaster(db);
        // 3、创建数据库会话
        mSession = daoMaster.newSession();
    }
}
