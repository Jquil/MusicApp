package com.jqwong.music.api

import com.jqwong.music.entity.*
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

/**
 * @author: Jq
 * @date: 4/29/2023
 */
interface MemfireDB {

    @GET("T_User")
    fun queryUserByName(@Query("Name")name:String): Observable<List<User>>

    @POST("T_User")
    fun insertUser(@Body user: User):Observable<Response<Void>>

    @POST("T_Music_Sheet")
    fun insertSheet(@Body sheet: Sheet):Observable<Response<Void>>

    @GET("T_Music_Sheet?order=Id.asc")
    fun querySheetByUserUUID(@Query("UserUUID")userId:String):Observable<List<Sheet>>

    @PATCH("T_Music_Sheet")
    fun updateSheet(@Body sheet:Sheet):Observable<Response<Void>>

    @DELETE("T_Music_Sheet")
    fun deleteSheet(@Query("Token") token: String,@Query("UserUUID") userUUID: String):Observable<Response<Void>>

    @POST("T_Music_SheetInfo")
    fun insertSheetInfo(@Body sheetInfo: SheetInfo):Observable<Response<Void>>

    @DELETE("T_Music_SheetInfo")
    fun deleteSheetInfo(@Query("Rid") rid: String,@Query("UserUUID") userUUID: String):Observable<Response<Void>>

    @DELETE("T_Music_SheetInfo")
    fun deleteSheetInfo(@Query("SheetToken") token: String):Observable<Response<Void>>

    @GET("T_Music_Media")
    fun queryMediasByRid(@Query("Rid") rid:String):Observable<List<Media>>

    @POST("T_Music_Media")
    fun insertMedia(@Body media:Media):Observable<Response<Void>>

    @POST("T_Music_Favorite_Media")
    fun insertFavoriteMedia(@Body media: FavoriteMedia):Observable<Response<Void>>

    @DELETE("T_Music_Favorite_Media")
    fun deleteFavoriteMedia(@Query("Rid") rid:String,@Query("UserUUID") userUUID:String):Observable<Response<Void>>

    @GET("T_Music_Favorite_Media")
    fun queryFavoriteMedia(@Query("UserUUID") userUUID:String):Observable<List<FavoriteMedia>>

    @GET("T_Music_Favorite_Media")
    fun queryFavoriteMedia(@Query("UserUUID") userUUID:String,@Query("Rid") rid:String):Observable<List<FavoriteMedia>>

    @POST("T_Music_Artist")
    fun insertArtist(@Body artist: Artist):Observable<Response<Void>>

    @GET("T_Music_Artist")
    fun queryArtistById(@Query("ArtistId") id:String):Observable<List<Artist>>

    @POST("T_Music_Favorite_Artist")
    fun insertFavoriteArtist(@Body artist: FavoriteArtist2):Observable<Response<Void>>

    @GET("T_Music_Favorite_Artist")
    fun queryFavoriteArtists(@Query("ArtistId") artistId:String,@Query("UserUUID") userUUID: String):Observable<List<FavoriteArtist2>>

    @GET("T_Music_Favorite_Artist")
    fun queryFavoriteArtists(@Query("UserUUID") userUUID: String):Observable<List<FavoriteArtist2>>

    @DELETE("T_Music_Favorite_Artist")
    fun deleteFavoriteArtist(@Query("ArtistId") artistId:String,@Query("UserUUID") userUUID: String):Observable<Response<Void>>

    @GET("T_Music_SheetInfo?order=Id.asc")
    fun querySheetInfoByUserUUID(@Query("UserUUID")userId:String):Observable<List<SheetInfo>>
}