package com.jqwong.music.service

import android.os.Build
import androidx.annotation.RequiresApi
import com.jqwong.music.api.KuWoMusicApi
import com.jqwong.music.api.NetEaseCloudMusicApi
import com.jqwong.music.app.App
import com.jqwong.music.helper.FunHelper
import com.jqwong.music.helper.awaitResult
import com.jqwong.music.helper.toJson
import com.jqwong.music.model.Artist
import com.jqwong.music.model.Config
import com.jqwong.music.model.Leaderboard
import com.jqwong.music.model.Lyrics
import com.jqwong.music.model.Media
import com.jqwong.music.model.Platform
import com.jqwong.music.model.Response
import com.jqwong.music.model.SongSheet
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.URLEncoder
import java.security.MessageDigest
import java.util.Base64
import java.util.concurrent.TimeUnit
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class NetEaseCloudService:IService {

    companion object{
        private val config = OkHttpClient.Builder()
            .connectTimeout(App.config.okhttp_request_timeout, TimeUnit.MILLISECONDS)
            .addInterceptor {
                val req = it.request()
                val builder = req.newBuilder()
                App.config.netEaseCloudMusicConfig.cookies.forEach { s, s2 ->
                    builder.header(s,s2)
                }
                it.proceed(builder.build())
            }
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

        private val service = Retrofit.Builder()
            .baseUrl("https://music.163.com/")
            .client(config.build())
            .addConverterFactory(
                MoshiConverterFactory
                .create(Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()))
            .build()
            .create(NetEaseCloudMusicApi::class.java)
    }

    override suspend fun getLeaderboard(): Response<List<Leaderboard>> {
        val title = FunHelper.getName()
        val result = service.getLeaderboard().awaitResult()
        return if(result.e != null){
            return error(title,result.e)
        }
        else{
            val data = result.data!!.list
            val list = mutableListOf<Leaderboard>()
            data.forEach{
                list.add(Leaderboard(
                        platform = Platform.NetEaseCloud,
                        id = it.id.toString(),
                        name = it.name,
                        children = null
                    )
                )
            }
            return Response(
                title = title,
                support = true,
                success = true,
                exception = null,
                message = "ok",
                data = list
            )
        }
    }

    override suspend fun getLeaderboardSongList(
        id: String,
        page: Int,
        limit: Int
    ): Response<List<Media>> {
        TODO("Not yet implemented")
    }

    override suspend fun getArtistSongList(id: Long, page: Int, limit: Int): Response<List<Media>> {
        TODO("Not yet implemented")
    }

    override suspend fun getArtistInfo(id: Long): Response<Artist> {
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override suspend fun search(key: String, page: Int, limit: Int): Response<List<Media>> {
        val title = FunHelper.getName()
        val map = mapOf(
            "s" to key,
            "type" to 1,
            "limit" to limit,
            "offset" to (page-1)*limit,
            "total" to true
        )
        val params = EncryptHelper.eApi("/api/cloudsearch/pc",map.toJson())
        val result = service.search(params).awaitResult()
        return if(result.e != null){
            return error(title,result.e)
        }
        else{
            val list = mutableListOf<Media>()
            result.data!!.result.songs.forEach {
                list.add(it.convert())
            }
            return Response(
                title = title,
                success = true,
                data = list,
                exception = null,
                message = "ok",
                support = true
            )
        }
    }

    override suspend fun getRecommendSongSheetList(data: Any): Response<List<SongSheet>> {
        TODO("Not yet implemented")
    }

    override suspend fun getRecommendSongSheetData(
        data: Any,
        page: Int,
        limit: Int
    ): Response<List<Media>> {
        TODO("Not yet implemented")
    }

    override suspend fun getRecommendDaily(data: Any): Response<List<Media>> {
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override suspend fun getPlayUrl(id: String, quality: Any): Response<String> {
        val map = mapOf(
            "ids" to "[${id}]",
            "encodeType" to "flac",
            "level" to quality
        )
        val params = EncryptHelper.weApi(map.toJson())
        service.getPlayUrl(params.first,params.second)
        TODO("Not yet implemented")
    }

    override suspend fun getMvUrl(id: String): Response<String> {
        TODO("Not yet implemented")
    }

    override suspend fun getLyrics(id: String): Response<Lyrics> {
        TODO("Not yet implemented")
    }


    @Suppress("Since15")
    class EncryptHelper{
        companion object{
            fun eApi(url:String,text:String):String{
                val message = "nobody${url}use${text}md5forencrypt"
                val hash = MessageDigest.getInstance("MD5").digest(message.toByteArray())
                val hex = StringBuilder(hash.size * 2)
                for (b in hash) {
                    var str = Integer.toHexString(b.toInt())
                    if (b < 0x10) {
                        str = "0$str"
                    }
                    hex.append(str.substring(str.length -2))
                }
                val data = "${url}-36cd479b6b5-${text}-36cd479b6b5-${hex}"
                val eapikey = "e82ckenh8dichen8"
                val transformation = "AES/ECB/PKCS5Padding"
                Cipher.getInstance(transformation).let {
                    val iv = Array<Byte>(16){i->0}
                    val keySpec = SecretKeySpec(eapikey.toByteArray(Charsets.UTF_8),"AES")
                    it.init(Cipher.ENCRYPT_MODE,keySpec)
                    val encryptBuffer = it.doFinal(data.toByteArray(Charsets.UTF_8))
                    val response = StringBuilder()
                    encryptBuffer.forEach {
                        var str = Integer.toHexString(it.toInt())
                        if (it < 0x10) {
                            str = "0$str"
                        }
                        response.append(str.substring(str.length -2))
                    }
                    return response.toString().uppercase()
                }
            }
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            fun weApi(text:String):Pair<String,String>{
                var encrypt_text = ""
                val iv = "0102030405060708"
                val presetKey = "0CoJUm6Qyw8W8jud"
                val encSecKey = "56e012ce37cbd833696f9a69ed67029b4ee7d01be9985d4776f6cad7b59ec804fab684f053931b053ae2e9938ee0868662f9d610815fc3780cb13d5654605bc7d5bf30d784ed86ef09ccbe2e582635a6e0d84eb35d2625a28a9495bec0905dca0984dd0bc281f29d20208b113b1c32299018b2b3d20ae9e8e2b68907eaf0a8c5"
                val secretKey = "nGJsaQv4KejANOFi"
                val transformation = "AES/CBC/PKCS5Padding"
                Cipher.getInstance(transformation).let {
                    val keySpec = SecretKeySpec(presetKey.toByteArray(Charsets.UTF_8),"AES")
                    it.init(Cipher.ENCRYPT_MODE,keySpec, IvParameterSpec(iv.toByteArray(Charsets.UTF_8)))
                    encrypt_text = Base64.getEncoder().encodeToString(it.doFinal(text.toByteArray(Charsets.UTF_8)))
                }
                Cipher.getInstance(transformation).let {
                    val keySpec = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8),"AES")
                    it.init(Cipher.ENCRYPT_MODE,keySpec, IvParameterSpec(iv.toByteArray(Charsets.UTF_8)))
                    encrypt_text = Base64.getEncoder().encodeToString(it.doFinal(encrypt_text.toByteArray(Charsets.UTF_8)))
                }
                encrypt_text = URLEncoder.encode(encrypt_text,Charsets.UTF_8)
                return Pair(encrypt_text,encSecKey)
            }
        }
    }
}