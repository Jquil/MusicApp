package com.jqwong.music.view

import android.os.Bundle
import com.jqwong.music.R
import com.jqwong.music.databinding.ActivityLyricBinding

/**
 * @author: Jq
 * @date: 7/24/2023
 */
class LyricActivity:BaseActivity<ActivityLyricBinding>() {
    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun intView() {
    }

    override fun useEventBus(): Boolean {
        return true
    }

    override fun statusBarColor(): Int {
        return R.color.background
    }
}