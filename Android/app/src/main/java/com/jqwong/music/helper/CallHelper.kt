package com.jqwong.music.helper

import com.jqwong.music.model.ApiResult
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback

/**
 * @author: Jq
 * @date: 7/30/2023
 */

suspend fun <T : Any> Call<T>.awaitResult(): ApiResult<T> {
    return suspendCancellableCoroutine{
            continuation ->
        enqueue(object:Callback<T>{
            override fun onResponse(call: Call<T>, response: retrofit2.Response<T>) {
                continuation.resumeWith(runCatching {
                    if(response.isSuccessful){
                        val data = response.body()
                        if(data == null){
                            ApiResult<T>(
                                data = null,
                                e = NullPointerException()
                            )
                        }
                        else{
                            ApiResult<T>(
                                data = data,
                                e = null
                            )
                        }
                    }
                    else{
                        ApiResult<T>(
                            data = null,
                            e = Exception("${response.raw().code()}:${response.raw().message()}")
                        )
                    }
                })
            }
            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWith(runCatching {
                    ApiResult<T>(
                        data = null,
                        e = Exception(t.message,t.cause)
                    )
                })
            }
        })
    }
}