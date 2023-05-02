package com.jqwong.music.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author: Jq
 * @date: 4/30/2023
 */
@Entity
public class HotSearchRecord {
    @Id
    public Long Id;
    public String Key;
    public String Time;
    @Generated(hash = 2120270786)
    public HotSearchRecord(Long Id, String Key, String Time) {
        this.Id = Id;
        this.Key = Key;
        this.Time = Time;
    }
    @Generated(hash = 814961664)
    public HotSearchRecord() {
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
    public String getTime() {
        return this.Time;
    }
    public void setTime(String Time) {
        this.Time = Time;
    }
}
