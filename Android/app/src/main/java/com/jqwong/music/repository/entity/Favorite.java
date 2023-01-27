package com.jqwong.music.repository.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Favorite {
    @org.greenrobot.greendao.annotation.Id(autoincrement = true)
    public Long Id;
    public Long UserId;
    public Long Rid;
    public Boolean Delete;
    @Generated(hash = 182043997)
    public Favorite(Long Id, Long UserId, Long Rid, Boolean Delete) {
        this.Id = Id;
        this.UserId = UserId;
        this.Rid = Rid;
        this.Delete = Delete;
    }
    @Generated(hash = 459811785)
    public Favorite() {
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
    public Long getRid() {
        return this.Rid;
    }
    public void setRid(Long Rid) {
        this.Rid = Rid;
    }
    public Boolean getDelete() {
        return this.Delete;
    }
    public void setDelete(Boolean Delete) {
        this.Delete = Delete;
    }
}
