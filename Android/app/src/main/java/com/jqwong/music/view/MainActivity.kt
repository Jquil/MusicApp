package com.jqwong.music.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jqwong.music.R
import com.jqwong.music.adapter.SongSheetAdapter
import com.jqwong.music.app.App
import com.jqwong.music.app.Constant
import com.jqwong.music.databinding.ActivityMainBinding
import com.jqwong.music.event.*
import com.jqwong.music.helper.AudioHelper
import com.jqwong.music.helper.CacheHelper
import com.jqwong.music.helper.RequestHelper
import com.jqwong.music.helper.TimeHelper
import com.jqwong.music.helper.UpdateHelper
import com.jqwong.music.helper.content
import com.jqwong.music.model.*
import com.jqwong.music.service.NetEaseCloudService
import com.jqwong.music.service.ServiceProxy
import com.jqwong.music.view.web.KuWoWebViewClient
import kotlinx.coroutines.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@UnstableApi
/**
 * @author: Jq
 * @date: 7/23/2023
 */
class MainActivity:BaseActivity<ActivityMainBinding>() {

    
    override fun initData(savedInstanceState: Bundle?) {
        _binding.stateLayout.showLoading()
        val sp = getSharedPreferences(Constant.CONFIG, MODE_PRIVATE)
        val strConfig = sp.getString(Constant.CONFIG,null)
        if(strConfig == null || strConfig == "") {
            App.config = Config.default()
        }
        else {
            try {
                val config = Config.fromJson(strConfig)
                App.config = config
            }
            catch (e:Exception){
                App.exceptions.add(
                    ExceptionLog(
                        title = "反序列化配置文件",
                        exception = e,
                        time = TimeHelper.getTime()
                    )
                )
                App.config = Config.default()
            }
        }
        // 抓取酷我平台Cookie
        _binding.wvView.settings.javaScriptEnabled = true
        _binding.wvView.settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36 Edg/115.0.1901.188"
        _binding.wvView.webViewClient = KuWoWebViewClient()
        _binding.wvView.loadUrl("http://kuwo.cn")

        // 同步歌单
        onSyncUserSheetEvent(SyncUserSheetEvent(Platform.NetEaseCloud,App.config.netEaseCloudConfig.sync_user_sheet){success, message ->
            if(!success)
                toast(message)
            _binding.stateLayout.content()
        })

        // 刷新token
        refreshToken(Platform.NetEaseCloud)

        // 检查更新
        UpdateHelper.newest {
            runOnUiThread {
                if(it.data != null){
                    App.newestVersion = it.data
                    if(App.newestVersion!!.number > App.version.number){
                        toast("应用有更新拉, 具体信息去'关于'中查看把")
                    }
                }
            }
        }

        // 获取alias
        RequestHelper.getAlias {
            if(it.success){
                it.data?.forEach {
                    App.globalAlias.put(it.name,it.alias)
                }
            }
            else{
                if(it.exception != null)
                    toast(it.exception.exception.message.toString())
                else
                    toast(it.message)
            }
        }
    }
    override fun intView() {
        _binding.btnDrawer.setOnClickListener {
            _binding.dlWrapper.openDrawer(GravityCompat.START)
        }
        _binding.dlContent.linkSetting.setOnClickListener {
            startActivity(Intent(this,SettingActivity::class.java))
        }
        _binding.dlContent.linkAbout.setOnClickListener {
            startActivity(Intent(this,AboutActivity::class.java))
        }
        _binding.dlContent.linkBgTask.setOnClickListener {
            //startActivity(Intent(this,DownloadActivity::class.java))
            toast("还没开放该功能噢")
        }
        _binding.dlContent.linkPc.setOnClickListener {
            toast("还没开放该功能噢")
        }
        _binding.dlContent.linkCarplay.setOnClickListener {
            toast("还没开放该功能噢")
        }
        _binding.dlContent.linkException.setOnClickListener {
            toast("还没开放该功能噢")
        }
        _binding.btnFavoriteMedia.setOnClickListener {
            toast("还没开放该功能噢")
        }
        _binding.layoutPlayBar.clPlayBar.setOnClickListener {
            startActivity(Intent(this,LyricActivity::class.java))
        }
        _binding.btnLeaderboard.setOnClickListener {
            startActivity(Intent(this,LeaderboardActivity::class.java).apply {
                putExtra(ExtraKey.Platform.name,App.config.default_search_platform.name)
            })
        }
        _binding.etSearch.setOnEditorActionListener { textView, i, keyEvent ->
            if(i == EditorInfo.IME_ACTION_SEARCH){
                val key = textView.text.toString()
                if(key == ""){
                    toast("请输入搜索关键字")
                }
                else{
                    _binding.etSearch.clearFocus()
                    startActivity(Intent(this,SearchResultActivity::class.java).apply {
                        putExtra(ExtraKey.Search.name,key)
                        putExtra(ExtraKey.Platform.name,App.config.default_search_platform.name)
                    })
                }
            }
            true
        }
        _binding.layoutPlayBar.ibPlayStatus.setOnClickListener {
            AudioHelper.playOrPause()
        }
        _binding.btnRecommendSheet.setOnClickListener {
            startActivity(Intent(this,RecommendSheetActivity::class.java).apply {
                putExtra(ExtraKey.Platform.name,App.config.default_search_platform.name)
            })
        }
        _binding.btnDailyMedia.setOnClickListener {
            startActivity(Intent(this,RecommendDailyActivity::class.java).apply {
                putExtra(ExtraKey.Platform.name,App.config.default_search_platform.name)
            })
        }
        if(App.playListIsInitialized() && !(App.playList.data.isNullOrEmpty())){
            onMediaChangeEvent(MediaChangeEvent(App.playList.data.get(App.playList.index)))
        }
        else{
            _binding.nsvMain.setPadding(0,0,0,0)
            _binding.clWrapperPlayBar.visibility = View.GONE
        }
        onPlayerStatusChangeEvent(PlayerStatusChangeEvent(AudioHelper.getPlayerIsPlaying()))
    }
    override fun useEventBus(): Boolean {
        return true
    }
    override fun statusBarColor(): Int {
        return R.color.background
    }
    override fun onStop() {
        App.config.save(this)
        if(App.config.exit_clear_cache)
            CacheHelper.clear(this)
        super.onStop()
    }
    
    private fun refreshToken(platform: Platform){
        when(platform){
            Platform.NetEaseCloud  -> {
                App.config.netEaseCloudConfig.let {
                    if(it.csrf_token.isNotEmpty()){
                        CoroutineScope(Dispatchers.IO).launch {
                            val service = ServiceProxy.get(platform).data as NetEaseCloudService
                            val result = service.refreshToken(it.csrf_token)
                            if(result.exception == null && result.success){
                                // refresh success
                            }
                        }
                    }
                }
            }
            else->{}
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSyncUserSheetEvent(event: SyncUserSheetEvent) {
        fun addView(platform:Platform,list:List<SongSheet>,jumpParams:String){
            val adapter = SongSheetAdapter()
            adapter.showPic = true
            val view = layoutInflater.inflate(R.layout.component_sheet,null)
            view.tag = platform.name
            val rv = view.findViewById<RecyclerView>(R.id.rv_list)
            val tv = view.findViewById<TextView>(R.id.tv_name)
            tv.text = platform.toString()
            rv.isNestedScrollingEnabled = false
            rv.layoutManager = LinearLayoutManager(this@MainActivity)
            rv.adapter = adapter
            adapter.setOnItemClickListener(object:BaseQuickAdapter.OnItemClickListener<SongSheet>{
                override fun onClick(
                    adapter: BaseQuickAdapter<SongSheet, *>,
                    view: View,
                    position: Int
                ) {
                    startActivity(Intent(this@MainActivity,UserSongSheetActivity::class.java).apply {
                        putExtra(ExtraKey.Platform.name,platform.name)
                        putExtra(ExtraKey.SongSheet.name,adapter.getItem(position)!!.toJson())
                        putExtra(ExtraKey.Data.name,jumpParams)
                    })
                }
            })
            adapter.submitList(list)
            _binding.llSheet.addView(view)
        }
        val platform = event.platform
        val child = _binding.llSheet.findViewWithTag<View>(platform.name)
        if (!event.sync) {
            if(child != null){
                _binding.llSheet.removeView(child)
            }
            event.callback(true,"")
            return
        }
        if(App.userSheets.containsKey(platform) && child != null){
            event.callback(true,"")
            return
        }
        if (event.platform == Platform.NetEaseCloud) {
            var reqParams: Any = ""
            App.config.netEaseCloudConfig.let {
                reqParams = "${it.uid};${it.csrf_token}"
            }
            if (reqParams == ";"){
                event.callback(true,"")
                return
            }
            CoroutineScope(Dispatchers.IO).launch {
                val service = ServiceProxy.get(Platform.NetEaseCloud).data as NetEaseCloudService
                val result = service.getUserSheet(reqParams)
                withContext(Dispatchers.Main) {
                    if (result.exception == null && result.support && result.data != null) {
                        addView(platform,result.data,App.config.netEaseCloudConfig.csrf_token)
                        App.userSheets.put(platform,result.data)
                        event.callback(true,"")
                    }
                    else{
                        event.callback(false,result.exception?.exception?.message.toString())
                    }
                }
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMediaChangeEvent(event: MediaChangeEvent){
        val media = event.media
        _binding.layoutPlayBar.tvName.text = media.name
        Glide.with(_binding.layoutPlayBar.ivPic)
            .asBitmap()
            .load(media.pic)
            .placeholder(R.drawable.ic_music)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
            .into(_binding.layoutPlayBar.ivPic)
        _binding.layoutPlayBar.lpiPlay.progress = 0
        if(_binding.clWrapperPlayBar.visibility == View.GONE){
            _binding.clWrapperPlayBar.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.IO).launch {
                while (_binding.clWrapperPlayBar.height == 0){
                    // wait
                }
                withContext(Dispatchers.Main){
                    _binding.nsvMain.setPadding(0,0,0,_binding.clWrapperPlayBar.height)
                }
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMediaPositionChangeEvent(event:MediaPositionChangeEvent){
        if(!App.playListIsInitialized() || App.playList.lyricInfo.second == null)
            return
        val lyric = App.playList.lyricInfo.second!!.current(event.position)
        _binding.layoutPlayBar.tvLyric.text = lyric.text
        _binding.layoutPlayBar.lpiPlay.progress = (event.position*1.0/App.playList.lyricInfo.second!!.lyrics.last().time*100).toInt()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMediaLoadingEvent(event:MediaLoadingEvent){
        _binding.layoutPlayBar.cpiLoading.visibility = if(event.finish) View.GONE else View.VISIBLE
        _binding.layoutPlayBar.ibPlayStatus.visibility = if(event.finish) View.VISIBLE else View.GONE
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLyricsLoadingEvent(event: LyricsLoadingEvent){
        when(event.info.first){
            LyricStatus.Loading->{
                _binding.layoutPlayBar.tvLyric.text = "loading..."
                _binding.layoutPlayBar.cpiLoading.visibility = View.VISIBLE
                _binding.layoutPlayBar.ibPlayStatus.visibility = View.GONE
            }
            LyricStatus.Error->{
                _binding.layoutPlayBar.tvLyric.text = "很抱歉, 没有找到歌曲歌词.."
                _binding.layoutPlayBar.cpiLoading.visibility = View.GONE
                _binding.layoutPlayBar.ibPlayStatus.visibility = View.VISIBLE
            }
            LyricStatus.Success->{
                if(App.playList.lyricInfo.second != null){
                    _binding.layoutPlayBar.tvLyric.text = App.playList.lyricInfo.second!!.lyrics.first().text
                }
                _binding.layoutPlayBar.cpiLoading.visibility = View.GONE
                _binding.layoutPlayBar.ibPlayStatus.visibility = View.VISIBLE
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlayerStatusChangeEvent(event: PlayerStatusChangeEvent){
        _binding.layoutPlayBar.ibPlayStatus.setImageResource(if(event.playing) R.drawable.ic_pause else R.drawable.ic_play)
    }
}