package com.jqwong.music.view

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.QuickAdapterHelper
import com.jqwong.music.R
import com.jqwong.music.adapter.CustomLoadMoreAdapter
import com.jqwong.music.adapter.MediaAdapter
import com.jqwong.music.app.App
import com.jqwong.music.databinding.ActivityTemplateBinding
import com.jqwong.music.event.CollectOrCancelMediaEvent
import com.jqwong.music.event.MediaChangeEvent
import com.jqwong.music.event.MediaLoadingEvent
import com.jqwong.music.helper.AudioHelper
import com.jqwong.music.helper.DownloadHelper
import com.jqwong.music.helper.PermissionHelper
import com.jqwong.music.helper.UpdateHelper
import com.jqwong.music.model.DownloadTask
import com.jqwong.music.model.LyricStatus
import com.jqwong.music.model.Media
import com.jqwong.music.model.Platform
import com.jqwong.music.model.PlayList
import com.jqwong.music.model.copy
import com.jqwong.music.view.listener.DoubleClickListener
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.UUID

/**
 * @author: Jq
 * @date: 8/13/2023
 */
abstract class Template:BaseActivity<ActivityTemplateBinding>() {
    lateinit var _platform: Platform
    lateinit var adapter: MediaAdapter
    lateinit var adapterHelper: QuickAdapterHelper
    var loadFinish:Boolean = false
    var page:Int = 0

    override fun initData(savedInstanceState: Bundle?) {

    }

    @UnstableApi
    override fun intView() {
        setSupportActionBar(_binding.includeToolbar.toolbar)
        _binding.includeToolbar.toolbar.setOnLongClickListener {
            true
        }
        _binding.includeToolbar.toolbar.setOnClickListener(object: DoubleClickListener(){
            override fun onDoubleClick(v: View?) {
                _binding.includeMain.rvList.scrollToPosition(0)
            }
        })
        _binding.includeToolbar.toolbar.setNavigationOnClickListener(object:OnClickListener{
            override fun onClick(v: View?) {
                finish()
            }
        })
        _binding.includeMain.rvList.layoutManager = LinearLayoutManager(this)
        adapter = MediaAdapter()
        adapter.setOnItemClickListener((BaseQuickAdapter.OnItemClickListener<Media> { adapter, view, position -> //val item = adapter.getItem(position)!!
            val item = adapter.getItem(position)
            if(App.playListIsInitialized() && App.playList.data.isNotEmpty() && App.playList.data[App.playList.index].compare(item!!))
                return@OnItemClickListener
            App.playList = PlayList(0,
                Pair(LyricStatus.Loading,null),adapter.items.subList(position,adapter.items.size).copy())
            adapter.notifyDataSetChanged()
            AudioHelper.start()
        }))

        val loadMoreAdapter = CustomLoadMoreAdapter()
        adapterHelper = QuickAdapterHelper.Builder(adapter)
            .setTrailingLoadStateAdapter(loadMoreAdapter)
            .build()
        _binding.includeMain.rvList.adapter = adapterHelper.adapter
        registerForContextMenu(_binding.includeMain.rvList)
    }
    override fun useEventBus(): Boolean {
        return true
    }
    override fun statusBarColor(): Int {
        return R.color.background
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterForContextMenu(_binding.includeMain.rvList)
    }
    @UnstableApi
    override fun onContextItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_artist -> {
                gotoArtistActivity(adapter.getSelectMediaByLongClick())
            }
            R.id.action_collect -> {
                val media = adapter.getSelectMediaByLongClick() ?: return false
                collectOrCancelMedia(_platform,null,media,true){}
            }
            R.id.action_change_platform -> {
                val media = adapter.getSelectMediaByLongClick() ?: return false
                changePlatform(_platform,media.name)
            }
            R.id.action_lyric -> {
                gotoLyricActivity()
            }
            R.id.action_download -> {
                val permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                if(!PermissionHelper.check(this, permission)){
                    toast("没有权限下载阿, 授权成功再来下载吧")
                    PermissionHelper.request(this,permission)
                }
                else{
                    // download
                    val m = adapter.getSelectMediaByLongClick() ?: return false
                    AudioHelper.getMedia(m){ success, media ->
                        if (!success || media == null || media.play_url.isEmpty()){
                            toast("获取歌曲url失败了..")
                        }
                        else{
                            toast("开始下载咯")
                            if(media.is_local){
                                // 文件拷贝
                            }
                            else{
                                val fDir = File(App.config.downloadPath)
                                if(!fDir.exists())
                                    fDir.mkdir()
                                val path = media.path(App.config.downloadPath)
                                val tsk = DownloadTask(
                                    id = UUID.randomUUID().toString(),
                                    name = File(path).name,
                                    downloadPath = media.play_url,
                                    savePath = path,
                                    finish = false,
                                    client = null
                                ){
                                    runOnUiThread {
                                        val uri = Uri.fromFile(File(path))
                                        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri)
                                        sendBroadcast(intent)
                                        toast("ok啦")
                                    }
                                }
                                DownloadHelper.add(tsk)
                            }
                        }
                    }
                }
            }
        }
        return super.onContextItemSelected(item)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 100){
            for(i in 0 until permissions.count()){
                val permission = permissions[i]
                val result = grantResults[i]
                if(permission.equals(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    if (result == PackageManager.PERMISSION_GRANTED){
                        toast("success")
                    }
                    else{
                        toast("error")
                    }
                }
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMediaChangeEvent(event: MediaChangeEvent){
        adapter.notifyDataSetChanged()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMediaLoadingEvent(event: MediaLoadingEvent){
        _binding.includeToolbar.cpiLoading.visibility = if(event.finish) View.GONE else View.VISIBLE
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCollectOrCancelMediaEvent(event: CollectOrCancelMediaEvent){
        _binding.includeToolbar.cpiLoading.visibility = if(event.finish) View.GONE else View.VISIBLE
    }
}