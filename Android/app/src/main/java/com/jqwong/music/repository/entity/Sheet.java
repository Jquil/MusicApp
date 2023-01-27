package com.jqwong.music.repository.entity;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Sheet {

    @Id(autoincrement = true)
    public Long Id;

    public String Name;

    public Long UserId;

    public Boolean Delete;

    public String Token;

    @Generated(hash = 1393827697)
    public Sheet(Long Id, String Name, Long UserId, Boolean Delete, String Token) {
        this.Id = Id;
        this.Name = Name;
        this.UserId = UserId;
        this.Delete = Delete;
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

    public Long getUserId() {
        return this.UserId;
    }

    public void setUserId(Long UserId) {
        this.UserId = UserId;
    }

    public Boolean getDelete() {
        return this.Delete;
    }

    public void setDelete(Boolean Delete) {
        this.Delete = Delete;
    }

    public String getToken() {
        return this.Token;
    }

    public void setToken(String Token) {
        this.Token = Token;
    }
    
}
