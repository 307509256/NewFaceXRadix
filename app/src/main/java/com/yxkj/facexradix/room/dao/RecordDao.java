package com.yxkj.facexradix.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.yxkj.facexradix.room.bean.Record;

import java.util.List;


/**
 * @ClassName: UserDao
 * @Desription:
 * @author: Dreamcoding
 * @date: 2018/12/6
 */

@Dao
public interface RecordDao {

    @Query("SELECT * FROM record")
    List<Record> listAll();

    @Query("SELECT * FROM record LIMIT :start OFFSET :sum")
    List<Record> list(int start, int sum);

    /**
     * 开门类型,1:远程开门、2：人脸识别开门、3：可视对讲开门、4：二维码开门、5：密码开门；0：暂无权限
     * @param openType
     * @return
     */
    @Query("SELECT * FROM record WHERE openType=:openType")
    List<Record> listRecordByOpenType(int openType);



    @Query("SELECT * FROM record WHERE userId=:userId")
    Record listRecordByUserid(String userId);



    @Query("SELECT * FROM record WHERE statu=:statu")
    List<Record> listRecordByStatu(int statu);




    @Query("SELECT COUNT(*) FROM record")
    int count();

    @Insert
    void insert(Record... records);

    @Update
    void update(Record... records);

    @Delete
    void delete(Record... records);

    @Query("DELETE FROM record WHERE 1=1")
    void deleteAll();

}
