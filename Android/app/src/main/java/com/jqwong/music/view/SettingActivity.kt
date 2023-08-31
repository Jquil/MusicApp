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
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.children
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.chad.library.adapter.base.dragswipe.QuickDragAndSwipe
import com.drake.statelayout.StateLayout
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.jqwong.music.R
import com.jqwong.music.adapter.PlatformPriorityAdapter
import com.jqwong.music.app.App
import com.jqwong.music.databinding.ActivitySettingBinding
import com.jqwong.music.event.SyncUserSheetEvent
import com.jqwong.music.helper.*
import com.jqwong.music.model.ChangePlatformItem
import com.jqwong.music.model.Config
import com.jqwong.music.model.ExceptionLog
import com.jqwong.music.model.Platform
import com.jqwong.music.service.NetEaseCloudService
import com.jqwong.music.service.ServiceProxy
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus


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
        _binding.tvConfigAutoChangePlatform.setOnClickListener {
            MaterialDialog(this, BottomSheet()).show {
                customView(R.layout.dialog_config_platform_priority)
                cornerRadius(20f)
                view.setBackgroundResource(R.drawable.bg_dialog)
                title(text = "自动切换平台配置")
                view.setTitleDefaultStyle(this@SettingActivity)
                val rv = view.contentLayout.findViewById<RecyclerView>(R.id.rv_list)
                rv.layoutManager = LinearLayoutManager(this@SettingActivity)
                val adapter = PlatformPriorityAdapter()
                val list = App.config.change_platform_priority.toList()
                list.sortedBy { item -> item.index }
                adapter.submitList(App.config.change_platform_priority)
                rv.adapter = adapter
                val quickDragAndSwipe = QuickDragAndSwipe()
                    .setDragMoveFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN)
                quickDragAndSwipe.attachToRecyclerView(rv)
                    .setDataCallback(adapter)
                view.contentLayout.findViewById<AppCompatButton>(R.id.btn_save).setOnClickListener {
                    val nList = mutableListOf<ChangePlatformItem>()
                    for (i in 0 until adapter.items.size){
                        adapter.items[i].index = i
                        nList.add(adapter.items[i])
                    }
                    App.config.change_platform_priority = nList
                    App.config.save(this@SettingActivity)
                    toast("保存成功")
                }
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
                title(text = "网易云配置")
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
                val swSync = view.contentLayout.findViewById<SwitchMaterial>(R.id.smSyncSheet)
                App.config.netEaseCloudConfig.let {
                    tvUid.text = it.uid.ifEmpty { "??" }
                    tvName.text = it.name.ifEmpty { "??" }
                    tvToken.text = it.csrf_token.ifEmpty { "??" }
                    tvMusicA.text = it.music_a.ifEmpty { "??" }
                    swSync.isChecked = it.sync_user_sheet
                }

                // 点击[login]弹出dialog,可以使用手机号码&扫码登陆
                view.contentLayout.findViewById<CircularProgressButton>(R.id.btn_login).setOnClickListener {
                    MaterialDialog(this@SettingActivity,BottomSheet()).show {
                        customView(R.layout.dialog_login_neteasecloud)
                        cornerRadius(20f)
                        title(text = "登陆")
                        view.setBackgroundResource(R.drawable.bg_dialog)
                        view.setTitleDefaultStyle(this@SettingActivity)
                        val tbLayout = view.contentLayout.findViewById<TabLayout>(R.id.tl_type)
                        val wrapperPhone = view.contentLayout.findViewById<ConstraintLayout>(R.id.cl_wrapper_phone)
                        val wrapperQr = view.contentLayout.findViewById<ConstraintLayout>(R.id.cl_wrapper_qr)
                        val ivQr = view.contentLayout.findViewById<ImageView>(R.id.iv_qr)
                        val btnAuthQr = view.contentLayout.findViewById<CircularProgressButton>(R.id.btn_authentication_qr)
                        val qrStateLayout = view.contentLayout.findViewById<StateLayout>(R.id.state_layout_qr)
                        var qrUniKey = ""
                        tbLayout.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
                            override fun onTabSelected(tab: TabLayout.Tab?) {
                                when(tab?.text){
                                    this@SettingActivity.getString(R.string.qr) -> {
                                        // 申请UniKey并生成二维码
                                        wrapperPhone.visibility = View.GONE
                                        wrapperQr.visibility = View.VISIBLE
                                        qrStateLayout.showLoading()
                                        CoroutineScope(Dispatchers.IO).launch {
                                            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                (ServiceProxy.getService(Platform.NetEaseCloud).data as NetEaseCloudService).getLoginUniKey()
                                            } else {
                                                TODO("VERSION.SDK_INT < TIRAMISU")
                                            }
                                            withContext(Dispatchers.Main){
                                                if(result.exception != null){
                                                    toast(result.exception.exception.message.toString())
                                                    qrStateLayout.error(result.exception)
                                                }
                                                else{
                                                    qrUniKey = result.data!!
                                                    val url = "https://music.163.com/login?codekey=${result.data}"
                                                    ivQr.setImageBitmap(QrHelper.createQRCodeBitmap(url,250,250,"UTF-8","L","1",
                                                        Color.BLACK,Color.WHITE))
                                                    qrStateLayout.showContent()
                                                }
                                            }
                                        }
                                    }
                                    this@SettingActivity.getString(R.string.phone) -> {
                                        wrapperPhone.visibility = View.VISIBLE
                                        wrapperQr.visibility = View.GONE
                                    }
                                }
                            }

                            override fun onTabUnselected(tab: TabLayout.Tab?) {

                            }

                            override fun onTabReselected(tab: TabLayout.Tab?) {

                            }
                        })
                        btnAuthQr.setOnClickListener {
                            if(qrUniKey.isEmpty())
                                return@setOnClickListener
                            (it as CircularProgressButton).startAnimation()
                            CoroutineScope(Dispatchers.IO).launch {
                                val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    (ServiceProxy.getService(Platform.NetEaseCloud).data as NetEaseCloudService).loginCheck(qrUniKey)
                                } else {
                                    TODO("VERSION.SDK_INT < TIRAMISU")
                                }
                                withContext(Dispatchers.Main){
                                    if (result.exception != null){
                                        loadingButtonFinishAnimation(it,false,result.exception.exception.message.toString())
                                    }
                                    else{
                                        val config = result.data!!.data as Config.NetEaseCloudConfig
                                        tvToken.text = config.csrf_token
                                        tvMusicA.text = config.music_a
                                        withContext(Dispatchers.IO){
                                            val result = (ServiceProxy.getService(Platform.NetEaseCloud).data as NetEaseCloudService).getUserInfo(config.csrf_token)
                                            withContext(Dispatchers.Main){
                                                if(result.data != null){
                                                    tvUid.setText(result.data.uid)
                                                    tvName.setText(result.data.name)
                                                }
                                            }
                                        }
                                        loadingButtonFinishAnimation(it,true,"认证成功")
                                    }
                                }
                            }
                        }
                    }
                }

                // 保存
                view.contentLayout.findViewById<CircularProgressButton>(R.id.btn_save).setOnClickListener {
                    (it as CircularProgressButton).startAnimation()
                    App.config.netEaseCloudConfig.let {
                        it.uid = tvUid.text.toString()
                        it.name = tvName.text.toString()
                        it.csrf_token = tvToken.text.toString()
                        it.music_a = tvMusicA.text.toString()
                        it.sync_user_sheet = swSync.isChecked
                    }
                    EventBus.getDefault().post(SyncUserSheetEvent(Platform.NetEaseCloud,App.config.netEaseCloudConfig.sync_user_sheet){success, message ->  })
                    App.config.save(this@SettingActivity)
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