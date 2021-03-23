package com.yxkj.facexradix.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;


import com.yxkj.facexradix.room.bean.IosToken;
import com.yxkj.facexradix.room.bean.Record;
import com.yxkj.facexradix.room.bean.Time;
import com.yxkj.facexradix.room.dao.IosTokenDao;
import com.yxkj.facexradix.room.dao.RecordDao;
import com.yxkj.facexradix.room.dao.TimeDao;


/**
 * @ClassName: FadoxDatabase
 * @Desription:
 * @author: Dreamcoding
 * @date: 2018/12/6
 */

@Database(entities = {IosToken.class,Record.class, Time.class}, version = 2,exportSchema = false)
public abstract class FacexDatabase extends RoomDatabase {

    private static final String DB_NAME = "FacexDatabase.db";
    private static volatile FacexDatabase instance;

    public static synchronized FacexDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static FacexDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                FacexDatabase.class,
                DB_NAME).allowMainThreadQueries().fallbackToDestructiveMigration().build();
    }
    public abstract RecordDao getRecord();
    public abstract TimeDao getTimeDao();
    public abstract IosTokenDao getIosTokenDao();
}
