package com.jqwong.music.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton
import com.jqwong.music.R
import com.jqwong.music.app.App
import com.jqwong.music.databinding.ActivityAboutBinding
import com.jqwong.music.helper.DownloadHelper
import com.jqwong.music.helper.UpdateHelper
import com.jqwong.music.helper.delayFinish
import com.jqwong.music.helper.setTitleDefaultStyle
import com.jqwong.music.model.DownloadTask

/**
 * @author: Jq
 * @date: 9/1/2023
 */
class AboutActivity:BaseActivity<ActivityAboutBinding>() {
    override fun initData(savedInstanceState: Bundle?) {
        _binding.tvContent.text =
                "1. 本软件为开源项目，数据来源于各官方音乐平台，因此不保证数据准确性 \n\n" +
                "2. 该项目为本人报以学习为目的研究，不进行任何形式商业用途，若有侵犯到任何人的合法利益，请及时联系我，我将第一时间删库跑路\n\n" +
                "3. 该项目运行时需要网络访问以及存储文件的权限，为了保证您的正常使用请授予需要的权限\n\n" +
                "4. 个人隐私问题，您产生的数据只会保存于本地，以及上传至各官方音乐平台\n\n" +
                "5. 确保后台运行正常播放，需要您手动锁定该应用，并设置后台运行模式为'无限制'"
    }

    override fun intView() {
        _binding.includeToolbar.toolbar.elevation = 0f
        _binding.includeToolbar.toolbar.setNavigationOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                finish()
            }
        })
        _binding.btnGithub.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.data = Uri.parse("https://github.com/Jquil/MusicApp")
                startActivity(intent)
            }
            catch (e:Exception){
                toast(e.message.toString())
            }
        }
        _binding.btnEmail.setOnClickListener {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Label", (it as AppCompatButton).text)
            clipboardManager.setPrimaryClip(clipData)
            toast("已复制到剪贴板咯")
        }
        _binding.btnCheckUpdate.setOnClickListener {
            fun show(){
                val number = packageManager.getPackageInfo(packageName,0).versionCode
                if(App.newestVersion!!.number == number){
                    toast("已经是最新版本咯")
                    return
                }
                MaterialDialog(this, BottomSheet()).show {
                    customView(R.layout.dialog_version_application)
                    cornerRadius(20f)
                    view.setBackgroundResource(R.drawable.bg_dialog)
                    title(text = "应用更新")
                    view.setTitleDefaultStyle(this@AboutActivity)
                    val tvVersion = view.contentLayout.findViewById<TextView>(R.id.tv_version)
                    val tvLog = view.contentLayout.findViewById<TextView>(R.id.tv_log)
                    val btnUpdate = view.contentLayout.findViewById<CircularProgressButton>(R.id.btn_update)
                    App.newestVersion!!.let {
                        tvVersion.text = it.version
                        tvLog.text = it.log
                    }
                    btnUpdate.setOnClickListener {
                        if(App.newestVersion!!.number == number){
                            toast("已经是最新版本咯")
                            return@setOnClickListener
                        }
                        val id = "${App.newestVersion!!.version}.apk"
                        val path = "${cacheDir}/${id}"
                        if(DownloadHelper.exist(id)){
                            if(DownloadHelper.downloading(id)){
                                toast("正在下载咯")
                                return@setOnClickListener
                            }
                        }
                        val task = DownloadTask(
                            id,
                            name = id,
                            App.newestVersion!!.path,
                            path,
                            finish = false,
                            client = null
                        ){
                            runOnUiThread {
                                UpdateHelper.install(path)
                            }
                        }
                        toast("开始下载咯")
                        DownloadHelper.add(task)
                    }
                }
            }
            val btn = it
            if(App.newestVersion == null){
                (it as CircularProgressButton).startAnimation()
                UpdateHelper.newest {
                    runOnUiThread {
                        if(it.exception != null) {
                            (btn as CircularProgressButton).delayFinish(this@AboutActivity,false,it.exception.exception.message.toString(), isToast = true)
                            return@runOnUiThread
                        }
                        if(!it.success){
                            (btn as CircularProgressButton).delayFinish(this@AboutActivity,false,it.message, isToast = true)
                            return@runOnUiThread
                        }
                        App.newestVersion = it.data
                        (btn as CircularProgressButton).delayFinish(this@AboutActivity,true,"", isToast = false)
                        show()
                    }
                }
            }
            else{
                show()
            }
        }
    }

    override fun useEventBus(): Boolean {
        return false
    }

    override fun statusBarColor(): Int {
        return R.color.background
    }
}