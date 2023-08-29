package com.jqwong.music.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.children
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.drake.statelayout.StateLayout
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.jqwong.music.R
import com.jqwong.music.app.App
import com.jqwong.music.databinding.ActivitySettingBinding
import com.jqwong.music.helper.*
import com.jqwong.music.model.Config
import com.jqwong.music.model.ExceptionLog
import com.jqwong.music.model.Platform
import com.jqwong.music.service.NetEaseCloudService
import com.jqwong.music.service.ServiceProxy
import kotlinx.coroutines.*

/**
 * @author: Jq
 * @date: 7/23/2023
 */
class SettingActivity:BaseActivity<ActivitySettingBinding>() {
    override fun initData(savedInstanceState: Bundle?) {

    }
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("RestrictedApi")
    override fun intView() {
        setSupportActionBar(_binding.includeToolbar.toolbar)
        supportActionBar?.title = "设置"
        _binding.includeToolbar.toolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                finish()
            }
        })
        App.config.let {
            _binding.inputOkhttpRequestTimeout.setText(it.okhttp_request_timeout.toString())
            _binding.inputMaxRetryCount.setText(it.retry_max_count.toString())
            _binding.smExitClearCache.isChecked = it.exit_clear_cache
            _binding.smAutoChangePlatform.isChecked = it.allow_auto_change_platform
            _binding.smUseFfmpeg.isChecked = it.allow_use_ffmpeg_parse
            _binding.smUseFfmpegOnlyWifi.isChecked = it.only_wifi_use_ffmpeg_parse
            (_binding.menuDefaultSearchPlatform.editText as? AutoCompleteTextView)?.let{
                setDropdownDefaultBackground(it)
                val list = mutableListOf<String>()
                for(item in Platform.values()){
                    list.add(item.name)
                }
                it.setAdapter(ArrayAdapter(this,R.layout.item_drop_down_text, list))
                it.hint = App.config.default_search_platform.name
            }
        }
        _binding.linkConfigKuwo.setOnClickListener {
            MaterialDialog(this, BottomSheet()).show {
                customView(R.layout.dialog_config_kuwo)
                cornerRadius(20f)
                view.setBackgroundResource(R.drawable.bg_dialog)
                title(text = "酷我音乐配置")
                view.setTitleDefaultStyle(this@SettingActivity)
                val wrapper = view.contentLayout.findViewById<LinearLayout>(R.id.ll_wrapper_cookie)
                var number = 1
                App.config.kuWoConfig.cookies.forEach {
                    val iv = layoutInflater.inflate(R.layout.item_cookie,null)
                    iv.findViewById<TextView>(R.id.tv_number).text = "#$number"
                    iv.findViewById<TextInputEditText>(R.id.input_name).setText(it.key)
                    iv.findViewById<TextInputEditText>(R.id.input_value).setText(it.value)
                    iv.findViewById<ImageButton>(R.id.btn_remove).setOnClickListener {
                        wrapper.removeView(iv)
                        number = 1
                        wrapper.children.forEach {
                            it.findViewById<TextView>(R.id.tv_number).text = "#$number"
                            number++
                        }
                    }
                    wrapper.addView(iv)
                    number++
                }
                view.contentLayout.findViewById<ImageButton>(R.id.btn_add_cookie).setOnClickListener {
                    val iv = layoutInflater.inflate(R.layout.item_cookie,null)
                    iv.findViewById<TextView>(R.id.tv_number).text = "#${wrapper.childCount+1}"
                    wrapper.addView(iv)
                }
                view.contentLayout.findViewById<CircularProgressButton>(R.id.btn_save).setOnClickListener {
                    (it as CircularProgressButton).startAnimation()
                    try {
                        val map = mutableMapOf<String,String>()
                        wrapper.children.forEach {
                            val name = it.findViewById<TextInputEditText>(R.id.input_name).text
                            val value = it.findViewById<TextInputEditText>(R.id.input_value).text
                            if(name != null && value != null && name.isNotEmpty()){
                                map.put(name.toString(), value.toString())
                            }
                        }
                        App.config.kuWoConfig.cookies.clear()
                        App.config.kuWoConfig.cookies.putAll(map)
                        App.config.save(this@SettingActivity)
                        loadingButtonFinishAnimation(it,true,"保存成功")
                    }
                    catch (e:Exception){
                        val log = ExceptionLog(
                            title = "Save KuWo Config",
                            exception = e,
                            time = TimeHelper.getTime()
                        )
                        App.exceptions.add(log)
                        loadingButtonFinishAnimation(it,false,"保存失败: ${log.exception.message}")
                    }
                }
            }
        }
        _binding.linkConfigNetEaseCloud.setOnClickListener {
            MaterialDialog(this,BottomSheet()).show {
                customView(R.layout.dialog_config_neteasecloud)
                cornerRadius(20f)
                view.setBackgroundResource(R.drawable.bg_dialog)
                title(text = "NetEaseCloud config")
                view.setTitleDefaultStyle(this@SettingActivity)
                val menuQuality = view.contentLayout.findViewById<TextInputLayout>(R.id.menu_quality)
                (menuQuality.editText as? AutoCompleteTextView)?.let{
                    setDropdownDefaultBackground(it)
                    val list = mutableListOf<String>()
                    var hint = ""
                    Config.NetEaseCloudConfig.qualities.forEach {
                        list.add(it.key)
                        if(it.value == App.config.netEaseCloudConfig.quality){
                            hint = it.key
                        }
                    }
                    it.setAdapter(ArrayAdapter(this@SettingActivity,R.layout.item_drop_down_text,list))
                    it.hint = hint
                }
                val tvUid = view.contentLayout.findViewById<TextView>(R.id.tv_uid)
                val tvName = view.contentLayout.findViewById<TextView>(R.id.tv_name)
                val tvToken = view.contentLayout.findViewById<TextView>(R.id.tv_token)
                val tvMusicA = view.contentLayout.findViewById<TextView>(R.id.tv_music_a)
                App.config.netEaseCloudConfig.let {
                    tvUid.text = it.uid
                    tvName.text = it.name
                    tvToken.text = it.csrf_token
                    tvMusicA.text = it.music_a
                }
                view.contentLayout.findViewById<CircularProgressButton>(R.id.btn_login).setOnClickListener {
                    // 逻辑: 生成二维码,由用户使用网易云App扫描并确认授权 => 获取到用户信息以及token
                    MaterialDialog(this@SettingActivity,BottomSheet()).show {
                        customView(R.layout.dialog_login_netease)
                        cornerRadius(20f)
                        title(text = "QR Code")
                        view.setBackgroundResource(R.drawable.bg_dialog)
                        view.setTitleDefaultStyle(this@SettingActivity)
                        val stateLayout = view.contentLayout.findViewById<StateLayout>(R.id.state_layout)
                        stateLayout.showLoading()
                        val ivQr = view.contentLayout.findViewById<ImageView>(R.id.iv_qr)
                        val btnAuthentication = view.contentLayout.findViewById<CircularProgressButton>(R.id.btn_authentication)
                        var uniKey = ""
                        // 显示二维码
                        CoroutineScope(Dispatchers.IO).launch {
                            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                (ServiceProxy.getService(Platform.NetEaseCloud).data as NetEaseCloudService).getLoginUniKey()
                            } else {
                                TODO("VERSION.SDK_INT < TIRAMISU")
                            }
                            withContext(Dispatchers.Main){
                                if(result.exception != null){
                                    toast(result.exception.exception.message.toString())
                                    stateLayout.error(result.exception)
                                }
                                else{
                                    uniKey = result.data!!
                                    val url = "https://music.163.com/login?codekey=${result.data}"
                                    ivQr.setImageBitmap(QrHelper.createQRCodeBitmap(url,250,250,"UTF-8","L","1",
                                        Color.BLACK,Color.WHITE))
                                    stateLayout.showContent()
                                }
                            }
                        }
                        // 用户认证
                        btnAuthentication.setOnClickListener {
                            if(uniKey.isNullOrEmpty())
                                return@setOnClickListener
                            (it as CircularProgressButton).startAnimation()
                            CoroutineScope(Dispatchers.IO).launch {
                                val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    (ServiceProxy.getService(Platform.NetEaseCloud).data as NetEaseCloudService).loginCheck(uniKey)
                                } else {
                                    TODO("VERSION.SDK_INT < TIRAMISU")
                                }
                                withContext(Dispatchers.Main){
                                    if (result.exception != null){
                                        loadingButtonFinishAnimation(it,false,result.exception.exception.message.toString())
                                    }
                                    else{
                                        val config = result.data!!.data as Config.NetEaseCloudConfig
                                        App.config.netEaseCloudConfig.csrf_token = config.csrf_token
                                        App.config.netEaseCloudConfig.music_a = config.music_a
                                        tvToken.setText(config.csrf_token)
                                        tvMusicA.setText(config.music_a)
                                        withContext(Dispatchers.IO){
                                            val result = (ServiceProxy.getService(Platform.NetEaseCloud).data as NetEaseCloudService).getUserInfo(config.csrf_token!!)
                                            withContext(Dispatchers.Main){
                                                if(result.data != null){
                                                    tvUid.text = result.data.uid
                                                    tvName.text = result.data.name
                                                }
                                            }
                                        }
                                        loadingButtonFinishAnimation(it,true,"authentication success")
                                    }
                                }
                            }
                        }
                    }
                }
                view.contentLayout.findViewById<CircularProgressButton>(R.id.btn_save).setOnClickListener {
                    (it as CircularProgressButton).startAnimation()
                    App.config.netEaseCloudConfig.let {
                        it.uid = tvUid.text.toString()
                        it.name = tvName.text.toString()
                        it.music_a = tvMusicA.text.toString()
                        it.csrf_token = tvToken.text.toString()
                    }
                    loadingButtonFinishAnimation(it,true,"保存成功")
                }
            }
        }
    }
    override fun useEventBus(): Boolean {
        return false
    }
    override fun statusBarColor(): Int {
        return R.color.background
    }
    override fun onDestroy() {
        super.onDestroy()
        App.config.let {
            it.okhttp_request_timeout = _binding.inputOkhttpRequestTimeout.text.toString().toLong()
            it.retry_max_count = _binding.inputMaxRetryCount.text.toString().toInt()
            it.exit_clear_cache = _binding.smExitClearCache.isChecked
            it.allow_auto_change_platform = _binding.smAutoChangePlatform.isChecked
            it.only_wifi_use_ffmpeg_parse = _binding.smUseFfmpegOnlyWifi.isChecked
            it.allow_use_ffmpeg_parse = _binding.smUseFfmpeg.isChecked
            _binding.menuDefaultSearchPlatform.editText.let {
                if(!it?.text.isNullOrEmpty()){
                    App.config.default_search_platform = Platform.valueOf(it?.text.toString())
                }
            }
        }
    }
    private fun setDropdownDefaultBackground(view:AutoCompleteTextView){
        view.setDropDownBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this,R.color.dropdown_menu_background)))
    }
    private fun loadingButtonFinishAnimation(btn:CircularProgressButton,success:Boolean,message:String){
        CoroutineScope(Dispatchers.IO).launch {
            delay(1500)
            withContext(Dispatchers.Main){
                val bitmap = AppCompatResources.getDrawable(this@SettingActivity,if(success) R.drawable.ic_yes else R.drawable.ic_no)!!.toBitmap()
                btn.doneLoadingAnimation(R.color.white,bitmap)
                withContext(Dispatchers.IO){
                    delay(500)
                    withContext(Dispatchers.Main){
                        btn.revertAnimation{
                            btn.background = resources.getDrawable(R.drawable.bg_button)
                        }
                        toast(message)
                    }
                }
            }
        }
    }
}