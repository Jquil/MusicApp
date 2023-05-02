package com.jqwong.music.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author: Jq
 * @date: 4/29/2023
 */
@Entity
public class Sheet {

    @Id(autoincrement = true)
    public Long Id;
    public String Name;
    public String UserUUID;
    public String Token;
    @Generated(hash = 1829908275)
    public Sheet(Long Id, String Name, String UserUUID, String Token) {
        this.Id = Id;
        this.Name = Name;
        this.UserUUID = UserUUID;
        this.Token = Token;
    }
    @Generated(hash = 1314343206)
    public Sheet() {
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
    public String getUserUUID() {
        return this.UserUUID;
    }
    public void setUserUUID(String UserUUID) {
        this.UserUUID = UserUUID;
    }
    public String getToken() {
        return this.Token;
    }
    public void setToken(String Token) {
        this.Token = Token;
    }

}
