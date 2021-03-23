package com.yxkj.facexradix.netty.util;

import android.content.Context;
import android.util.Log;

import com.yxdz.commonlib.util.LogUtils;
import com.yxdz.commonlib.util.SPUtils;
import com.yxkj.facexradix.Constants;
import com.yxkj.facexradix.MyApplication;
import com.yxkj.facexradix.netty.Message;
import com.yxkj.facexradix.room.FacexDatabase;
import com.yxkj.facexradix.room.bean.Record;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.channel.Channel;

public class ClientUtil {


    public static boolean isOnCall = false;
    public static String Receiver;
    public static String Sender;
    private static String TAG = "ClientUtil";
    public static AtomicInteger count = new AtomicInteger(0);

    public static ConcurrentHashMap<Integer, Message> msgMap = new ConcurrentHashMap<Integer, Message>();

    public static int increase() {
        return count.incrementAndGet();
    }

    public static void sendSn(Context context) {

    }

    /**
     * 发送数据
     *
     * @param msg
     * @return
     */
    public static Message sendMessage(Message msg) {
        Channel channel = ClientMain.getChannel();
        if (channel != null && channel.isActive()) {
            try {
                //发送数据
                int id = increase();
                msg.setMsgId(id);
                for (int i = 0; i < 3; ++i) {
                    Log.e(TAG, "发送数据：" + msg.toString());
                    channel.writeAndFlush(msg);
                    Thread.sleep((1 + i) * 500);
                    //检查结果是否已返回
                    if (msgMap.containsKey(id)) {
                        Message message = msgMap.get(id);
                        msgMap.clear();
                        return message;
                    }

                }
            } catch (InterruptedException e) {

            }
        }
        return null;
    }

    //    boolean aBoolean = SPUtils.getInstance().getBoolean(Constants.SERVER_CONNECT_STATS);
//        if (!aBoolean){
//        return msg;
//    }
    public static Message requestMessage(Message msg) {
        Message result = null;
        Channel channel = ClientMain.getChannel();
        if (channel != null) {
            try {
                //发送数据
                Log.e(TAG, "发送数据：" + msg.toString());
                int id = msg.getMsgId();
                channel.writeAndFlush(msg);
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(50);
                    if (msgMap.containsKey(id)) {
                        result = msgMap.get(id);
                        msgMap.remove(id);
                        break;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "异常：" + e.getMessage());
            }
        }
        return result;
    }


    public static Message requestMessageForRecord(Message msg) {
        Channel channel = ClientMain.getChannel();
        if (channel != null) {
            try {
                //发送数据
                Log.e(TAG, "发送数据：" + msg.toString());
                channel.writeAndFlush(msg);
            } catch (Exception e) {
                Log.e(TAG, "异常：" + e.getMessage());
            }
        }
        return null;
    }


    public static void sendMessage(String sendtype, int roomId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("sender", Sender);
        map.put("receiver", Receiver);

        HashMap<String, Object> forward = new HashMap<>();
        forward.put("sendtype", sendtype);
        forward.put("roomId", roomId);
        map.put("forward", forward);

        Message message = new Message();
        message.setMsgId(increase());
        message.setCode(302);
        message.setData(map);


        Channel channel = ClientMain.getChannel();
        if (channel != null) {
            channel.writeAndFlush(message);
        }
    }

    public static void sendMessage(String sendtype) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("sender", Sender);
        map.put("receiver", Receiver);

        HashMap<String, Object> forward = new HashMap<>();
        forward.put("sendtype", sendtype);
        map.put("forward", forward);

        Message message = new Message();
        message.setMsgId(increase());
        message.setCode(302);
        message.setData(map);


        Channel channel = ClientMain.getChannel();
        if (channel != null) {
            channel.writeAndFlush(message);
        }
    }


    public static void sendEventRecord(int eventCode) {
        Message message = new Message();
        HashMap<String, Object> map = new HashMap();
        map.put("event", eventCode);
        map.put("time", System.currentTimeMillis());
        message.setMsgId(1);
        message.setCode(115);
        message.setData(map);
        requestMessageForRecord(message);
    }

    public static void sendOpenRecord(String userId, int openType, int statu, String cardNo) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (SPUtils.getInstance().getInt(Constants.DEVICE_CTRL_MODE, 0) == 1) {
                    return;
                }
                if (SPUtils.getInstance().getBoolean(Constants.SERVER_CONNECT_STATS, false)) {
                    Message message = new Message();
                    message.setMsgId(increase());
                    message.setCode(116);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("userId", userId);
                    map.put("openTime", System.currentTimeMillis());
//                    map.put("openType", 40+openType);
                    map.put("openType", openType);
                    map.put("statu", statu);
                    map.put("card", cardNo);
                    message.setData(map);
                    LogUtils.d(TAG, "发送回复：" + message.toString());
                    requestMessage(message);
                } else {
                    Record record = new Record();
                    record.setCard(cardNo);
                    record.setUserId(userId);
                    record.setOpenTime(System.currentTimeMillis());
                    record.setStatu(statu);
                    record.setOpenType(openType);
                    FacexDatabase.getInstance(MyApplication.getAppContext()).getRecord().insert(record);
                }
            }
        }).start();
    }

    static void sendOpenRecord(Record record) {
        Message message = new Message();
        HashMap<String, Object> map = new HashMap();
        map.put("userId", record.getUserId());
        map.put("openTime", record.getOpenTime());
        map.put("openType", record.getOpenType());
        map.put("statu", record.getStatu());
        map.put("card", record.getCard());
        message.setMsgId(increase());
        message.setCode(116);
        message.setData(map);
        Channel channel = ClientMain.getChannel();
        if (channel != null) {
            channel.writeAndFlush(message);
        }
    }
}
