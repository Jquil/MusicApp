package com.jqwong.music.view

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jqwong.music.R
import com.jqwong.music.adapter.SongSheetAdapter
import com.jqwong.music.app.App
import com.jqwong.music.event.CollectOrCancelMediaEvent
import com.jqwong.music.helper.setTitleDefaultStyle
import com.jqwong.music.model.Artist
import com.jqwong.music.model.Media
import com.jqwong.music.model.Platform
import com.jqwong.music.model.SongSheet
import com.jqwong.music.service.ServiceProxy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        val parent = javaClass.superclass.name
        var name = acName.substring(0, acName.indexOf("Activity"))
        if(parent != BaseActivity::class.java.name){
            val arr = parent.split('.')
            name = arr.last()
        }
        val bindingClass = classLoader.loadClass("${packageName}.databinding.Activity${name}Binding")
        return bindingClass.getMethod("inflate", LayoutInflater::class.java)
            .invoke(null, layoutInflater) as T
    }
    protected fun toast(message:String){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }
    protected fun changePlatform(list:List<Platform>,call:(platform:Platform)->Unit){
        MaterialDialog(this, BottomSheet()).show {
            customView(R.layout.dialog_select_common)
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
    protected fun selectArtist(list:List<Artist>, call:(artist:Artist)->Unit){
        MaterialDialog(this, BottomSheet()).show {
            customView(R.layout.dialog_select_common)
            cornerRadius(20f)
            setTitle("")
            view.setBackgroundResource(R.drawable.bg_dialog)
            view.setTitleDefaultStyle(this@BaseActivity)
            val layout = view.contentLayout.findViewById<LinearLayout>(R.id.ll_wrapper)
            var index = 0
            list.forEach {
                val name = it.name
                val child = View.inflate(this@BaseActivity,R.layout.item_select_artist,null)
                child.findViewById<TextView>(R.id.tv_name).let {
                    it.text = name
                    it.tag = index
                    it.setOnClickListener {
                        call.invoke(list.get(it.tag as Int))
                    }
                }
                layout.addView(child)
                index++
            }
        }
    }
    protected fun selectUserSheet(platform: Platform, call: (sheet: SongSheet) -> Unit){
        if(!App.userSheets.containsKey(platform)){
            toast("Sorry, you did not synchronize the '${platform.name}' platform data")
            return
        }
        selectSheet(App.userSheets.get(platform)!!,call)
    }
    protected fun selectSheet(list:List<SongSheet>, call: (sheet: SongSheet)-> Unit){
        MaterialDialog(this, BottomSheet()).show {
            customView(R.layout.dialog_select_common_x)
            cornerRadius(20f)
            view.setBackgroundResource(R.drawable.bg_dialog)
            view.setTitleDefaultStyle(this@BaseActivity)
            val adapter = SongSheetAdapter()
            val rvList = view.contentLayout.findViewById<RecyclerView>(R.id.rv_list)
            rvList.layoutManager = LinearLayoutManager(this@BaseActivity)
            rvList.adapter = adapter
            adapter.submitList(list)
            adapter.setOnItemClickListener(object: BaseQuickAdapter.OnItemClickListener<SongSheet>{
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onClick(
                    adapter: BaseQuickAdapter<SongSheet, *>,
                    view: View,
                    position: Int
                ) {
                    val item = adapter.getItem(position)!!
                    call(item)
                }
            })
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    protected fun collectOrCancelMedia(platform: Platform,sheet: SongSheet?,media: Media, collect:Boolean,call:(success:Boolean) -> Unit){
        //if(media.platform != platform){
        //    val msg = "It is not supported to bookmark songs from the '${media.platform.name}' platform to the '${platform.name}' platform"
        //    toast(msg)
        //    return
        //}
        if(collect){
            selectUserSheet(platform){
                EventBus.getDefault().post(CollectOrCancelMediaEvent(false))
                CoroutineScope(Dispatchers.IO).launch {
                    var reqParams:Any = ""
                    when(platform){
                        Platform.NetEaseCloud -> {
                            reqParams = "${it.id};${media.id};${App.config.netEaseCloudMusicConfig.csrf_token}"
                        }
                        else -> {}
                    }
                    val result = ServiceProxy.collectOrCancelSong(platform,true,reqParams)
                    withContext(Dispatchers.Main){
                        var msg = ""
                        if(result.exception != null){
                            msg = result.exception.exception.message.toString()
                        }
                        else{
                            msg = result.message
                        }
                        if(result.success){
                            call(true)
                        }
                        toast(msg)
                        EventBus.getDefault().post(CollectOrCancelMediaEvent(true))
                    }
                }
            }
        }
        else{
            var reqParams:Any = ""
            when(platform){
                Platform.NetEaseCloud -> {
                    reqParams = "${sheet!!.id};${media.id};${App.config.netEaseCloudMusicConfig.csrf_token}"
                }
                else -> {}
            }
            EventBus.getDefault().post(CollectOrCancelMediaEvent(false))
            CoroutineScope(Dispatchers.IO).launch {
                val result = ServiceProxy.collectOrCancelSong(platform,false,reqParams)
                withContext(Dispatchers.Main){
                    if(result.exception != null){
                        toast(result.exception.exception.message.toString())
                    }
                    else{
                        toast(result.message)
                    }
                    if(result.success){
                        call(true)
                    }
                    EventBus.getDefault().post(CollectOrCancelMediaEvent(true))
                }
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