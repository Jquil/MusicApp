package com.jqwong.music.event

import com.jqwong.music.model.Platform

class SyncUserSheetEvent(val platform: Platform,val sync:Boolean,val callback:(success:Boolean,message:String) -> Unit)