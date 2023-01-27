package com.jqwong.music.repository.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Artist {
    @org.greenrobot.greendao.annotation.Id(autoincrement = true)
    public Long Id;
    public Long UserId;
    public Long ArtistId;
    public String Name;
    public String Pic;
    @Generated(hash = 1090937447)
    public Artist(Long Id, Long UserId, Long ArtistId, String Name, String Pic) {
        this.Id = Id;
        this.UserId = UserId;
        this.ArtistId = ArtistId;
        this.Name = Name;
        this.Pic = Pic;
    }
    @Generated(hash = 19829037)
    public Artist() {
    }
    public Long getId() {
        return this.Id;
    }
    public void setId(Long Id) {
        this.Id = Id;
    }
    public Long getUserId() {
        return this.UserId;
    }
    public void setUserId(Long UserId) {
        this.UserId = UserId;
    }
    public Long getArtistId() {
        return this.ArtistId;
    }
    public void setArtistId(Long ArtistId) {
        this.ArtistId = ArtistId;
    }
    public String getName() {
        return this.Name;
    }
    public void setName(String Name) {
        this.Name = Name;
    }
    public String getPic() {
        return this.Pic;
    }
    public void setPic(String Pic) {
        this.Pic = Pic;
    }
}
