package com.jqwong.music.event

import com.jqwong.music.model.LyricStatus
import com.jqwong.music.model.Lyrics

class LyricsLoadingEvent(val info: Pair<LyricStatus, Lyrics?>)