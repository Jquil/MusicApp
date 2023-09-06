package com.jqwong.music.service

import com.jqwong.music.api.QQMusicApi
import com.jqwong.music.api.entity.qq.PlayUrl
import com.jqwong.music.app.App
import com.jqwong.music.helper.TimeHelper
import com.jqwong.music.helper.ZLibHelper
import com.jqwong.music.helper.awaitResult
import com.jqwong.music.helper.toRam
import com.jqwong.music.model.Artist
import com.jqwong.music.model.Leaderboard
import com.jqwong.music.model.Lyric
import com.jqwong.music.model.Lyrics
import com.jqwong.music.model.Media
import com.jqwong.music.model.Platform
import com.jqwong.music.model.Response
import com.jqwong.music.model.SongSheet
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.security.MessageDigest
import java.util.Base64
import java.util.concurrent.TimeUnit

class QQMusicService:IService {
    companion object{
        private val config = OkHttpClient.Builder()
            .connectTimeout(App.config.okhttp_request_timeout, TimeUnit.MILLISECONDS)
            .addInterceptor {
                val req = it.request()
                val builder = req.newBuilder()
                builder.header("referer","https://i.y.qq.com")
                builder.header("Host","u.y.qq.com")
                it.proceed(builder.build())
            }
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

        private val service = Retrofit.Builder()
            .baseUrl("https://u.y.qq.com/")
            .client(config.build())
            .addConverterFactory(
                MoshiConverterFactory
                .create(
                    Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()))
            .build()
            .create(QQMusicApi::class.java)

        private val client = OkHttpClient()
    }
    override fun getPlatform(): Platform {
        return Platform.QQ
    }

    override suspend fun collectOrCancelSong(collect: Boolean, data: Any): Response<Boolean> {
        return notSupport(this::collectOrCancelSong.name)
    }

    override suspend fun getLeaderboard(): Response<List<Leaderboard>> {
        return notSupport(this::getLeaderboard.name)
    }

    override suspend fun getLeaderboardSongList(
        id: String,
        page: Int,
        limit: Int
    ): Response<List<Media>> {
        return notSupport(this::getLeaderboardSongList.name)
    }

    
    override suspend fun getArtistSongList(
        mid: String,
        page: Int,
        limit: Int
    ): Response<List<Media>> {
        val title = this::getArtistSongList.name
        val text = "{\n" +
                "\t\"comm\": {\n" +
                "\t\t\"ct\": 24,\n" +
                "\t\t\"cv\": 0\n" +
                "\t},\n" +
                "\t\"singer\": {\n" +
                "\t\t\"method\": \"get_singer_detail_info\",\n" +
                "\t\t\"param\": {\n" +
                "\t\t\t\"sort\": 5,\n" +
                "\t\t\t\"singermid\": \"$mid\",\n" +
                "\t\t\t\"sin\": ${(page-1)*limit},\n" +
                "\t\t\t\"num\": $limit\n" +
                "\t\t},\n" +
                "\t\t\"module\": \"music.web_singer_info_svr\"\n" +
                "\t}\n" +
                "}"
        val result = service.getArtistSongList(text.toRam()).awaitResult()
        return if(result.e != null){
            return error(title,result.e)
        }
        else{
            val list = mutableListOf<Media>()
            result.data!!.singer.data.songlist.forEach {
                list.add(it.convert())
            }
            return Response(
                title = title,
                support = true,
                success = true,
                data = list,
                message = "",
                exception = null
            )
        }
    }

    override suspend fun getArtistInfo(id: Long): Response<Artist> {
        return notSupport(this::getArtistInfo.name)
    }

    
    override suspend fun search(key: String, page: Int, limit: Int): Response<List<Media>> {
        val title = this::search.name
        val text = "{\"music.search.SearchCgiService.DoSearchForQQMusicDesktop\": {\n" +
                "            \"method\": \"DoSearchForQQMusicDesktop\",\n" +
                "            \"module\": \"music.search.SearchCgiService\",\n" +
                "            \"param\": {\n" +
                "                \"search_type\": 0,\n" +
                "                \"query\": \"${key}\",\n" +
                "                \"page_num\": ${page},\n" +
                "                \"num_per_page\": ${limit}\n" +
                "            }\n" +
                "        }}"
        val result = service.search(text.toRam()).awaitResult()
        return if(result.e != null){
            error(title,result.e)
        }
        else{
            val list = mutableListOf<Media>()
            result.data!!.result.data.body.song.list.forEach {
                list.add(it.convert())
            }
            return Response(
                title = title,
                success = true,
                support = true,
                data = list,
                exception = null,
                message = ""
            )
        }
    }

    override suspend fun getRecommendSongSheetList(data: Any): Response<List<SongSheet>> {
        return notSupport(this::getRecommendSongSheetList.name)
    }

    override suspend fun getRecommendSongSheetData(
        id: String,
        page: Int,
        limit: Int,
        data: Any
    ): Response<List<Media>> {
        return notSupport(this::getRecommendSongSheetData.name)
    }

    override suspend fun getRecommendDaily(data: Any): Response<List<Media>> {
        return notSupport(this::getRecommendDaily.name)
    }

    
    override suspend fun getPlayUrl(mid: String, quality: Any): Response<String> {
        // sq hr hq mp3
        val title = this::getPlayUrl.name
        val platform = "qq"
        val device = "MI 14 Pro Max"
        val osVersion = "13"
        val time = TimeHelper.getTime()
        val sign1 = hashMD5("6d849adb2f3e00d413fe48efbb18d9bb${time}6562653262383463363633646364306534333668").lowercase()
        val s6 = "{\\\"method\\\":\\\"GetMusicUrl\\\",\\\"platform\\\":\\\"${platform}\\\",\\\"t1\\\":\\\"${mid}\\\",\\\"t2\\\":\\\"${quality}\\\"}"
        val s7 = "{\\\"uid\\\":\\\"\\\",\\\"token\\\":\\\"\\\",\\\"deviceid\\\":\\\"84ac82836212e869dbeea73f09ebe52b\\\",\\\"appVersion\\\":\\\"4.1.2\\\",\\\"vercode\\\":\\\"4120\\\",\\\"device\\\":\\\"${device}\\\",\\\"osVersion\\\":\\\"${osVersion}\\\"}"
        val sign2 = hashMD5("${s6.replace("\\","")}${s7.replace("\\","")}${sign1}${time}NDRjZGIzNzliNzEe").lowercase()
        var s8 = "{\n" +
                "        \"text_1\":\"${s6}\",\n" +
                "        \"text_2\":\"${s7}\",\n" +
                "        \"sign_1\":\"${sign1}\",\n" +
                "        \"time\": \"${time}\",\n" +
                "        \"sign_2\":\"${sign2}\"\n" +
                "}"
        val builder = StringBuilder()
        s8.encodeToByteArray().forEach {
            builder.append("${String.format("%02x", it)}")
        }
        s8 = builder.toString().uppercase()
        builder.clear()
        val reqParams = ZLibHelper.compress(s8.encodeToByteArray())
        val urlList = listOf(
            "http://gcsp.kzti.top:1030/client/cgi-bin/api.fcg",
            "http://119.91.134.171:1030/client/cgi-bin/api.fcg",
            "http://106.52.68.150:1030/client/cgi-bin/api.fcg"
        )
        val request = Request.Builder()
            .url(urlList.first())
            .post(RequestBody.create(MediaType.parse("text/plain"),reqParams))
            .build()
        try {
            val response = client.newCall(request).execute()
            if(response.isSuccessful){
                val bytes = response.body()!!.bytes()
                val content = ZLibHelper.uncompress(bytes)
                val data = PlayUrl.fromJson(content!!)
                if(data.data.isNullOrEmpty())
                    return error(title, Exception("not found play url"))
                return Response(
                    title = title,
                    support = true,
                    success = true,
                    data = data.data,
                    exception = null,
                    message = ""
                )
            }
            else{
                return error(title, Exception(response.message()))
            }
        }
        catch (e:Exception){
            return error(title, e)
        }
    }

    
    override suspend fun getMvUrl(vid: String): Response<String> {
        val title = this::getMvUrl.name
        val text = "{\n" +
                "\t\"getMvUrl\": {\n" +
                "\t\t\"module\": \"gosrf.Stream.MvUrlProxy\",\n" +
                "\t\t\"method\": \"GetMvUrls\",\n" +
                "\t\t\"param\": {\n" +
                "\t\t\t\"vids\": [\"$vid\"],\n" +
                "\t\t\t\"request_typet\": 10001\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}"
        val result = service.getMvUrl(text.toRam()).awaitResult()
        return if(result.e != null){
            error(title,result.e)
        }
        else{
            try {
                val data = (result.data!!.getMvUrl.data as Map<String, Any>)
                if(data.containsKey(vid)){
                    val item = data[vid] as Map<String,Any>
                    if(item.containsKey("mp4")){
                        val infoList = item["mp4"] as List<Map<String,Any>>
                        infoList.forEach {
                            if(it.containsKey("freeflow_url")){
                                val list = it["freeflow_url"] as List<String>
                                if(!list.isNullOrEmpty()){
                                    return Response(
                                        title = title,
                                        message = "",
                                        success = true,
                                        support = true,
                                        data = list.last(),
                                        exception = null
                                    )
                                }
                            }
                        }
                    }
                }
                throw Exception("parse error!")
            }
            catch (e:Exception){
                return error(title,e)
            }
        }

    }

    
    override suspend fun getLyrics(id: String): Response<Lyrics> {
        val title = this::getLyrics.name
        val text = "{\n" +
                "            \"comm\": {\n" +
                "                \"cv\": 4747474,\n" +
                "                \"ct\": 24,\n" +
                "                \"format\": \"json\",\n" +
                "                \"inCharset\": \"utf-8\",\n" +
                "                \"outCharset\": \"utf-8\",\n" +
                "                \"notice\": 0,\n" +
                "                \"platform\": \"yqq.json\",\n" +
                "                \"needNewCode\": 1\n" +
                "            },\n" +
                "            \"PlayLyricInfo\": {\n" +
                "                \"module\": \"music.musichallSong.PlayLyricInfo\",\n" +
                "                \"method\": \"GetPlayLyricInfo\",\n" +
                "                \"param\": {\n" +
                "                    \"songID\": ${id.toLong()}\n" +
                "                }\n" +
                "            }\n" +
                "        }"
        val result = service.getLyrics(text.toRam()).awaitResult()
        return if(result.e != null){
            error(title,result.e)
        }
        else{
            val list = mutableListOf<Lyric>()
            val bytes = Base64.getDecoder().decode(result.data!!.PlayLyricInfo.data.lyric)
            val content = String(bytes)
            val array = content.split("\n")
            for (i in 0 until array.count()){
                val item = array[i]
                val infoList = item.split("]")
                if(infoList.count() != 2)
                    continue
                val lyric = infoList[1]
                if(lyric.isEmpty())
                    continue
                var strTime = infoList[0]
                if(strTime.get(0) != '[')
                    continue
                var time:Long = 0
                strTime = strTime.substring(1)
                strTime.let {
                    var index = 0
                    val builder = StringBuilder()
                    while (index < strTime.count()){
                        var char = strTime.get(index)
                        if(char.equals(':')){
                            char = strTime.get(++index)
                            val x = builder.toString().toLong()
                            time += (x*60*1000)
                            builder.clear()
                        }
                        else if (char.equals('.')){
                            char = strTime.get(++index)
                            val x = builder.toString().toLong()
                            time += (x*1000)
                            builder.clear()
                        }
                        builder.append(char)
                        index++
                    }
                    val x = builder.toString().toLong()
                    time += x
                }
                list.add(Lyric(time,lyric))
            }
            return Response(
                title = title,
                success = true,
                support = true,
                exception = null,
                message = "",
                data = Lyrics(Platform.QQ,id,list)
            )
        }
    }

    override suspend fun getUserSheet(data: Any): Response<List<SongSheet>> {
        return notSupport(this::getUserSheet.name)
    }

    override suspend fun getUserSheetData(page: Int, limit: Int, data: Any): Response<List<Media>> {
        return notSupport(this::getUserSheetData.name)
    }

    private fun hashMD5(content:String):String{
        val hash = MessageDigest.getInstance("MD5").digest(content.toByteArray())
        val hex = StringBuilder(hash.size * 2)
        for (b in hash) {
            var str = Integer.toHexString(b.toInt())
            if (b < 0x10) {
                str = "0$str"
            }
            hex.append(str.substring(str.length -2))
        }
        return hex.toString()
    }
}