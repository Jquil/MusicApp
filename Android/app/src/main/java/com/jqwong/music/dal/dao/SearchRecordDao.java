package com.jqwong.music.dal.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.jqwong.music.entity.SearchRecord;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "SEARCH_RECORD".
*/
public class SearchRecordDao extends AbstractDao<SearchRecord, Long> {

    public static final String TABLENAME = "SEARCH_RECORD";

    /**
     * Properties of entity SearchRecord.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "Id", true, "_id");
        public final static Property Key = new Property(1, String.class, "Key", false, "KEY");
        public final static Property Time = new Property(2, String.class, "Time", false, "TIME");
    }


    public SearchRecordDao(DaoConfig config) {
        super(config);
    }
    
    public SearchRecordDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SEARCH_RECORD\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: Id
                "\"KEY\" TEXT," + // 1: Key
                "\"TIME\" TEXT);"); // 2: Time
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SEARCH_RECORD\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, SearchRecord entity) {
        stmt.clearBindings();
 
        Long Id = entity.getId();
        if (Id != null) {
            stmt.bindLong(1, Id);
        }
 
        String Key = entity.getKey();
        if (Key != null) {
            stmt.bindString(2, Key);
        }
 
        String Time = entity.getTime();
        if (Time != null) {
            stmt.bindString(3, Time);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, SearchRecord entity) {
        stmt.clearBindings();
 
        Long Id = entity.getId();
        if (Id != null) {
            stmt.bindLong(1, Id);
        }
 
        String Key = entity.getKey();
        if (Key != null) {
            stmt.bindString(2, Key);
        }
 
        String Time = entity.getTime();
        if (Time != null) {
            stmt.bindString(3, Time);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public SearchRecord readEntity(Cursor cursor, int offset) {
        SearchRecord entity = new SearchRecord( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // Id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // Key
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // Time
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, SearchRecord entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setKey(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTime(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(SearchRecord entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(SearchRecord entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(SearchRecord entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}