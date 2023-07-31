package com.jqwong.music.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.viewbinding.ViewBinding

/**
 * @author: Jq
 * @date: 7/23/2023
 */
abstract class BaseActivity<T: ViewBinding>: AppCompatActivity(){
    protected lateinit var TAG:String
    protected lateinit var _binding:T
    private var _useEventBus:Boolean = false
    protected val _pageItemSize = 20
    abstract fun initData(savedInstanceState: Bundle?)
    abstract fun intView()
    abstract fun useEventBus():Boolean
    abstract fun statusBarColor():Int
    private fun getBinding():T{
        val acName = javaClass.simpleName
        val name = acName.substring(0, acName.indexOf("Activity"))
        val bindingClass = classLoader.loadClass("${packageName}.databinding.Activity${name}Binding")
        return bindingClass.getMethod("inflate", LayoutInflater::class.java)
            .invoke(null, layoutInflater) as T
    }
    protected fun toast(message:String){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TAG = localClassName
        window.statusBarColor = getColor(statusBarColor())
        val wic = ViewCompat.getWindowInsetsController(window.decorView)
        if(wic != null)
        {
            wic.isAppearanceLightStatusBars = false
        }
        _binding = getBinding()
        setContentView(_binding.root)
        intView()
        initData(savedInstanceState)
        _useEventBus = useEventBus()
        if(_useEventBus)
        {
            // register eventbus
        }
    }
    override fun onDestroy() {
        if(_useEventBus)
        {
            // cancel register eventbus
        }
        super.onDestroy()
    }
}