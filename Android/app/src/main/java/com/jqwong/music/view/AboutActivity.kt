package com.jqwong.music.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import com.jqwong.music.R
import com.jqwong.music.databinding.ActivityAboutBinding

/**
 * @author: Jq
 * @date: 9/1/2023
 */
class AboutActivity:BaseActivity<ActivityAboutBinding>() {
    override fun initData(savedInstanceState: Bundle?) {

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
        _binding.btnUpgrade.setOnClickListener {
            toast("还没开放该功能噢")
        }
    }

    override fun useEventBus(): Boolean {
        return false
    }

    override fun statusBarColor(): Int {
        return R.color.background
    }
}