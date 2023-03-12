package com.jqwong.music.api

import com.jqwong.music.repository.entity.Artist
import com.jqwong.music.repository.entity.Favorite
import com.jqwong.music.repository.entity.Media
import com.jqwong.music.repository.entity.Sheet
import com.jqwong.music.repository.entity.SheetInfo
import com.jqwong.music.repository.entity.User
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface RemoteApi {


    @GET("T_User")
    fun QueryUserOfName(@Query("Name")name:String):Observable<List<User>>


    @POST("T_User")
    fun Register(@Body user: User):Observable<Response<Void>>


    @POST("T_Music_Sheet")
    fun AddSheet(@Body sheet:Sheet):Observable<Response<Void>>


    @PATCH("T_Music_Sheet")
    fun UpdateSheet(@Body sheet:Sheet):Observable<Response<Void>>


    @GET("T_Music_Sheet")
    fun QuerySheetOfUserId(@Query("UserId")userId:String):Observable<List<Sheet>>


    @GET("T_Music_Sheet")
    fun QuerySheetOfToken(@Query("Token") token:String):Observable<List<Sheet>>


    @GET("T_Music_SheetInfo")
    fun QuerySheetInfo(@Query("UserId") userId: String):Observable<List<SheetInfo>>


    @GET("T_Music_SheetInfo")
    fun QuerySheetInfo(@Query("UserId") userId: String,@Query("SheetToken") sheetToken:String,@Query("Rid") rid:String):Observable<List<SheetInfo>>


    @PATCH("T_Music_SheetInfo")
    fun UpdateSheetInfo(@Body sheetInfo: SheetInfo):Observable<Response<Void>>


    @POST("T_Music_SheetInfo")
    fun AddSheetInfo(@Body sheetInfo: SheetInfo):Observable<Response<Void>>


    @GET("T_Music_Artist")
    fun QueryAristOfUserId(@Query("UserId") userId:String):Observable<List<Artist>>


    @POST("T_Music_Artist")
    fun AddArtist(@Body artist: Artist):Observable<Response<Void>>


    @DELETE("T_Music_Artist")
    fun DeleteArtist(@Query("UserId") userId: String,@Query("ArtistId") artistId:String):Observable<Response<Void>>


    @GET("T_Music_Media")
    fun QueryMediaOfRid(@Query("Rid") rid:String):Observable<List<Media>>


    @POST("T_Music_Media")
    fun AddMedia(@Body media:Media):Observable<Response<Void>>


    @POST("T_Music_Favorite")
    fun AddFavorite(@Body favorite: Favorite):Observable<Response<Void>>


    @DELETE("T_Music_Favorite")
    fun DeleteFavorite(@Query("UserId") userId: String,@Query("Rid") rid:String):Observable<Response<Void>>


    @GET("T_Music_Favorite")
    fun QueryFavoriteOfUserId(@Query("UserId") userId:String):Observable<List<Favorite>>


    @GET("T_Music_Media")
    fun QueryMedia():Observable<List<Media>>

}