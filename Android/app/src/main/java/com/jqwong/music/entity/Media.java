package com.jqwong.music.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author: Jq
 * @date: 4/30/2023
 */
@Entity
public class Media {
    @Id
    public Long Id;
    public String MusicRid;
    public Integer Rid;
    public String Artist;
    public String Pic;
    public String Album;
    public Integer AlbumId;
    public String Name;
    public String Pic120;
    public String SongTimeMinutes;
    public Long Time;
    public String Url;
    public Long ArtistId;
    @Generated(hash = 770543333)
    public Media(Long Id, String MusicRid, Integer Rid, String Artist, String Pic,
            String Album, Integer AlbumId, String Name, String Pic120,
            String SongTimeMinutes, Long Time, String Url, Long ArtistId) {
        this.Id = Id;
        this.MusicRid = MusicRid;
        this.Rid = Rid;
        this.Artist = Artist;
        this.Pic = Pic;
        this.Album = Album;
        this.AlbumId = AlbumId;
        this.Name = Name;
        this.Pic120 = Pic120;
        this.SongTimeMinutes = SongTimeMinutes;
        this.Time = Time;
        this.Url = Url;
        this.ArtistId = ArtistId;
    }
    @Generated(hash = 551662551)
    public Media() {
    }
    public Long getId() {
        return this.Id;
    }
    public void setId(Long Id) {
        this.Id = Id;
    }
    public String getMusicRid() {
        return this.MusicRid;
    }
    public void setMusicRid(String MusicRid) {
        this.MusicRid = MusicRid;
    }
    public Integer getRid() {
        return this.Rid;
    }
    public void setRid(Integer Rid) {
        this.Rid = Rid;
    }
    public String getArtist() {
        return this.Artist;
    }
    public void setArtist(String Artist) {
        this.Artist = Artist;
    }
    public String getPic() {
        return this.Pic;
    }
    public void setPic(String Pic) {
        this.Pic = Pic;
    }
    public String getAlbum() {
        return this.Album;
    }
    public void setAlbum(String Album) {
        this.Album = Album;
    }
    public Integer getAlbumId() {
        return this.AlbumId;
    }
    public void setAlbumId(Integer AlbumId) {
        this.AlbumId = AlbumId;
    }
    public String getName() {
        return this.Name;
    }
    public void setName(String Name) {
        this.Name = Name;
    }
    public String getPic120() {
        return this.Pic120;
    }
    public void setPic120(String Pic120) {
        this.Pic120 = Pic120;
    }
    public String getSongTimeMinutes() {
        return this.SongTimeMinutes;
    }
    public void setSongTimeMinutes(String SongTimeMinutes) {
        this.SongTimeMinutes = SongTimeMinutes;
    }
    public Long getTime() {
        return this.Time;
    }
    public void setTime(Long Time) {
        this.Time = Time;
    }
    public String getUrl() {
        return this.Url;
    }
    public void setUrl(String Url) {
        this.Url = Url;
    }
    public Long getArtistId() {
        return this.ArtistId;
    }
    public void setArtistId(Long ArtistId) {
        this.ArtistId = ArtistId;
    }
}
