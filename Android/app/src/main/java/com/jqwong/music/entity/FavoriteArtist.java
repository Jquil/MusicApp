package com.jqwong.music.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author: Jq
 * @date: 4/30/2023
 */
@Entity
public class FavoriteArtist {
    @Id
    public Long Id;
    public String Name;
    public String Pic70;
    public Long AId;
    @Generated(hash = 1382960978)
    public FavoriteArtist(Long Id, String Name, String Pic70, Long AId) {
        this.Id = Id;
        this.Name = Name;
        this.Pic70 = Pic70;
        this.AId = AId;
    }
    @Generated(hash = 235669337)
    public FavoriteArtist() {
    }
    public Long getId() {
        return this.Id;
    }
    public void setId(Long Id) {
        this.Id = Id;
    }
    public String getName() {
        return this.Name;
    }
    public void setName(String Name) {
        this.Name = Name;
    }
    public String getPic70() {
        return this.Pic70;
    }
    public void setPic70(String Pic70) {
        this.Pic70 = Pic70;
    }
    public Long getAId() {
        return this.AId;
    }
    public void setAId(Long AId) {
        this.AId = AId;
    }
}
