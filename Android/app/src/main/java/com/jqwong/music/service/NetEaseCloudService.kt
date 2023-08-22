package com.jqwong.music.service

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.jqwong.music.api.NetEaseCloudMusicApi
import com.jqwong.music.app.App
import com.jqwong.music.helper.*
import com.jqwong.music.model.*
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

    private val TAG = "NetEaseCloudService"

    companion object{
        private val config = OkHttpClient.Builder()
            .connectTimeout(App.config.okhttp_request_timeout, TimeUnit.MILLISECONDS)
            .addInterceptor {
                val req = it.request()
                var token = ""
                var role = "MUSIC_A=bf8bfeabb1aa84f9c8c3906c04a04fb864322804c83f5d607e91a04eae463c9436bd1a17ec353cf780b396507a3f7464e8a60f4bbc019437993166e004087dd32d1490298caf655c2353e58daa0bc13cc7d5c198250968580b12c1b8817e3f5c807e650dd04abd3fb8130b7ae43fcc5b;"
                if(!App.config.netEaseCloudMusicConfig.csrf_token.isNullOrEmpty()){
                    token = "csrf=${App.config.netEaseCloudMusicConfig.csrf_token};"
                }
                if(!App.config.netEaseCloudMusicConfig.music_a.isNullOrEmpty()){
                    role = "MUSIC_U=${App.config.netEaseCloudMusicConfig.music_a};"
                }
                val builder = req.newBuilder()
                    .header("Content-Type","application/x-www-form-urlencoded")
                    .header("Cookie","NMTID=f43b2a069050d510416f015d10cd2ae0;_ntes_nuid=0250cd17c80bf506719bd4e019c616b0;__remember_me=true;${token} ${role}")
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

    override fun getPlatform(): Platform {
        return Platform.NetEaseCloud
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override suspend fun collectOrCancelSong(collect: Boolean, data: Any): Response<Boolean> {
        val title = this::collectOrCancelSong.name
        val arr = data.toString().split(';')
        if(arr.count() != 3)
            return error("data is invalid")
        val map = mapOf(
            "op" to if(collect) "add" else "del",
            "pid" to arr[0],
            "trackIds" to "[\"${arr[1]}\"]",
            "imme" to "true",
            "csrf_token" to arr[2]
        )
        val _text = EncryptHelper.weApi(map.toJson())
        val params = "params=${_text.first}&encSecKey=${_text.second}"
        val result = service.collectOrCancelSong(params.toRam()).awaitResult()
        return if(result.e != null)
            error(title,result.e)
        else{
            var msg = "execute success!"
            var success = true
            if(result.data!!.code != 200){
                success = false
                msg = "execute failed!!"
            }
            if(result.data.message != null){
                msg = result.data.message
            }
            return Response(
                title = title,
                support = true,
                success = success,
                data = null,
                exception = null,
                message = msg
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getLeaderboard(): Response<List<Leaderboard>> {
        val title = this::getLeaderboard.name
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override suspend fun getLeaderboardSongList(
        id: String,
        page: Int,
        limit: Int
    ): Response<List<Media>> {
        return getPlayList(this::getLeaderboardSongList.name,id,page,limit,"")
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun getPlayList(title:String, id:String, page: Int, limit: Int, token:String):Response<List<Media>>{
        val detail = service.getPlayListDetail(id.toLong()).awaitResult()
        if(detail.e != null){
            return error(title,detail.e)
        }
        val ids = detail.data!!.playlist.trackIds
        val start = (page-1)*limit
        if(start >= ids.count())
            return Response(
                title = title,
                success = true,
                support = true,
                data = listOf(),
                message = "ok",
                exception = null
            )

        var end = start + limit
        if(end >= ids.count())
            end = ids.count()-1
        val list = ids.subList(start,end)
        val builder = StringBuilder()
        list.forEach {
            builder.append("{\"id\":${it.id}},")
        }
        builder.deleteAt(builder.length-1)
        builder.insert(0,'[')
        builder.append(']')
        val map = mapOf(
            "c" to builder.toString(),
            "csrf_token" to token
        )
        val _text = EncryptHelper.weApi(map.toJson())
        val params = "params=${_text.first}&encSecKey=${_text.second}"
        val result = service.getPlayList(params.toRam()).awaitResult()
        if(result.e != null)
            return error(title,result.e)
        else{
            val data = mutableListOf<Media>()
            result.data!!.songs.forEach {
                data.add(it.convert())
            }
            return Response(
                title = title,
                data = data,
                support = true,
                success = true,
                message = "ok",
                exception = null
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override suspend fun getArtistSongList(id: String, page: Int, limit: Int): Response<List<Media>> {
        val title = this::getArtistSongList.name
        val map = mapOf(
            "id" to id.toString(),
            "private_cloud" to "true",
            "work_type" to "1",
            "order" to "hot",
            "offset" to (page-1)*limit,
            "limit" to limit,
            "csrf_token" to ""
        )
        val _text = EncryptHelper.weApi(map.toJson())
        val params = "params=${_text.first}&encSecKey=${_text.second}"
        val result = service.getArtistSongList(params.toRam()).awaitResult()
        return if(result.e != null){
            return error(title,result.e)
        }
        else{
            val list = mutableListOf<Media>()
            result.data!!.songs.forEach {
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

    override suspend fun getArtistInfo(id: Long): Response<Artist> {
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override suspend fun search(key: String, page: Int, limit: Int): Response<List<Media>> {
        val title = this::search.name
        val api = "https://interface.music.163.com/eapi/cloudsearch/pc"
        val map = mapOf(
            "s" to key,
            "type" to 1,
            "limit" to limit,
            "offset" to (page-1)*limit,
            "total" to true
        )
        val params = EncryptHelper.eApi("/api/cloudsearch/pc",map.toJson())
        val result = service.search(api,params).awaitResult()
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override suspend fun getRecommendSongSheetList(data: Any): Response<List<SongSheet>> {
        val title = this::getRecommendSongSheetList.name
        val map = mapOf(
            "csrf_token" to data
        )
        val _text = EncryptHelper.weApi(map.toJson())
        val params = "params=${_text.first}&encSecKey=${_text.second}"
        val result = service.getRecommendSongSheet(params.toRam()).awaitResult()
        return if(result.e != null)
            error(title,result.e)
        else{
            val list = mutableListOf<SongSheet>()
            result.data!!.recommend.forEach {
                list.add(it.convert())
            }
            return Response(
                title = title,
                success = true,
                support = true,
                message = "ok",
                data = list,
                exception = null
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override suspend fun getRecommendSongSheetData(
        id: String,
        page: Int,
        limit: Int,
        data:Any,
    ): Response<List<Media>> {
        return getPlayList(this::getRecommendSongSheetData.name,id,page,limit,data.toString())
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override suspend fun getRecommendDaily(data: Any): Response<List<Media>> {
        val title = this::getRecommendDaily.name
        val map = mapOf(
            "csrf_token" to data.toString()
        )
        val _text = EncryptHelper.weApi(map.toJson())
        val params = "params=${_text.first}&encSecKey=${_text.second}"
        val result = service.getDailySongs(params.toRam()).awaitResult()
        return if(result.e != null)
            error(title,result.e)
        else{
            val list = mutableListOf<Media>()
            result.data!!.data.dailySongs.forEach {
                list.add(it.convert())
            }
            Response(
                title = title,
                data = list,
                support = true,
                success = true,
                message = "ok",
                exception = null
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override suspend fun getUserSheet(data:Any):Response<List<SongSheet>>{
        // uid:String, token:String
        val title = this::getUserSheet.name
        if(data.toString().isNullOrEmpty())
            return notSupport(title)
        val arr = data.toString().split(';')
        val uid = arr[0]
        val token = arr[1]
        if(uid.isNullOrEmpty() || token.isNullOrEmpty())
            return notSupport(title)
        val map = mapOf(
            "uid" to uid,
            "limit" to 99999,
            "offset" to 0,
            "includeVideo" to false,
            "csrf_token" to token
        )
        val _text = EncryptHelper.weApi(map.toJson())
        val params = "params=${_text.first}&encSecKey=${_text.second}"
        val result = service.getUserSheet(params.toRam()).awaitResult()
        return if(result.e != null)
            error(title,result.e)
        else{
            val list = mutableListOf<SongSheet>()
            result.data!!.playlist.forEach {
                list.add(
                    SongSheet(
                        platform = Platform.NetEaseCloud,
                        id = it.id.toString(),
                        name = it.name,
                        pic = it.coverImgUrl,
                        description = ""
                    )
                )
            }
            Response(
                title = title,
                data = list,
                success = true,
                support = true,
                message = "ok",
                exception = null
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override suspend fun getUserSheetData(page:Int, limit:Int, data: Any): Response<List<Media>> {
        val title = this::getUserSheetData.name
        if(data.toString().isNullOrEmpty())
            return notSupport(title)
        val arr = data.toString().split(';')
        if(arr.count() != 2)
            return notSupport(title)
        val id = arr[0]
        val token = arr[1]
        if(id.isNullOrEmpty() || token.isNullOrEmpty())
            return notSupport(title)
        return getPlayList(title,id,page,limit,token)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override suspend fun getPlayUrl(id: String, quality: Any): Response<String> {
        val title = this::getPlayUrl.name
        val api = "https://interface.music.163.com/eapi/song/enhance/player/url/v1"
        val url = "/api/song/enhance/player/url/v1"
        val map = mapOf(
            "ids" to "[${id}]",
            "encodeType" to "flac",
            "level" to quality
        )
        val params = EncryptHelper.eApi(url,map.toJson())
        val result = service.getPlayUrl(api,params).awaitResult()
        return if(result.e != null)
            error(title,result.e)
        else{
            return Response(
                title = title,
                support = true,
                success = true,
                data = result.data!!.data.first().url,
                exception = null,
                message = "ok"
            )
        }
    }

    @SuppressLint("NewApi")
    override suspend fun getMvUrl(data: String): Response<String> {
        val title = this::getMvUrl.name
        val map = mapOf(
            "id" to data,
            "r" to 1080
        )
        val _text = EncryptHelper.weApi(map.toJson())
        val params = "params=${_text.first}&encSecKey=${_text.second}"
        val result = service.getMvUrl(params.toRam()).awaitResult()
        return if(result.e != null)
            error(title,result.e)
        else{
            return Response(
                title = title,
                support = true,
                success = true,
                data = result.data!!.data.url,
                exception = null,
                message = "ok"
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getLyrics(id: String): Response<Lyrics> {
        val title = this::getLyrics.name
        val result = service.getLyrics(id).awaitResult()
        return if(result.e != null)
            error(title,result.e)
        else{
            val data = result.data!!.lrc.lyric
            val arr = data.split("\n")
            val list = mutableListOf<Lyric>()
            arr.forEach {
                if(!it.isNullOrEmpty()){
                    val arr2 = it.split('[')
                    var lyric = ""
                    for(i in arr2.size-1 downTo 0){
                        val arr2Item = arr2[i]
                        if(!arr2Item.contains(']')){
                            continue
                        }
                        val arr2Str = arr2Item.substring(0,arr2Item.indexOf(']'))
                        if(i == arr2.size - 1){
                            lyric = arr2Item.replace(arr2Str,"").replace("]","")
                        }
                        list.add(
                            Lyric(
                                text = lyric.trim(),
                                time = arr2Str.toNetEaseCloudTime()
                            )
                        )
                    }
                }
            }
            list.sortBy { item -> item.time }

            Response(
                title = title,
                success = true,
                support = true,
                data = Lyrics(
                    id = id,
                    platform = Platform.NetEaseCloud,
                    lyrics = list
                ),
                message = "ok",
                exception = null
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun getLoginUniKey():Response<String>{
        val title = this::getLoginUniKey.name
        val map = mapOf(
            "type" to 1,
            "csrf_token" to ""
        )
        val _text = EncryptHelper.weApi(map.toJson())
        val params = "params=${_text.first}&encSecKey=${_text.second}"
        val result = service.getLoginUniKey(params.toRam()).awaitResult()
        return if(result.e != null){
            error(title,result.e)
        }
        else{
            Response(
                title = title,
                data = result.data!!.unikey,
                support = true,
                success = true,
                exception = null,
                message = "ok"
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun loginCheck(key: String):Response<LoginResponse>{
        val title = this::loginCheck.name
        val map = mapOf(
            "key" to key,
            "type" to 1,
            "csrf_token" to ""
        )
        val _text = EncryptHelper.weApi(map.toJson())
        val params = "params=${_text.first}&encSecKey=${_text.second}"
        val result = service.loginCheck(params.toRam()).awaitResult()
        return if (result.e != null)
            error(title,result.e)
        else{
            var token = ""
            var musicA = ""
            result.header.let {
                it!!.values("set-cookie").forEach {
                    if(it.contains("__csrf")){
                        val arr = it.split(';')
                        arr.forEach {
                            if(it.contains("__csrf")){
                                token = it.replace("__csrf=","")
                                return@forEach
                            }
                        }
                    }
                    if(it.contains("MUSIC_U")){
                        val arr = it.split(';')
                        arr.forEach {
                            if(it.contains("MUSIC_U")){
                                musicA = it.replace("MUSIC_U=","")
                                return@forEach
                            }
                        }
                    }
                }
            }
            Response(
                title = title,
                support = true,
                success = true,
                data = LoginResponse(
                    success = result.data!!.code == 803,
                    data = NetEaseCloudMusicConfig(
                        csrf_token = token,
                        music_a = musicA,
                        quality = "",
                        uid = "",
                        name = ""
                    ),
                    message = result.data.message
                ),
                exception = null,
                message = "ok"
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun getUserInfo(token:String):Response<NetEaseCloudMusicConfig>{
        val title = this::getUserInfo.name
        val map = mapOf(
            "csrf_token" to token
        )
        val _text = EncryptHelper.weApi(map.toJson())
        val params = "params=${_text.first}&encSecKey=${_text.second}"
        val result = service.getUserInfo(params.toRam()).awaitResult()
        return if(result.e != null)
            error(title,result.e)
        else {
            val profile = result.data!!.profile
            Response(
                title = title,
                success = true,
                support = true,
                data = NetEaseCloudMusicConfig(
                    uid = profile.userId.toString(),
                    name = profile.nickname,
                    csrf_token = null,
                    music_a = null,
                    quality = ""
                ),
                exception = null,
                message = "ok"
            )
        }
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