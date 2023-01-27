package com.jqwong.music.repository.entity;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class History {

    @Id(autoincrement = true)
    private Long Id;


    private String Key;


    @Generated(hash = 1367783544)
    public History(Long Id, String Key) {
        this.Id = Id;
        this.Key = Key;
    }


    @Generated(hash = 869423138)
    public History() {
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
}
