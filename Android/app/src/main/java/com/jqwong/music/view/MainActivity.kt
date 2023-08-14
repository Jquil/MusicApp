package com.jqwong.music.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
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
import com.jqwong.music.helper.TimeHelper
import com.jqwong.music.helper.startAnimation
import com.jqwong.music.model.*
import com.jqwong.music.service.NetEaseCloudService
import com.jqwong.music.service.ServiceProxy
import com.jqwong.music.view.web.KuWoWebViewClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@UnstableApi
/**
 * @author: Jq
 * @date: 7/23/2023
 */
class MainActivity:BaseActivity<ActivityMainBinding>() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun initData(savedInstanceState: Bundle?) {
        _binding.stateLayout.showLoading()
        val sp = getSharedPreferences(Constant.CONFIG, MODE_PRIVATE)
        val strConfig = sp.getString(Constant.CONFIG,null)
        if(strConfig == null || strConfig == "") {
            App.config = getDefaultConfig()
        }
        else {
            try {
                val moshi = Moshi.Builder()
                    .addLast(KotlinJsonAdapterFactory())//使用kotlin反射处理，要加上这个
                    .build()
                val adapter = moshi.adapter(Config::class.java)
                val config = adapter.fromJson(strConfig)
                App.config = config!!
            }
            catch (e:Exception)
            {
                App.exceptions.add(
                    ExceptionLog(
                        title = "反序列化配置文件",
                        exception = e,
                        time = TimeHelper.getTime()
                    )
                )
                App.config = getDefaultConfig()
            }
        }

        // 加载酷我平台Headers
        _binding.wvView.settings.javaScriptEnabled = true
        _binding.wvView.settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36 Edg/115.0.1901.188"
        _binding.wvView.webViewClient = KuWoWebViewClient()
        _binding.wvView.loadUrl("http://kuwo.cn")

        // 初始化用户平台歌单
        CoroutineScope(Dispatchers.IO).launch {
            ServiceProxy.all().forEach {
                var data:Any = ""
                var jumpParams = ""
                when(it::class.java){
                    NetEaseCloudService::class.java ->{
                        App.config.netEaseCloudMusicConfig.let {
                            data = "${it.uid};${it.csrf_token}"
                            jumpParams = it.csrf_token!!
                        }
                    }
                }
                val result = it.getUserSheet(data)
                val platform = it.getPlatform()
                if(result.exception == null && result.support && result.data != null){
                    withContext(Dispatchers.Main){
                        val adapter = SongSheetAdapter()
                        adapter.showPic = true
                        val view = layoutInflater.inflate(R.layout.component_sheet,null)
                        val rv = view.findViewById<RecyclerView>(R.id.rv_list)
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
                        adapter.submitList(result.data)
                        _binding.llSheet.addView(view)
                    }
                }

                if(it == ServiceProxy.all().last()){
                    withContext(Dispatchers.Main){
                        showContent()
                    }
                }
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
                    toast("please input key about your want to search")
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

        if(App.playListIsInitialized() && !(App.playList.data.isNullOrEmpty())){
            onMediaChangeEvent(MediaChangeEvent(App.playList.data.get(App.playList.index)))
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
        super.onStop()
    }
    private fun getDefaultConfig():Config {
        return Config(
            okhttp_request_timeout = 3000,
            ffmpeg_parse_timeout = 3000,
            data_sync_platform = Platform.NetEaseCloud,
            default_search_platform = Platform.KuWo,
            auto_change_platform = true,
            priority_auto_change_platform = Platform.KuWo,
            ffmpeg_parse_quality_audition = FmgQuality.HQ,
            ffmpeg_parse_quality_upload = FmgQuality.SQ,
            netEaseCloudMusicConfig = NetEaseCloudMusicConfig(
                csrf_token = "",
                music_a = "",
                quality = NetEaseCloudMusicConfig.qualities.get("无损")!!,
                uid = "",
                name = ""
            ),
            qqMusicConfig = QQMusicConfig(
                cookies = HashMap<String,String>()
            ),
            kuGouMusicConfig = KuGouMusicConfig(
                cookies = HashMap<String,String>()
            ),
            kuWoMusicConfig = KuWoMusicConfig(
                cookies = HashMap<String,String>(),
                quality = KuWoMusicConfig.qualities.getOrDefault("flac","flac")
            ),
            memFireDbConfig = MemFireDbConfig(
                url = "",
                apiKey = "",
                user = null,
                upload_favorite_artist = true,
                upload_exception = true,
                upload_video_bind_lyric_info = true
            )
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMediaChangeEvent(event: MediaChangeEvent){
        val media = event.media
        if(media.video == null){
            _binding.layoutPlayBar.tvName.text = media.audio!!.name
            Glide.with(_binding.layoutPlayBar.ivPic)
                .asBitmap()
                .load(media.audio!!.pic)
                .placeholder(R.drawable.ic_music)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(10)))
                .into(_binding.layoutPlayBar.ivPic)
        }
        _binding.layoutPlayBar.lpiPlay.progress = 0
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMediaPositionChangeEvent(event:MediaPositionChangeEvent){
        if(!App.playListIsInitialized() || App.playList.lyrics == null)
            return
        val lyric = App.playList.lyrics!!.current(event.position)
        _binding.layoutPlayBar.tvLyric.text = lyric.text
        _binding.layoutPlayBar.lpiPlay.progress = (event.position*1.0/App.playList.lyrics!!.lyrics.last().time*100).toInt()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMediaLoadingEvent(event:MediaLoadingEvent){
        // 加载歌词完成后重置状态
        _binding.layoutPlayBar.tvLyric.text = "loading..."
        _binding.layoutPlayBar.cpiLoading.visibility = View.VISIBLE
        _binding.layoutPlayBar.ibPlayStatus.visibility = View.GONE
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLyricsLoadingEvent(event: LyricsLoadingEvent){
        if(event.finish){
            _binding.layoutPlayBar.tvLyric.text = ""
            if(App.playListIsInitialized()){
                if(App.playList.lyrics != null){
                    _binding.layoutPlayBar.tvLyric.text = App.playList.lyrics!!.lyrics.first().text
                }
                else{
                    _binding.layoutPlayBar.tvLyric.text = "i'm sorry, but no data was requested"
                }
            }
            _binding.layoutPlayBar.cpiLoading.visibility = View.GONE
            _binding.layoutPlayBar.ibPlayStatus.visibility = View.VISIBLE
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlayerStatusChangeEvent(event: PlayerStatusChangeEvent){
        _binding.layoutPlayBar.ibPlayStatus.setImageResource(if(event.playing) R.drawable.ic_pause else R.drawable.ic_play)
    }

    fun showContent(){
        _binding.stateLayout.apply {
            onContent {
                this@apply.startAnimation()
            }
            this.showContent()
        }
    }
}