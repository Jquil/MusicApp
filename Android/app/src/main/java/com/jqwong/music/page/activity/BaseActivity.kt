package com.jqwong.music.page.activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.jqwong.music.R
import com.jqwong.music.app.App

abstract class BaseActivity : AppCompatActivity() {

    protected var TAG = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TAG = localClassName
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        if(App.Context.resources.configuration.uiMode == 0x11){
            // 白天模式
            val wic: WindowInsetsControllerCompat? = ViewCompat.getWindowInsetsController(window.decorView)
            // true表示Light Mode，状态栏字体呈黑色，反之呈白色
            wic?.setAppearanceLightStatusBars(true)
        }
        else{
            // 夜间模式
        }
        SetContentView(savedInstanceState)
        Init()
    }

    override fun onDestroy() {
        super.onDestroy()
        Destory()
    }

    fun Init(){
        InitView()
        InitData()
        InitListener()
    }

    abstract fun InitView()

    abstract fun InitData()

    abstract fun InitListener()

    abstract fun SetContentView(savedInstanceState: Bundle?)

    abstract fun Destory()
}