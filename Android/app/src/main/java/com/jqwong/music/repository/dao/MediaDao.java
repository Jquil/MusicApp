package com.jqwong.music.repository.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.jqwong.music.repository.entity.Media;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MEDIA".
*/
public class MediaDao extends AbstractDao<Media, Long> {

    public static final String TABLENAME = "MEDIA";

    /**
     * Properties of entity Media.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "Id", true, "_id");
        public final static Property Url = new Property(1, String.class, "Url", false, "URL");
        public final static Property Album = new Property(2, String.class, "Album", false, "ALBUM");
        public final static Property Artist = new Property(3, String.class, "Artist", false, "ARTIST");
        public final static Property Name = new Property(4, String.class, "Name", false, "NAME");
        public final static Property Rid = new Property(5, String.class, "Rid", false, "RID");
        public final static Property Pic = new Property(6, String.class, "Pic", false, "PIC");
        public final static Property Time = new Property(7, Long.class, "Time", false, "TIME");
        public final static Property StrTime = new Property(8, String.class, "StrTime", false, "STR_TIME");
        public final static Property ArtistId = new Property(9, Long.class, "ArtistId", false, "ARTIST_ID");
    }


    public MediaDao(DaoConfig config) {
        super(config);
    }
    
    public MediaDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MEDIA\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: Id
                "\"URL\" TEXT," + // 1: Url
                "\"ALBUM\" TEXT," + // 2: Album
                "\"ARTIST\" TEXT," + // 3: Artist
                "\"NAME\" TEXT," + // 4: Name
                "\"RID\" TEXT," + // 5: Rid
                "\"PIC\" TEXT," + // 6: Pic
                "\"TIME\" INTEGER," + // 7: Time
                "\"STR_TIME\" TEXT," + // 8: StrTime
                "\"ARTIST_ID\" INTEGER);"); // 9: ArtistId
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MEDIA\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Media entity) {
        stmt.clearBindings();
 
        Long Id = entity.getId();
        if (Id != null) {
            stmt.bindLong(1, Id);
        }
 
        String Url = entity.getUrl();
        if (Url != null) {
            stmt.bindString(2, Url);
        }
 
        String Album = entity.getAlbum();
        if (Album != null) {
            stmt.bindString(3, Album);
        }
 
        String Artist = entity.getArtist();
        if (Artist != null) {
            stmt.bindString(4, Artist);
        }
 
        String Name = entity.getName();
        if (Name != null) {
            stmt.bindString(5, Name);
        }
 
        String Rid = entity.getRid();
        if (Rid != null) {
            stmt.bindString(6, Rid);
        }
 
        String Pic = entity.getPic();
        if (Pic != null) {
            stmt.bindString(7, Pic);
        }
 
        Long Time = entity.getTime();
        if (Time != null) {
            stmt.bindLong(8, Time);
        }
 
        String StrTime = entity.getStrTime();
        if (StrTime != null) {
            stmt.bindString(9, StrTime);
        }
 
        Long ArtistId = entity.getArtistId();
        if (ArtistId != null) {
            stmt.bindLong(10, ArtistId);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Media entity) {
        stmt.clearBindings();
 
        Long Id = entity.getId();
        if (Id != null) {
            stmt.bindLong(1, Id);
        }
 
        String Url = entity.getUrl();
        if (Url != null) {
            stmt.bindString(2, Url);
        }
 
        String Album = entity.getAlbum();
        if (Album != null) {
            stmt.bindString(3, Album);
        }
 
        String Artist = entity.getArtist();
        if (Artist != null) {
            stmt.bindString(4, Artist);
        }
 
        String Name = entity.getName();
        if (Name != null) {
            stmt.bindString(5, Name);
        }
 
        String Rid = entity.getRid();
        if (Rid != null) {
            stmt.bindString(6, Rid);
        }
 
        String Pic = entity.getPic();
        if (Pic != null) {
            stmt.bindString(7, Pic);
        }
 
        Long Time = entity.getTime();
        if (Time != null) {
            stmt.bindLong(8, Time);
        }
 
        String StrTime = entity.getStrTime();
        if (StrTime != null) {
            stmt.bindString(9, StrTime);
        }
 
        Long ArtistId = entity.getArtistId();
        if (ArtistId != null) {
            stmt.bindLong(10, ArtistId);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Media readEntity(Cursor cursor, int offset) {
        Media entity = new Media( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // Id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // Url
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // Album
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // Artist
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // Name
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // Rid
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // Pic
            cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7), // Time
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // StrTime
            cursor.isNull(offset + 9) ? null : cursor.getLong(offset + 9) // ArtistId
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Media entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUrl(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setAlbum(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setArtist(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setRid(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setPic(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setTime(cursor.isNull(offset + 7) ? null : cursor.getLong(offset + 7));
        entity.setStrTime(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setArtistId(cursor.isNull(offset + 9) ? null : cursor.getLong(offset + 9));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Media entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Media entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Media entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
