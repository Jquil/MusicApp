package com.jqwong.music.model

import com.chad.library.adapter.base.QuickAdapterHelper

class LoadInfo(
    var page:Int = 1,
    var size:Int = 20,
    var finish:Boolean = false,
    var helper:QuickAdapterHelper? = null
)