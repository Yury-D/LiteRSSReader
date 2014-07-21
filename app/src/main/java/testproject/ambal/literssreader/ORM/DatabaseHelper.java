package testproject.ambal.literssreader.ORM;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import testproject.ambal.literssreader.ORM.entities.Channel;
import testproject.ambal.literssreader.ORM.entities.Item;

/**
 * Created by Ambal on 17.07.14.
 */

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();

    // in data/data/APPNAME/DATABASE_NAME.db
    private static final String DATABASE_NAME = "FeedContent.db";
    private static final int DATABASE_VERSION = 1;

    //ссылки на DAO, соответсвующие сущностям, хранимым в БД
    private Dao<Item, Integer> simpleItemDao = null;
    private Dao<Channel, Integer> simpleChannelDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Channel.class);
            TableUtils.createTable(connectionSource, Item.class);
        } catch (SQLException e) {
            Log.e(TAG, "error creating DB " + DATABASE_NAME);
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Item.class, true);
            TableUtils.dropTable(connectionSource, Channel.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the Database Access Object (DAO) for our data class. It will create it or just give the cached
     * value.
     */
    public Dao<Item, Integer> getItemDao() throws SQLException {
        if (simpleItemDao == null) {
            simpleItemDao = getDao(Item.class);
        }
        return simpleItemDao;
    }

    public Dao<Channel, Integer> getChannelDao() throws SQLException {
        if (simpleChannelDao == null) {
            simpleChannelDao = getDao(Channel.class);
        }
        return simpleChannelDao;
    }

    @Override
    public void close() {
        super.close();
        simpleItemDao = null;
        simpleChannelDao = null;
    }
}

