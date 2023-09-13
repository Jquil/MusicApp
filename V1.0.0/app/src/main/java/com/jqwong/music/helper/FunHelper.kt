package com.jqwong.music.helper

import okhttp3.MediaType
import okhttp3.RequestBody

fun String.toRam():RequestBody{
    return RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),this)
}