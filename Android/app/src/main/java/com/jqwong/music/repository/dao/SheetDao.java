package com.jqwong.music.repository.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.jqwong.music.repository.entity.Sheet;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "SHEET".
*/
public class SheetDao extends AbstractDao<Sheet, Long> {

    public static final String TABLENAME = "SHEET";

    /**
     * Properties of entity Sheet.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "Id", true, "_id");
        public final static Property Name = new Property(1, String.class, "Name", false, "NAME");
        public final static Property UserId = new Property(2, Long.class, "UserId", false, "USER_ID");
        public final static Property Delete = new Property(3, Boolean.class, "Delete", false, "DELETE");
        public final static Property Token = new Property(4, String.class, "Token", false, "TOKEN");
    }


    public SheetDao(DaoConfig config) {
        super(config);
    }
    
    public SheetDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"SHEET\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: Id
                "\"NAME\" TEXT," + // 1: Name
                "\"USER_ID\" INTEGER," + // 2: UserId
                "\"DELETE\" INTEGER," + // 3: Delete
                "\"TOKEN\" TEXT);"); // 4: Token
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"SHEET\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Sheet entity) {
        stmt.clearBindings();
 
        Long Id = entity.getId();
        if (Id != null) {
            stmt.bindLong(1, Id);
        }
 
        String Name = entity.getName();
        if (Name != null) {
            stmt.bindString(2, Name);
        }
 
        Long UserId = entity.getUserId();
        if (UserId != null) {
            stmt.bindLong(3, UserId);
        }
 
        Boolean Delete = entity.getDelete();
        if (Delete != null) {
            stmt.bindLong(4, Delete ? 1L: 0L);
        }
 
        String Token = entity.getToken();
        if (Token != null) {
            stmt.bindString(5, Token);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Sheet entity) {
        stmt.clearBindings();
 
        Long Id = entity.getId();
        if (Id != null) {
            stmt.bindLong(1, Id);
        }
 
        String Name = entity.getName();
        if (Name != null) {
            stmt.bindString(2, Name);
        }
 
        Long UserId = entity.getUserId();
        if (UserId != null) {
            stmt.bindLong(3, UserId);
        }
 
        Boolean Delete = entity.getDelete();
        if (Delete != null) {
            stmt.bindLong(4, Delete ? 1L: 0L);
        }
 
        String Token = entity.getToken();
        if (Token != null) {
            stmt.bindString(5, Token);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Sheet readEntity(Cursor cursor, int offset) {
        Sheet entity = new Sheet( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // Id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // Name
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // UserId
            cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0, // Delete
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) // Token
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Sheet entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setUserId(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setDelete(cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0);
        entity.setToken(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Sheet entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Sheet entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Sheet entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
