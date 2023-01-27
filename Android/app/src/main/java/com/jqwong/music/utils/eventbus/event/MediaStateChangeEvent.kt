package com.jqwong.music.utils.eventbus.event

class MediaStateChangeEvent(val state: MediaState)


enum class MediaState{
    Play,
    Pause
}