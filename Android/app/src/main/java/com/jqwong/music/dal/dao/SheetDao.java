package com.jqwong.music.dal.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.jqwong.music.entity.Sheet;

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
        public final static Property UserUUID = new Property(2, String.class, "UserUUID", false, "USER_UUID");
        public final static Property Token = new Property(3, String.class, "Token", false, "TOKEN");
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
                "\"USER_UUID\" TEXT," + // 2: UserUUID
                "\"TOKEN\" TEXT);"); // 3: Token
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
 
        String UserUUID = entity.getUserUUID();
        if (UserUUID != null) {
            stmt.bindString(3, UserUUID);
        }
 
        String Token = entity.getToken();
        if (Token != null) {
            stmt.bindString(4, Token);
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
 
        String UserUUID = entity.getUserUUID();
        if (UserUUID != null) {
            stmt.bindString(3, UserUUID);
        }
 
        String Token = entity.getToken();
        if (Token != null) {
            stmt.bindString(4, Token);
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
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // UserUUID
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3) // Token
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Sheet entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setUserUUID(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setToken(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
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