package com.jqwong.music.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author: Jq
 * @date: 4/30/2023
 */
@Entity
public class FavoriteMedia {
    @Id
    public Long Id;
    public Integer Rid;
    public String UserUUID;
    @Generated(hash = 1349135919)
    public FavoriteMedia(Long Id, Integer Rid, String UserUUID) {
        this.Id = Id;
        this.Rid = Rid;
        this.UserUUID = UserUUID;
    }
    @Generated(hash = 1742746794)
    public FavoriteMedia() {
    }
    public Long getId() {
        return this.Id;
    }
    public void setId(Long Id) {
        this.Id = Id;
    }
    public Integer getRid() {
        return this.Rid;
    }
    public void setRid(Integer Rid) {
        this.Rid = Rid;
    }
    public String getUserUUID() {
        return this.UserUUID;
    }
    public void setUserUUID(String UserUUID) {
        this.UserUUID = UserUUID;
    }
}
