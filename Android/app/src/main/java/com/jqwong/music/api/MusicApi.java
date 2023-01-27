package com.jqwong.music.api;

import com.jqwong.music.model.ArtistInfo;
import com.jqwong.music.model.Bang;
import com.jqwong.music.model.MusicBaseData;
import com.jqwong.music.model.MusicList;
import com.jqwong.music.model.Song;
import com.jqwong.music.model.SongList;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface MusicApi {

    @GET("search/searchKey?key=&httpsStatus=1&reqId=db3f8670-e1f6-11eb-942d-33e288737b1d")
    Observable<MusicBaseData<List<String>>> GetHotSearch();


    @GET("search/searchMusicBykeyWord?httpsStatus=1&reqId=23016430-e1eb-11eb-a2ee-bf024dbfa4c7")
    Observable<MusicBaseData<SongList>> GetSearchResult(@Query("key") String key, @Query("pn") int page, @Query("rn") int size);


    @GET("artist/artistMusic?httpsStatus=1&reqId=87263830-f72d-11eb-979c-c11891b4f2ba")
    Observable<MusicBaseData<SongList>> GetArtistSong(@Query("artistid") String artistId,@Query("pn") int page, @Query("rn") int size);


    @GET("artist/artist?httpsStatus=1&reqId=b06e62f0-f582-11eb-bd8d-c19fac490f25")
    Observable<MusicBaseData<ArtistInfo>> GetArtistInfo(@Query("artistid") String artistId);


    @GET("bang/bang/bangMenu?httpsStatus=1&reqId=e617e730-e1f7-11eb-942d-33e288737b1d")
    Observable<MusicBaseData<List<Bang>>> GetBangMenu();


    @GET("bang/bang/musicList?httpsStatus=1&reqId=b092ca30-e152-11eb-90cc-79484e9fbe4d")
    Observable<MusicBaseData<MusicList>> GetBangSong(@Query("bangId") String sourceId, @Query("pn") int page, @Query("rn") int size);
}
