package com.yxkj.facexradix.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.yxkj.facexradix.room.bean.Time;

import java.util.List;


@Dao
public interface TimeDao {

    @Query("SELECT * FROM time")
    List<Time> listTime();

    @Query("SELECT * FROM time LIMIT :sum OFFSET :start")
    List<Time> listTime(int sum, int start);

    @Query("SELECT * FROM time")
    Time getTime();

    @Insert
    void insert(Time... times);

    @Update
    void update(Time... times);

    @Delete
    void delete(Time... times);

    @Query("DELETE FROM time WHERE 1=1")
    void deleteAll();
}
