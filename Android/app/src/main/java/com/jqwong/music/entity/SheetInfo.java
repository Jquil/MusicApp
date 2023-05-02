package com.jqwong.music.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author: Jq
 * @date: 4/30/2023
 */
@Entity
public class SheetInfo {
    @Id
    public Long Id;
    public String UserUUID;
    public String SheetToken;
    public String Rid;
    @Generated(hash = 1516913763)
    public SheetInfo(Long Id, String UserUUID, String SheetToken, String Rid) {
        this.Id = Id;
        this.UserUUID = UserUUID;
        this.SheetToken = SheetToken;
        this.Rid = Rid;
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
    public String getUserUUID() {
        return this.UserUUID;
    }
    public void setUserUUID(String UserUUID) {
        this.UserUUID = UserUUID;
    }
    public String getSheetToken() {
        return this.SheetToken;
    }
    public void setSheetToken(String SheetToken) {
        this.SheetToken = SheetToken;
    }
    public String getRid() {
        return this.Rid;
    }
    public void setRid(String Rid) {
        this.Rid = Rid;
    }
}
