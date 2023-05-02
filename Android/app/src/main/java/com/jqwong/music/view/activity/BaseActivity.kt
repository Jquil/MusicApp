package com.jqwong.music.view.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewbinding.ViewBinding
import com.jqwong.music.R
import org.greenrobot.eventbus.EventBus

abstract class BaseActivity<T:ViewBinding>: AppCompatActivity() {

    protected lateinit var TAG:String

    protected lateinit var _binding:T

    private var _useEventBus:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TAG = localClassName
        setStatusBar()
        initBinding()
        setContentView(_binding.root)
        initView()
        initListener()
        initData(savedInstanceState)
        setTitle(Title())
        _useEventBus = useEventBus()
        if(_useEventBus){
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        if(_useEventBus){
            EventBus.getDefault().unregister(this)
        }
        super.onDestroy()
    }

    fun toast(str:String){
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show()
    }

    private fun setStatusBar(){
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        if(this.resources.configuration.uiMode == 0x11){
            val wic: WindowInsetsControllerCompat? = ViewCompat.getWindowInsetsController(window.decorView)
            wic?.setAppearanceLightStatusBars(true)
        }
        else{
            // 夜间模式
        }
    }

    private fun initBinding(){
        val acName = javaClass.simpleName
        val name = acName.substring(0, acName.indexOf("Activity"))
        val bindingClass =
            classLoader.loadClass("${packageName}.databinding.Activity${name}Binding")
        //最后强转为以泛型传入的实际Binding的类型
        _binding = bindingClass.getMethod("inflate", LayoutInflater::class.java)
            .invoke(null, layoutInflater) as T
    }


    protected fun setTitle(title:String){
        val title = title.replace("&nbsp;"," ")
        _binding.root.findViewById<TextView>(R.id.tv_title)?.text = title
    }

    /**
     * 设置标题
     * @return String
     */
    abstract fun Title():String


    /**
     * 初始化数据
     * @param savedInstanceState Bundle?
     */
    abstract fun initData(savedInstanceState: Bundle?)


    /**
     * 初始化监听器
     */
    abstract fun initListener()

    /**
     * 初始化视图
     */
    abstract fun initView()


    /**
     * 是否使用事件总线
     * @return Boolean
     */
    abstract fun useEventBus():Boolean
}