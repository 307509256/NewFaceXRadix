package com.yxkj.facexradix.netty.controller;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.idl.face.main.api.FaceApi;
import com.baidu.idl.face.main.model.User;
import com.baidu.idl.main.facesdk.utils.FileUitls;
import com.yxkj.facexradix.netty.Message;
import com.yxkj.facexradix.netty.util.ClientMain;
import com.yxkj.facexradix.ui.activity.MainActivity;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import io.netty.channel.Channel;

public class UserControllerService extends Service {


    private CreateReceiver createReceiver;
    private UpdateReceiver updateReceiver;
    //    private OrperationReceiver orperationReceiver;
    private DeleteReceiver deleteReceiver;
    private Channel channel;
    private MainActivity mainActivity;
    private long current;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        initReceiver();
        registerReceivers();
    }

    private void sendSusses(Intent intent) {
        channel = ClientMain.getChannel();
        if (channel != null) {
            Message message = new Message();
            message.setMsgId(intent.getIntExtra("msgid", 1));
            message.setCode(intent.getIntExtra("cmd", 1));
            HashMap<String, Object> map = new HashMap<>();
            map.put("code", 0);
            map.put("msg", "成功");
            message.setData(map);
            channel.writeAndFlush(message);
            Log.e("--------", "time ::::" + (System.currentTimeMillis() - current));
        }
    }

    private void registerReceivers() {
        registerReceiver(createReceiver, new IntentFilter("TCP_INTENT_USER_CREATE"));
        registerReceiver(updateReceiver, new IntentFilter("TCP_INTENT_USER_UPDATE"));
//        registerReceiver(orperationReceiver, new IntentFilter("TCP_INTENT_USER_FACE_ORPERATION"));
        registerReceiver(deleteReceiver, new IntentFilter("TCP_INTENT_USER_DELETE"));
    }

    private void initReceiver() {
        createReceiver = new CreateReceiver();
        updateReceiver = new UpdateReceiver();
//        orperationReceiver = new OrperationReceiver();
        deleteReceiver = new DeleteReceiver();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(createReceiver);
        unregisterReceiver(updateReceiver);
//        unregisterReceiver(orperationReceiver);
        unregisterReceiver(deleteReceiver);
        super.onDestroy();
    }

    private class CreateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // log.d("CreateReceiver", intent.getStringExtra("data"));
            current = System.currentTimeMillis();
            sendSusses(intent);
            JSONArray data = JSON.parseArray(intent.getStringExtra("data"));
            if (data.size() != 0) {
                for (int i = 0; i < data.size(); i++) {
                    String pwd = data.getJSONObject(i).getString("pwd");
                    String userId = data.getJSONObject(i).getString("userId");
                    String name = data.getJSONObject(i).getString("name");
                    String card = data.getJSONObject(i).getString("card").toUpperCase();
                    List<User> facex = FaceApi.getInstance().getUserListByCardNo(card);
                    if (facex != null && facex.size() > 0) {
                        for (User user : facex) {
//                            if (user.getCard().equals(card)) {
                            File faceimage = new File(FileUitls.getFaceDirectory(), user.getImageName());
                            faceimage.delete();
                            FaceApi.getInstance().userDelete(user.getUserId(), "facex");
//                            }
                        }
                    }
                    User user = new User();
                    user.setCard(card);
                    user.setPwd(pwd);
                    user.setUserId(userId);
                    user.setUserName(name);
                    user.setGroupId("facex");
                    user.setUserInfo(name);
                    FaceApi.getInstance().userAdd(user);
                }
            }
        }
    }

    private class UpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            JSONArray data = JSON.parseArray(intent.getStringExtra("data"));
            String pwd = data.getJSONObject(0).getString("pwd");
            String userId = data.getJSONObject(0).getString("userId");
            String name = data.getJSONObject(0).getString("name");
            String card = data.getJSONObject(0).getString("card");
            List<User> facex = FaceApi.getInstance().getUserListByUserName("facex", name);
            if (facex.size() > 0) {
                for (User user : facex) {
                    if (user.getCard().equals(card)) {
                        user.setPwd(pwd);
                        user.setUserId(userId);
                        user.setUserInfo(name);
                        user.setUserName(name);
                        user.setCard(card);
                        FaceApi.getInstance().userUpdate(user);
                    }
                }
            }
            sendSusses(intent);
        }
    }

    private class DeleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // log.d("CreateReceiver", intent.getStringExtra("data"));
            JSONObject jsonObject = JSON.parseObject(intent.getStringExtra("data"));
            String delete = jsonObject.getString("delete");
            List<User> facex = FaceApi.getInstance().getUserList("facex");
            for (User user : facex) {
                if (user.getUserId().equals(delete)) {
                    File faceimage = new File(FileUitls.getFaceDirectory(), user.getImageName());
                    faceimage.delete();
                    FaceApi.getInstance().userDelete(delete, "facex");
                }
            }
            // log.d("DeleteReceiver", "FaceApi.getInstance().getmUserNum():" + FaceApi.getInstance().getmUserNum());
            sendSusses(intent);
        }
    }
}
