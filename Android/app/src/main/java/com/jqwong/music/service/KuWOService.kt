package com.jqwong.music.service

import android.content.res.Resources.NotFoundException
import android.os.Build
import androidx.annotation.RequiresApi
import com.jqwong.music.api.KuWoMusicApi
import com.jqwong.music.app.App
import com.jqwong.music.helper.TimeHelper
import com.jqwong.music.helper.awaitResult
import com.jqwong.music.helper.toKwTime
import com.jqwong.music.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.Base64
import java.util.concurrent.TimeUnit

/**
 * @author: Jq
 * @date: 7/28/2023
 */
class KuWOService:IService {
    companion object{
        private val TAG = "KuWOService"
        private val FILTER_LIST = mutableListOf(
            "mobi.kuwo.cn"
        )
        private val config = OkHttpClient.Builder()
            .connectTimeout(App.config.okhttp_request_timeout,TimeUnit.MILLISECONDS)
            .addInterceptor {
                val req = it.request()
                val builder = req.newBuilder()
                if(!FILTER_LIST.contains(req.url().host())){
                    App.config.kuWoMusicConfig.cookies.forEach { s, s2 ->
                        builder.header(s,s2)
                    }
                }
                it.proceed(builder.build())
            }
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

        private val service = Retrofit.Builder()
            .baseUrl("https://www.kuwo.cn/api/")
            .client(config.build())
            .addConverterFactory(MoshiConverterFactory
                .create(Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()))
            .build()
            .create(KuWoMusicApi::class.java)
    }
    override suspend fun getLeaderboard(): Response<List<Leaderboard>> {
        val title = this::getLeaderboard.name
        val result = service.getLeaderboard().awaitResult()
        if(result.e != null){
            return error(title,result.e)
        }
        else{
            val list = mutableListOf<Leaderboard>()
            result.data?.data?.forEach {
                val children = mutableListOf<Leaderboard>()
                it.list.forEach {
                    children.add(
                        Leaderboard(
                            platform = Platform.KuWo,
                            id = it.sourceid,
                            name = it.name,
                            children = null
                        )
                    )
                }
                list.add(
                    Leaderboard(
                        platform = Platform.KuWo,
                        id = null,
                        name = it.name,
                        children = children
                    )
                )
            }

            return Response(
                title = title,
                success = true,
                message = "ok",
                data = list,
                exception = null,
                support = true
            )
        }
    }
    override suspend fun getLeaderboardSongList(id: String, page: Int, limit: Int): Response<List<Media>> {
        val title = this::getLeaderboardSongList.name
        val result = service.getLeaderboardSongList(id.toString(),page,limit).awaitResult()
        if(result.e != null){
            return error(title,result.e)
        }
        else{
            val list = mutableListOf<Media>()
            result.data!!.data.musicList.forEach {
                list.add(Media(
                    audio = Audio.convert(it.convert()),
                    video = null
                ))
            }
            return Response(
                title = title,
                success = true,
                message = "ok",
                data = list,
                exception = null,
                support = true
            )
        }
    }
    override suspend fun getArtistSongList(id: Long, page: Int, limit: Int):Response<List<Media>> {
        val title = this::getArtistSongList.name
        val result = service.getArtistSongList(id.toString(),page,limit).awaitResult()
        if(result.e != null){
            return error(title,result.e)
        }
        else{
            val list = mutableListOf<Media>()
            result.data!!.data.list.forEach {
                list.add(Media(
                    audio = Audio.convert(it.convert()),
                    video = null
                ))
            }
            return Response(
                title = title,
                success = true,
                message = "ok",
                data = list,
                exception = null,
                support = true
            )
        }
    }
    override suspend fun getArtistInfo(id: Long): Response<Artist> {
        val title = this::getArtistInfo.name
        val result = service.getArtistInfo(id.toString()).awaitResult()
        return if(result.e != null){
            error(title,result.e)
        }
        else{
            val data = result.data?.data!!
            val artist = Artist(
                id = data.id.toString(),
                name = data.name,
                pic = data.pic,
                description = data.info,
                platform = Platform.KuWo
            )
            Response(
                title = title,
                success = true,
                message = "ok",
                data = artist,
                exception = null,
                support = true
            )
        }
    }
    override suspend fun search(key: String, page: Int, limit: Int): Response<List<Media>> {
        val title = this::search.name
        val result = service.search(key,page,limit).awaitResult()
        if(result.e != null){
            return error(title,result.e)
        }
        else{
            val list = mutableListOf<Media>()
            result.data!!.data.list.forEach {
                list.add(Media(
                    audio = Audio.convert(it.convert()),
                    video = null
                ))
            }
            return Response(
                title = title,
                success = true,
                message = "ok",
                data = list,
                exception = null,
                support = true
            )
        }
    }
    override suspend fun getRecommendSongSheetList(data: Any): Response<List<SongSheet>> {
        val title = this::getRecommendSongSheetList.name
        val result = service.getRecommendSongSheet().awaitResult()
        return if(result.e != null){
            error(title,result.e)
        } else{
            val list = mutableListOf<SongSheet>()
            result.data?.data?.list?.forEach {
                list.add(SongSheet(
                    platform = Platform.KuWo,
                    id = it.id.toString(),
                    name = it.name,
                    pic = it.img,
                    description = it.info
                ))
            }
            Response(
                title = title,
                success = true,
                message = "ok",
                data = list,
                exception = null,
                support = true
            )
        }
    }
    override suspend fun getRecommendSongSheetData(id:String, page:Int, limit:Int,data:Any): Response<List<Media>> {
        val title = this::getRecommendSongSheetData.name
        val result = service.getRecommendSongSheetData(id,page,limit).awaitResult()
        return if(result.e != null){
            error(title,result.e)
        }
        else{
            val list = mutableListOf<Media>()
            result.data!!.data.musicList.forEach {
                list.add(Media(
                    audio = Audio.convert(it.convert()),
                    video = null
                ))
            }
            Response(
                title = title,
                success = true,
                message = "ok",
                data = list,
                exception = null,
                support = true
            )
        }
    }
    override suspend fun getRecommendDaily(data: Any): Response<List<Media>> {
        val title = this::getRecommendDaily.name
        return notSupport(title)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override suspend fun getPlayUrl(id: String, quality: Any):Response<String> {
        // $"corp=kuwo&p2p=1&type=convert_url2&format={format}&rid={musicid}"
        val title = this::getPlayUrl.name
        var text = "corp=kuwo&p2p=1&type=convert_url2&format=${quality}&rid=${id}"
        text = EncryptHelper.encrypt(text)
        val url = "http://mobi.kuwo.cn/mobi.s?f=kuwo&q=${text}"
        val result = service.getPlayUrl(url).awaitResult()
        return if(result.e != null){
            error(title,result.e)
        }
        else{
            val data = result.data!!.bytes().toString(Charsets.UTF_8)
            val line = data.split("\r\n")
            for(i in 0 until line.size){
                if(line[i].indexOf("url=") != -1){
                    return Response(
                        title = title,
                        data = line[i].substring(3+1),
                        success = true,
                        support = true,
                        exception = null,
                        message = "ok"
                    )
                }
            }
            return Response(
                title = title,
                data = "",
                success = true,
                support = true,
                exception = ExceptionLog(
                    title = title,
                    exception = NotFoundException("url"),
                    time = TimeHelper.getTime()
                ),
                message = "ok"
            )
        }
    }
    override suspend fun getMvUrl(id: String):Response<String> {
        TODO("Not yet implemented")
    }
    override suspend fun getLyrics(id: String) :Response<Lyrics>{
        val title = this::getLyrics.name
        val url = "http://m.kuwo.cn/newh5/singles/songinfoandlrc?musicId=${id}&httpsStatus=1&reqId=f9204c10-1df1-11ec-8b4f-9f163660962a"
        val result = service.getLyrics(url).awaitResult()
        return if(result.e != null){
            error(title,result.e)
        }
        else{
            val list = mutableListOf<Lyric>()
            result.data!!.data.lrclist.forEach {
                list.add(
                    Lyric(
                        time = it.time.toKwTime(),
                        text = it.lineLyric
                    )
                )
            }
            Response(
                title=title,
                success = true,
                support = true,
                exception = null,
                data = Lyrics(Platform.KuWo,id,list),
                message = "ok"
            )
        }
    }
    class EncryptHelper{
        companion object{
            private val SECRET_KEY = "ylzsxkwm".toByteArray(Charsets.UTF_8)
            private val DES_MODE_DECRYPT:Long = 1
            private val ARR_MASK = mutableListOf<Long>()
            private val ARR_PC_A = listOf<Long>(56, 48, 40, 32, 24, 16, 8, 0, 57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 62, 54, 46, 38, 30, 22, 14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 60, 52, 44, 36, 28, 20, 12, 4, 27, 19, 11, 3)
            private val ARR_PC_B = listOf<Long>(13, 16, 10, 23, 0, 4, -1, -1, 2, 27, 14, 5, 20, 9, -1, -1, 22, 18, 11, 3, 25, 7, -1, -1, 15, 6, 26, 19, 12, 1, -1, -1, 40, 51, 30, 36, 46, 54, -1, -1, 29, 39, 50, 44, 32, 47, -1, -1, 43, 48, 38, 55, 33, 52, -1, -1, 45, 41, 49, 35, 28, 31, -1, -1)
            private val ARR_LS = listOf<Long>(1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1 )
            private val ARR_LS_MASK = listOf<Long>(0, 0x100001, 0x300003)
            private val MATRIX_NS_BOX = listOf(
                listOf<Long>( 14, 4, 3, 15, 2, 13, 5, 3, 13, 14, 6, 9, 11, 2, 0, 5, 4, 1, 10, 12, 15, 6, 9, 10, 1, 8, 12, 7, 8, 11, 7, 0, 0, 15, 10, 5, 14, 4, 9, 10, 7, 8, 12, 3, 13, 1, 3, 6, 15, 12, 6, 11, 2, 9, 5, 0, 4, 2, 11, 14, 1, 7, 8, 13 ),
                listOf<Long>( 15, 0, 9, 5, 6, 10, 12, 9, 8, 7, 2, 12, 3, 13, 5, 2, 1, 14, 7, 8, 11, 4, 0, 3, 14, 11, 13, 6, 4, 1, 10, 15, 3, 13, 12, 11, 15, 3, 6, 0, 4, 10, 1, 7, 8, 4, 11, 14, 13, 8, 0, 6, 2, 15, 9, 5, 7, 1, 10, 12, 14, 2, 5, 9 ),
                listOf<Long>( 10, 13, 1, 11, 6, 8, 11, 5, 9, 4, 12, 2, 15, 3, 2, 14, 0, 6, 13, 1, 3, 15, 4, 10, 14, 9, 7, 12, 5, 0, 8, 7, 13, 1, 2, 4, 3, 6, 12, 11, 0, 13, 5, 14, 6, 8, 15, 2, 7, 10, 8, 15, 4, 9, 11, 5, 9, 0, 14, 3, 10, 7, 1, 12 ),
                listOf<Long>( 7, 10, 1, 15, 0, 12, 11, 5, 14, 9, 8, 3, 9, 7, 4, 8, 13, 6, 2, 1, 6, 11, 12, 2, 3, 0, 5, 14, 10, 13, 15, 4, 13, 3, 4, 9, 6, 10, 1, 12, 11, 0, 2, 5, 0, 13, 14, 2, 8, 15, 7, 4, 15, 1, 10, 7, 5, 6, 12, 11, 3, 8, 9, 14 ),
                listOf<Long>( 2, 4, 8, 15, 7, 10, 13, 6, 4, 1, 3, 12, 11, 7, 14, 0, 12, 2, 5, 9, 10, 13, 0, 3, 1, 11, 15, 5, 6, 8, 9, 14, 14, 11, 5, 6, 4, 1, 3, 10, 2, 12, 15, 0, 13, 2, 8, 5, 11, 8, 0, 15, 7, 14, 9, 4, 12, 7, 10, 9, 1, 13, 6, 3 ),
                listOf<Long>( 12, 9, 0, 7, 9, 2, 14, 1, 10, 15, 3, 4, 6, 12, 5, 11, 1, 14, 13, 0, 2, 8, 7, 13, 15, 5, 4, 10, 8, 3, 11, 6, 10, 4, 6, 11, 7, 9, 0, 6, 4, 2, 13, 1, 9, 15, 3, 8, 15, 3, 1, 14, 12, 5, 11, 0, 2, 12, 14, 7, 5, 10, 8, 13 ),
                listOf<Long>( 4, 1, 3, 10, 15, 12, 5, 0, 2, 11, 9, 6, 8, 7, 6, 9, 11, 4, 12, 15, 0, 3, 10, 5, 14, 13, 7, 8, 13, 14, 1, 2, 13, 6, 14, 9, 4, 1, 2, 14, 11, 13, 5, 0, 1, 10, 8, 3, 0, 11, 3, 5, 9, 4, 15, 2, 7, 8, 12, 15, 10, 7, 6, 12 ),
                listOf<Long>( 13, 7, 10, 0, 6, 9, 5, 15, 8, 4, 3, 10, 11, 14, 12, 5, 2, 11, 9, 6, 15, 12, 0, 3, 4, 1, 14, 13, 1, 2, 7, 8, 1, 2, 12, 15, 10, 4, 0, 3, 13, 14, 6, 9, 7, 8, 9, 6, 15, 1, 5, 12, 3, 10, 14, 5, 8, 7, 11, 0, 4, 13, 2, 11 )
            )
            private val ARR_IP_A = listOf<Long>(57, 49, 41, 33, 25, 17, 9, DES_MODE_DECRYPT, 59, 51, 43, 35, 27, 19, 11, 3, 61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7, 56, 48, 40, 32, 24, 16, 8, 0, 58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14, 6)
            private val ARR_IP_B = listOf<Long>(39, 7, 47, 15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51, 19, 59, 27, 34, 2, 42, 10, 50, 18, 58, 26, 33, DES_MODE_DECRYPT, 41, 9, 49, 17, 57, 25, 32, 0, 40, 8, 48, 16, 56, 24)
            private val ARR_E = listOf<Long>(31, 0, DES_MODE_DECRYPT, 2, 3, 4, -1, -1, 3, 4, 5, 6, 7, 8, -1, -1, 7, 8, 9, 10, 11, 12, -1, -1, 11, 12, 13, 14, 15, 16, -1, -1, 15, 16, 17, 18, 19, 20, -1, -1, 19, 20, 21, 22, 23, 24, -1, -1, 23, 24, 25, 26, 27, 28, -1, -1, 27, 28, 29, 30, 31, 30, -1, -1)
            private val ARR_P = listOf<Long>(15, 6, 19, 20, 28, 11, 27, 16, 0, 14, 22, 25, 4, 17, 30, 9, 1, 7, 23, 13, 31, 26, 2, 8, 18, 12, 29, 5, 21, 10, 3, 24)
            init {
                var tmp:Long = 1
                for (i in 0 until 64){
                    ARR_MASK.add(if(i == 0) "1".toLong() else (tmp*2))
                    tmp = ARR_MASK[i]
                }
            }
            private fun transformLongU(arr:List<Long>, num:Int, len:ULong):Long{
                var len2:Long = 0
                for(i in 0 until num){
                    val index = arr[i].toInt()
                    if(index > ARR_MASK.size || index < 0)
                        continue
                    val v = OperatorULong(len).and(ARR_MASK[index])
                    if(arr[i] < 0 || v == "0".toULong())
                        continue
                    len2 = len2.or(ARR_MASK[i])
                }
                return len2
            }
            private fun transformLongX(arr:List<Long>, num:Int, len:Long):Long{
                var len2:Long = 0
                for(i in 0 until num){
                    val index = arr[i].toInt()
                    if(index > ARR_MASK.size || index < 0)
                        continue
                    val v = len.and(ARR_MASK[index])
                    if(arr[i] < 0 || v == "0".toLong())
                        continue
                    len2 = len2.or(ARR_MASK[i])
                }
                return len2
            }
            private fun transformULongU(arr:List<Long>, num:Int, len:ULong):ULong{
                var len2:ULong = 0u
                for(i in 0 until num){
                    val index = arr[i].toInt()
                    if(index > ARR_MASK.size || index < 0)
                        continue
                    val v = OperatorULong(len).and(ARR_MASK[index])
                    if(arr[i] < 0 || v == "0".toULong())
                        continue
                    len2 = OperatorULong(len2).or(ARR_MASK[i])
                }
                return len2
            }
            private fun subKey(arr:Array<ULong>, num:ULong, len:ULong){
                var len2 = transformULongU(ARR_PC_A,56,len)
                //println(len2)
                for(i in 0 until 16){
                    // priority: (-) > (<< >>) > (^) > (|)
                    // len2 = ((len2 & ARR_LS_MASK[ARR_LS[i]]) << 28 - ARR_LS[i] | (len2 & ~ARR_LS_MASK[ARR_LS[i]]) >> ARR_LS[i])
                    val _v1 = OperatorULong(len2).and(ARR_LS_MASK[ARR_LS[i].toInt()])
                    val _v2 = 28 - ARR_LS[i]
                    val _v3 = OperatorULong(_v1).shl(_v2.toInt())
                    val _v4 = ARR_LS_MASK[ARR_LS[i].toInt()].inv()
                    val _v5 = OperatorULong(len2).and(_v4)
                    val _v6 = OperatorULong(_v5).shr(ARR_LS[i].toInt())
                    val _v7 = _v3.or(_v6)
                    len2 = _v7
                    arr[i] = transformULongU(ARR_PC_B,64,len2)
                }
                var j = 0
                while (num == "1".toULong() && j < 8){
                    val tmp = arr[j]
                    arr[j] = arr[15-j]
                    arr[15-j] = tmp
                    j++
                }
            }
            private fun dES64(arr:Array<ULong>, len:ULong):Long{
                val pr = Array<ULong>(8){i->0u}
                val pSource = arrayOf<Long>(0,0)
                var out = transformLongU(ARR_IP_A,64,len)
                pSource[0] = (0xFFFFFFFF).and(out)
                pSource[1] = ((-4294967296).and(out)).shr(32)
                for(i in 0 until 16){
                    var r = pSource[1]
                    r = transformLongX(ARR_E,64,r)
                    var nr = OperatorULong(arr[i]).xor(r)
                    for(j in 0 until 8){
                        // 255 & nr >> j*8    =>  priority: (*) > (>>) > (&)
                        val _v1 = j*8
                        val _v2 = nr.shr(_v1)
                        val _v3 = _v2.and(255u)
                        pr[j] = _v3
                    }
                    var sOut:Long = 0
                    for(j in 7 downTo 0){
                        sOut = sOut.shl(4)
                        sOut = sOut.or(MATRIX_NS_BOX[j][pr[j].toInt()])
                    }
                    nr = transformULongU(ARR_P,32,sOut.toULong())
                    val l = pSource[0]
                    pSource[0] = pSource[1]
                    pSource[1] = l.xor(nr.toLong())
                }
                pSource.reverse()
                // priority: (<<) > (&) > (|)
                val _v1 = pSource[1].shl(32)
                val _v2 = (-4294967296).and(_v1)
                val _v3 = (0xFFFFFFFF).and(pSource[0])
                out = _v2.or(_v3)
                out = transformLongX(ARR_IP_B,64,out)
                // println(out)
                return out
            }

            @RequiresApi(Build.VERSION_CODES.O)
            fun encrypt(text:String):String{
                val buffer = text.toByteArray()
                var len:ULong = 0u
                for(i in 0 until 8){
                    // priority: (*) > (<<) > (|)
                    val _v1 = i*8
                    val _v2 = SECRET_KEY[i].toULong().shl(_v1)
                    val _v3 = _v2.or(len)
                    len = _v3
                }
                //println(len)

                val arr_zero_16 = Array<ULong>(16){ i -> 0u }
                subKey(arr_zero_16,0u,len)
                //println(arr_zero_16)

                val j = Math.floor(buffer.size / 8.0).toInt()
                val arr_zero_j = Array<ULong>(j){ i->0u }
                for(m in 0 until j){
                    for(n in 0 until 8){
                        val _v1 = buffer[n+m*8].toLong()
                        val _v2 = _v1.shl(n*8)
                        arr_zero_j[m] = OperatorULong(arr_zero_j[m]).or(_v2)
                    }
                }

                val arr_zero_jx = Array<Long>(Math.floor((1+8*(j+1))/8.0).toInt()){i -> 0}
                for (i in 0 until j){
                    arr_zero_jx[i] = dES64(arr_zero_16,arr_zero_j[i])
                }

                val arrBya = buffer.slice(j*8..buffer.size-1)
                var len2 = 0
                for(i in 0 until buffer.size%8){
                    val _v1 = i*8
                    val _v2 = arrBya[i].toInt().shl(_v1)
                    val _v3 = len2.or(_v2)
                    len2 = _v3
                }
                arr_zero_jx[j] = dES64(arr_zero_16,len2.toULong())


                val arrByb = Array<Long>(8*arr_zero_jx.size){i -> 0}
                var i4 = 0
                arr_zero_jx.forEach {
                    for (i6 in 0 until 8){
                        // priority: (*) > (>>) > (&)
                        val _v1 = i6*8
                        val _v2 = it.shr(_v1)
                        arrByb[i4] = _v2.and(255)
                        i4++
                    }
                }

                val b8 = mutableListOf<Byte>()
                arrByb.forEach {
                    val v = it.toByte()
                    b8.add(v)
                }
                var _text = Base64.getEncoder().encodeToString(b8.toByteArray())
                _text = _text.replace("\n","")
                return _text
            }
        }
        class OperatorULong(val v:ULong){
            fun shl(bit:Int):ULong{
                return this.v.shl(bit)
            }
            fun shr(bit:Int):ULong{
                return this.v.shr(bit)
            }
            fun or(v:Long):ULong{
                if(v < 0){
                    return _or(this.v,v)
                }
                else{
                    val vU64 = v.toULong()
                    return this.v.or(vU64)
                }
            }
            fun or(v:ULong):ULong{
                return this.v.or(v)
            }
            fun and(v:Long):ULong{
                if(v < 0){
                    return _and(this.v,v)
                }
                else{
                    val vU64 = v.toULong()
                    return this.v.and(vU64)
                }
            }
            fun xor(v:Long):ULong{
                if(v < 0){
                    return _xor(this.v,v)
                }
                else{
                    val vU64 = v.toULong()
                    return this.v.xor(vU64)
                }
            }
            private fun _or(x:ULong,y:Long):ULong{
                var strX = x.toString(2)
                var strY = java.lang.Long.toBinaryString(y)
                if(strX.length > strY.length){
                    strY = strY.padStart(strX.length,'0')
                }
                else{
                    strX = strX.padStart(strY.length,'0')
                }
                val builder = StringBuilder()
                for(i in 0 until strX.length){
                    if(strX[i] == '1' || strY[i] == '1'){
                        builder.append('1')
                    }
                    else{
                        builder.append('0')
                    }
                }
                val str = builder.toString()
                return str.toULong(2)
            }
            private fun _and(x:ULong,y:Long):ULong{
                var strX = x.toString(2)
                var strY = java.lang.Long.toBinaryString(y)
                if(strX.length > strY.length){
                    strY = strY.padStart(strX.length,'0')
                }
                else{
                    strX = strX.padStart(strY.length,'0')
                }
                val builder = StringBuilder()
                for(i in 0 until strX.length){
                    if(strX[i] == '1' && strY[i] == '1'){
                        builder.append('1')
                    }
                    else{
                        builder.append('0')
                    }
                }
                val str = builder.toString()
                return str.toULong(2)
            }
            private fun _xor(x:ULong,y:Long):ULong{
                var strX = x.toString(2)
                var strY = java.lang.Long.toBinaryString(y)
                if(strX.length > strY.length){
                    strY = strY.padStart(strX.length,'0')
                }
                else{
                    strX = strX.padStart(strY.length,'0')
                }
                val builder = StringBuilder()
                for(i in 0 until strX.length){
                    if(strX[i] == '1' && strY[i] == '1'){
                        builder.append('0')
                    }
                    else{
                        if(strX[i] == '1' || strY[i] == '1'){
                            builder.append('1')
                        }
                        else{
                            builder.append('0')
                        }
                    }
                }
                val str = builder.toString()
                return str.toULong(2)
            }
        }
    }
}