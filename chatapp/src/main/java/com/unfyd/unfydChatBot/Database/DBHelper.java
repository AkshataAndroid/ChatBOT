package com.unfyd.unfydChatBot.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

public class DBHelper extends OrmLiteSqliteOpenHelper {
    public static final String DB_NAME = "chat3.db";
    private static final int DB_VERSION = 1;
   public Dao<clsChatDetail, ?> dao;
   public Context mContext;
    private DBHelper dbHelper = null;
    public DBHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, clsChatDetail.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao.CreateOrUpdateStatus createOrUpdate(clsChatDetail obj) throws SQLException {
        try {
            List mChatDetail=getAll(clsChatDetail.class);
            //if(mChatDetail!=null && mChatDetail.size()<=0) {
                dao = (Dao<clsChatDetail, ?>) getDao(obj.getClass());
                return dao.createOrUpdate(obj);
//            }else
//            {
//                dao.update(obj);
//            }
           // return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List getAll(Class clazz) throws SQLException {
        try {
      dao   = getDao(clazz);
            return dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void updateClsChatDetail(clsChatDetail student) {

        try {
            dao = (Dao<clsChatDetail, ?>) getDao(student.getClass());
            dao.createOrUpdate(student);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<clsChatDetail, ?> getDao() throws SQLException {
        if(dao == null) {
            try {
                dao = getDao(clsChatDetail.class);
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        }
        return dao;
    }
    public DBHelper getHelper() {
        if (dbHelper == null) {
            dbHelper = OpenHelperManager.getHelper(mContext, DBHelper.class);
        }
        return dbHelper;
    }

    public void updateFields(String fileDownloaded,String filePath,String type)
    {
        try {
            UpdateBuilder<clsChatDetail, ?> updateBuilder = getDao().updateBuilder();
// update the goal_title and goal_why fields
            updateBuilder.updateColumnValue("is_File_Downloaded", fileDownloaded);
            updateBuilder.updateColumnValue("file_Path", filePath);
// but only update the rows where the description is some value
            updateBuilder.where().eq("message_Type", type);
// actually perform the update
            updateBuilder.update();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }
}
