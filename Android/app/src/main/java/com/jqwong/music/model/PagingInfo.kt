package com.jqwong.music.model

import com.chad.library.adapter.base.QuickAdapterHelper

/**
 * @author: Jq
 * @date: 4/29/2023
 */
class PagingInfo(
    var page:Int,
    var pageSize:Int,
    var finish:Boolean,
    var helper: QuickAdapterHelper?
)