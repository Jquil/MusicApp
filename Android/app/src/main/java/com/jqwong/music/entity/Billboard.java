package com.jqwong.music.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author: Jq
 * @date: 4/30/2023
 */
@Entity
public class Billboard {
    @Id
    public Long Id;

    public String Name;
    public String SourceId;
    public String BId;
    public String Parent;
    public String Time;
    @Generated(hash = 499707286)
    public Billboard(Long Id, String Name, String SourceId, String BId,
            String Parent, String Time) {
        this.Id = Id;
        this.Name = Name;
        this.SourceId = SourceId;
        this.BId = BId;
        this.Parent = Parent;
        this.Time = Time;
    }
    @Generated(hash = 468014413)
    public Billboard() {
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
    public String getSourceId() {
        return this.SourceId;
    }
    public void setSourceId(String SourceId) {
        this.SourceId = SourceId;
    }
    public String getBId() {
        return this.BId;
    }
    public void setBId(String BId) {
        this.BId = BId;
    }
    public String getParent() {
        return this.Parent;
    }
    public void setParent(String Parent) {
        this.Parent = Parent;
    }
    public String getTime() {
        return this.Time;
    }
    public void setTime(String Time) {
        this.Time = Time;
    }
}
