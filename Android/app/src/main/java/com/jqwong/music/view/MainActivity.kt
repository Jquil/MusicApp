package com.jqwong.music.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.annotation.RequiresApi
import androidx.core.view.GravityCompat
import com.jqwong.music.R
import com.jqwong.music.app.App
import com.jqwong.music.app.Constant
import com.jqwong.music.databinding.ActivityMainBinding
import com.jqwong.music.helper.TimeHelper
import com.jqwong.music.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * @author: Jq
 * @date: 7/23/2023
 */
class MainActivity:BaseActivity<ActivityMainBinding>() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun initData(savedInstanceState: Bundle?) {
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
                cookies = HashMap<String, String>(),
                csrf_token = "",
                username = "",
                pic = "",
                quality = NetEaseCloudMusicConfig.qualities.get("无损")!!
            ),
            qqMusicConfig = QQMusicConfig(
                cookies = HashMap<String,String>()
            ),
            kuGouMusicConfig = KuGouMusicConfig(
                cookies = HashMap<String,String>()
            ),
            kuWoMusicConfig = KuWoMusicConfig(
                cookies = HashMap<String,String>().apply {
                    put("Cookie","Hm_lvt_cdb524f42f0ce19b169a8071123a4797=1689694107; _ga=GA1.2.259721034.1689694749; _gid=GA1.2.1715768254.1689694749; Hm_lpvt_cdb524f42f0ce19b169a8071123a4797=1689770825; _ga_ETPBRPM9ML=GS1.2.1689770827.2.0.1689770827.60.0.0; Hm_Iuvt_cdb524f42f0ce19b169b8072123a4727=3MiWHX6n8Zr8sN48sF3dccyTWjZ54Hxy")
                    put("Secret","6d11133bb2dcdb6786619f78d7a8adc54ef3caecde8e45df2f1656e490f16f42053e556b")
                    put("Referer","https://www.kuwo.cn")
                },
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
}