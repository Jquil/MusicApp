package com.jqwong.music.repository.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class SheetInfo {
    @org.greenrobot.greendao.annotation.Id(autoincrement = true)
    public Long Id;
    public Long UserId;
    public Long Rid;
    public String SheetToken;
    public Boolean Delete;
    @Generated(hash = 1874017003)
    public SheetInfo(Long Id, Long UserId, Long Rid, String SheetToken,
            Boolean Delete) {
        this.Id = Id;
        this.UserId = UserId;
        this.Rid = Rid;
        this.SheetToken = SheetToken;
        this.Delete = Delete;
    }
    @Generated(hash = 566694578)
    public SheetInfo() {
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
    public String getSheetToken() {
        return this.SheetToken;
    }
    public void setSheetToken(String SheetToken) {
        this.SheetToken = SheetToken;
    }
    public Boolean getDelete() {
        return this.Delete;
    }
    public void setDelete(Boolean Delete) {
        this.Delete = Delete;
    }


}
