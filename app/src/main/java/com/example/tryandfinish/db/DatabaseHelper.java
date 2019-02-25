package com.example.tryandfinish.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.tryandfinish.db.model.Filmovi;
import com.example.tryandfinish.db.model.Glumac;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "ormLite1";
    private static final int DATABASE_VERSION = 1;

    private Dao<Filmovi, Integer> mFilmoviDao = null;
    private Dao<Glumac, Integer> mGlumacDao = null;

    public DatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Glumac.class);
            TableUtils.createTable(connectionSource, Filmovi.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Filmovi.class, true);
            TableUtils.dropTable(connectionSource, Glumac.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public Dao<Filmovi, Integer> getmFilmoviDao() throws SQLException {
        if (mFilmoviDao == null) {
            mFilmoviDao = getDao(Filmovi.class);
        }
        return mFilmoviDao;
    }

    public Dao<Glumac, Integer> getmGlumacDao() throws SQLException {
        if (mGlumacDao == null) {
            mGlumacDao = getDao(Glumac.class);
        }
        return mGlumacDao;
    }

    @Override
    public void close() {
        mGlumacDao = null;
        mFilmoviDao = null;
        super.close();

    }
}
