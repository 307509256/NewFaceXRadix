package com.baidu.idl.face.main;


import com.baidu.idl.face.main.model.User;

/**
 * Created by chaixiaogang on 2018/11/8.
 */

public interface FaceRecognitionCallback {

    void onResponse(User user, String type);
    void onResponseQr(String cardNo);

    void onNoFace();
}
