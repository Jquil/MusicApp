package com.jqwong.music.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.viewbinding.ViewBinding
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.jqwong.music.R
import com.jqwong.music.helper.setTitleDefaultStyle
import com.jqwong.music.model.Platform
import org.greenrobot.eventbus.EventBus

/**
 * @author: Jq
 * @date: 7/23/2023
 */
abstract class BaseActivity<T: ViewBinding>: AppCompatActivity(){
    protected lateinit var TAG:String
    protected lateinit var _binding:T
    private var useEventBus:Boolean = false
    protected val pageItemSize = 20
    protected val maxReloadCount = 3
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
    protected fun changePlatform(list:List<Platform>,call:(platform:Platform)->Unit){
        MaterialDialog(this, BottomSheet()).show {
            customView(R.layout.dialog_select_platform)
            cornerRadius(20f)
            setTitle("")
            view.setBackgroundResource(R.drawable.bg_dialog)
            view.setTitleDefaultStyle(this@BaseActivity)
            val layout = view.contentLayout.findViewById<LinearLayout>(R.id.ll_wrapper)
            list.forEach {
                val name = it.name
                val child = View.inflate(this@BaseActivity,R.layout.item_platform,null)
                child.findViewById<TextView>(R.id.tv_name).let {
                    it.text = name
                    it.setOnClickListener {
                        call.invoke(Platform.valueOf((it as TextView).text.toString()))
                    }
                }
                layout.addView(child)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TAG = localClassName
        window.statusBarColor = getColor(statusBarColor())
        val wic = ViewCompat.getWindowInsetsController(window.decorView)
        if(wic != null) {
            wic.isAppearanceLightStatusBars = false
        }
        _binding = getBinding()
        setContentView(_binding.root)
        intView()
        initData(savedInstanceState)
        useEventBus = useEventBus()
        if(useEventBus)
        {
            EventBus.getDefault().register(this)
        }
    }
    override fun onDestroy() {
        if(useEventBus)
        {
            EventBus.getDefault().unregister(this)
        }
        super.onDestroy()
    }
}