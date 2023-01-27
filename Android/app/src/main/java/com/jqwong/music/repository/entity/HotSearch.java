package com.jqwong.music.repository.entity;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class HotSearch {

    @Id(autoincrement = true)
    public Long Id;


    public String Key;


    public String Date;


    @Generated(hash = 793931688)
    public HotSearch(Long Id, String Key, String Date) {
        this.Id = Id;
        this.Key = Key;
        this.Date = Date;
    }


    @Generated(hash = 160356079)
    public HotSearch() {
    }


    public Long getId() {
        return this.Id;
    }


    public void setId(Long Id) {
        this.Id = Id;
    }


    public String getKey() {
        return this.Key;
    }


    public void setKey(String Key) {
        this.Key = Key;
    }


    public String getDate() {
        return this.Date;
    }


    public void setDate(String Date) {
        this.Date = Date;
    }
}
