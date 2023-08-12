package com.jqwong.music.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.CalendarContract.Colors
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
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
import com.jqwong.music.helper.QrHelper
import com.jqwong.music.helper.TimeHelper
import com.jqwong.music.helper.setTitleDefaultStyle
import com.jqwong.music.model.ExceptionLog
import com.jqwong.music.model.FmgQuality
import com.jqwong.music.model.NetEaseCloudMusicConfig
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
        supportActionBar?.title = "Setting"
        _binding.includeToolbar.toolbar.setNavigationOnClickListener {
            finish()
        }
        _binding.inputOkhttpRequestTimeout.setText(App.config.okhttp_request_timeout.toString())
        _binding.inputFfmpegParseTimeout.setText(App.config.ffmpeg_parse_timeout.toString())
        val fmgQualities = mutableListOf<String>()
        for(item in FmgQuality.values()){
            fmgQualities.add(item.name)
        }
        val adapterFmgQuality = ArrayAdapter(this,R.layout.item_drop_down_text,fmgQualities)
        (_binding.menuFpgQualityAudition.editText as? AutoCompleteTextView)?.let {
            setDropdownDefaultBackground(it)
            it.setAdapter(adapterFmgQuality)
            it.hint = (App.config.ffmpeg_parse_quality_audition.name)
        }
        (_binding.menuFpgQualityUpload.editText as? AutoCompleteTextView)?.let{
            setDropdownDefaultBackground(it)
            it.setAdapter(adapterFmgQuality)
            it.hint = App.config.ffmpeg_parse_quality_upload.name
        }

        (_binding.menuDefaultSearchSource.editText as? AutoCompleteTextView)?.let{
            setDropdownDefaultBackground(it)
            it.setAdapter(ArrayAdapter(this,R.layout.item_drop_down_text, listOf(Platform.KuWo,Platform.NetEaseCloud)))
            it.hint = App.config.default_search_platform.name
        }
        (_binding.menuSyncPlatformData.editText as? AutoCompleteTextView)?.let{
            setDropdownDefaultBackground(it)
            it.setAdapter(ArrayAdapter(this,R.layout.item_drop_down_text, listOf(Platform.NetEaseCloud)))
            it.hint = App.config.data_sync_platform.name
        }
        (_binding.menuAutoChangePlatform.editText as? AutoCompleteTextView)?.let{
            setDropdownDefaultBackground(it)
            it.setAdapter(ArrayAdapter(this,R.layout.item_drop_down_text, listOf(Platform.KuWo,Platform.NetEaseCloud)))
            it.hint = App.config.priority_auto_change_platform.name
        }
        _binding.smAutoChangePlatform.isChecked = App.config.auto_change_platform
        _binding.linkConfigKuwo.setOnClickListener {
            MaterialDialog(this,BottomSheet()).show {
                customView(R.layout.dialog_config_kuwo)
                cornerRadius(20f)
                view.setBackgroundResource(R.drawable.bg_dialog)
                title(text = "Kuwo config")
                view.setTitleDefaultStyle(this@SettingActivity)
                val wrapper = view.contentLayout.findViewById<LinearLayout>(R.id.ll_wrapper_cookie)
                var number = 1
                App.config.kuWoMusicConfig.cookies.forEach {
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
                        App.config.kuWoMusicConfig.cookies.clear()
                        App.config.kuWoMusicConfig.cookies.putAll(map)
                        App.config.save(this@SettingActivity)
                        loadingButtonFinishAnimation(it,true,"save success")
                    }
                    catch (e:Exception){
                        val log = ExceptionLog(
                            title = "Save KuWo Config",
                            exception = e,
                            time = TimeHelper.getTime()
                        )
                        App.exceptions.add(log)
                        loadingButtonFinishAnimation(it,false,"save failed: ${log.exception.message}")
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
                    NetEaseCloudMusicConfig.qualities.forEach {
                        list.add(it.key)
                        if(it.value == App.config.netEaseCloudMusicConfig.quality){
                            hint = it.key
                        }
                    }
                    it.setAdapter(ArrayAdapter(this@SettingActivity,R.layout.item_drop_down_text,list))
                    it.hint = hint
                }
                val tvToken = view.contentLayout.findViewById<TextView>(R.id.tv_token)
                val tvMusicA = view.contentLayout.findViewById<TextView>(R.id.tv_music_a)
                tvToken.setText(App.config.netEaseCloudMusicConfig.csrf_token)
                tvMusicA.setText(App.config.netEaseCloudMusicConfig.music_a)
                view.contentLayout.findViewById<AppCompatButton>(R.id.btn_login).setOnClickListener {
                    // 生成二维码
                    MaterialDialog(this@SettingActivity,BottomSheet()).show {
                        customView(R.layout.dialog_login_netease)
                        cornerRadius(20f)
                        title(text = "QR Code")
                        view.setBackgroundResource(R.drawable.bg_dialog)
                        view.setTitleDefaultStyle(this@SettingActivity)
                        val stateLayout = view.contentLayout.findViewById<StateLayout>(R.id.state_layout)
                        stateLayout.showLoading()
                        val ivQr = view.contentLayout.findViewById<ImageView>(R.id.iv_qr)
                        val btnAuthentication = view.contentLayout.findViewById<AppCompatButton>(R.id.btn_authentication)
                        var key = ""
                        CoroutineScope(Dispatchers.IO).launch {
                            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                (ServiceProxy.getService(Platform.NetEaseCloud).data as NetEaseCloudService).getLoginUniKey()
                            } else {
                                TODO("VERSION.SDK_INT < TIRAMISU")
                            }
                            withContext(Dispatchers.Main){
                                if(result.exception != null){
                                    toast(result.exception.exception.message.toString())
                                }
                                else{
                                    key = result.data!!
                                    val url = "https://music.163.com/login?codekey=${result.data}"
                                    ivQr.setImageBitmap(QrHelper.createQRCodeBitmap(url,250,250,"UTF-8","L","1",
                                        Color.BLACK,Color.WHITE))
                                    stateLayout.showContent()
                                }
                            }
                        }
                        btnAuthentication.setOnClickListener {
                            if(key.isNullOrEmpty())
                                return@setOnClickListener
                            CoroutineScope(Dispatchers.IO).launch {
                                val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    (ServiceProxy.getService(Platform.NetEaseCloud).data as NetEaseCloudService).loginCheck(key)
                                } else {
                                    TODO("VERSION.SDK_INT < TIRAMISU")
                                }
                                withContext(Dispatchers.Main){
                                    if (result.exception != null){
                                        toast(result.exception.exception.message.toString())
                                    }
                                    else{
                                        toast(result.data!!.message)
                                        if(result.data.data != null){
                                            val config = result.data.data as NetEaseCloudMusicConfig
                                            App.config.netEaseCloudMusicConfig.csrf_token = config.csrf_token
                                            App.config.netEaseCloudMusicConfig.music_a = config.music_a
                                            tvToken.setText(config.csrf_token)
                                            tvMusicA.setText(config.music_a)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                view.contentLayout.findViewById<AppCompatButton>(R.id.btn_save).setOnClickListener {

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
        _binding.inputOkhttpRequestTimeout.text.toString().let {
            var value:Long = 0
            if (it != ""){
                value = it.toLong()
            }
            if(value != App.config.okhttp_request_timeout){
                App.config.okhttp_request_timeout = value
                // reset okhttp timeout
            }
        }
        _binding.inputFfmpegParseTimeout.text.toString().let {
            var value:Long = 0
            if(it != ""){
                value = it.toLong()
            }
            App.config.ffmpeg_parse_timeout = value
        }
        _binding.menuFpgQualityAudition.editText?.text.toString().let {
            if(it != ""){
                val value = FmgQuality.valueOf(it)
                App.config.ffmpeg_parse_quality_audition = value
            }
        }
        _binding.menuFpgQualityUpload.editText?.text.toString().let {
            if(it != ""){
                val value = FmgQuality.valueOf(it)
                App.config.ffmpeg_parse_quality_upload = value
            }
        }
        _binding.menuDefaultSearchSource.editText?.text.toString().let {
            if(it != ""){
                val value = Platform.valueOf(it)
                App.config.default_search_platform = value
            }
        }
        _binding.menuSyncPlatformData.editText?.text.toString().let {
            if(it != ""){
                val value = Platform.valueOf(it)
                App.config.data_sync_platform = value
            }
        }
        _binding.menuAutoChangePlatform.editText?.text.toString().let {
            if(it != ""){
                val value = Platform.valueOf(it)
                App.config.priority_auto_change_platform = value
            }
        }
        App.config.auto_change_platform = _binding.smAutoChangePlatform.isChecked
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