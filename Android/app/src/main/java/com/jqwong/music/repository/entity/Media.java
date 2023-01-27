package com.jqwong.music.repository.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Media {
    @org.greenrobot.greendao.annotation.Id(autoincrement = true)
    public Long Id;
    public String Url;
    public String Album;
    public String Artist;
    public String Name;
    public String Rid;
    public String Pic;
    public Long Time;
    public String StrTime;
    public Long ArtistId;
    @Generated(hash = 1269955460)
    public Media(Long Id, String Url, String Album, String Artist, String Name,
            String Rid, String Pic, Long Time, String StrTime, Long ArtistId) {
        this.Id = Id;
        this.Url = Url;
        this.Album = Album;
        this.Artist = Artist;
        this.Name = Name;
        this.Rid = Rid;
        this.Pic = Pic;
        this.Time = Time;
        this.StrTime = StrTime;
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
    public String getUrl() {
        return this.Url;
    }
    public void setUrl(String Url) {
        this.Url = Url;
    }
    public String getAlbum() {
        return this.Album;
    }
    public void setAlbum(String Album) {
        this.Album = Album;
    }
    public String getArtist() {
        return this.Artist;
    }
    public void setArtist(String Artist) {
        this.Artist = Artist;
    }
    public String getName() {
        return this.Name;
    }
    public void setName(String Name) {
        this.Name = Name;
    }
    public String getRid() {
        return this.Rid;
    }
    public void setRid(String Rid) {
        this.Rid = Rid;
    }
    public String getPic() {
        return this.Pic;
    }
    public void setPic(String Pic) {
        this.Pic = Pic;
    }
    public Long getTime() {
        return this.Time;
    }
    public void setTime(Long Time) {
        this.Time = Time;
    }
    public String getStrTime() {
        return this.StrTime;
    }
    public void setStrTime(String StrTime) {
        this.StrTime = StrTime;
    }
    public Long getArtistId() {
        return this.ArtistId;
    }
    public void setArtistId(Long ArtistId) {
        this.ArtistId = ArtistId;
    }
}
