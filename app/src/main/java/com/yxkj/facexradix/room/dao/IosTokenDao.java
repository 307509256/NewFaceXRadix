package com.yxkj.facexradix.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.yxkj.facexradix.room.bean.IosToken;
import com.yxkj.facexradix.room.bean.Record;

import java.util.List;


@Dao
public interface IosTokenDao {



    /**
     * @ClassName: UserDao
     * @Desription:
     * @author: Dreamcoding
     * @date: 2018/12/6
     */



        @Query("SELECT * FROM IosToken")
        List<IosToken> listAll();

        @Query("SELECT * FROM IosToken LIMIT :start OFFSET :sum")
        List<IosToken> list(int start, int sum);






        @Query("SELECT * FROM IosToken WHERE sn=:sn")
        IosToken listTokenBySn(String sn);








        @Query("SELECT COUNT(*) FROM IosToken")
        int count();

        @Insert
        void insert(IosToken... iosTokens);

        @Update
        void update(IosToken... iosTokens);

        @Delete
        void delete(IosToken... iosTokens);

        @Query("DELETE FROM IosToken WHERE 1=1")
        void deleteAll();



}
