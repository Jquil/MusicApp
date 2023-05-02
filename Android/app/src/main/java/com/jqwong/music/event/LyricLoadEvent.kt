package com.jqwong.music.event

import com.jqwong.music.model.Lyric

/**
 * @author: Jq
 * @date: 5/1/2023
 */
class LyricLoadEvent(val status: LyricLoadStatus,val lyric: Lyric?)

enum class LyricLoadStatus{
    LOADING,SUCCESS,ERROR
}