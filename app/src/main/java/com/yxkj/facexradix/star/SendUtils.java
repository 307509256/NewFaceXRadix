package com.yxkj.facexradix.star;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.apiInterface.IXHResultCallback;
import com.starrtc.starrtcsdk.core.im.message.XHIMMessage;
import com.yxdz.commonlib.util.MD5Util;
import com.yxkj.facexradix.star.demo.MLOC;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendUtils {

    private final static String APP_SECRET = "922663785ca076c592d2a96be541ce34";
    private final static String APP_KEY = "m2ef4b4cc31c00";
    private static String sign;

    static public void sendMsgIos(MessageBean msg, String registrationId) {

        Gson gson = new GsonBuilder().serializeNulls().setLongSerializationPolicy(LongSerializationPolicy.STRING).disableHtmlEscaping().create();
        String json = gson.toJson(msg);
        MobPushBean mobPushBean = new MobPushBean();
        mobPushBean.setAppkey("m2ef4b4cc31c00");
        mobPushBean.setContent("室外人脸识别机邀请你进行通话");
        mobPushBean.setIosProduction(0);
        mobPushBean.setExtras(json);
        List<Integer> integers = new ArrayList<>();
        integers.add(2);
        mobPushBean.setPlats(integers);
        List<String> registrationIds = new ArrayList<>();
        registrationIds.add(registrationId);
        mobPushBean.setRegistrationIds(registrationIds);
        mobPushBean.setTarget(4);
        mobPushBean.setType(1);

        String pushString = gson.toJson(mobPushBean);
        sign = pushString + APP_SECRET;
        try {
            sign = MD5Util.toMD5(sign);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // log.d("SendUtils", sign);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, pushString);
        Request request = new Request.Builder()
                .url("http://api.push.mob.com/v2/push")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("key", APP_KEY)
                .addHeader("sign", sign)
                .build();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    // log.d("sendUtils", "run: " + response.isSuccessful());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }


    static public void sendMsg(MessageBean msg, String mTargetId) {
        Gson gson = new Gson();
        String json = gson.toJson(msg);
        try {
            XHIMMessage message = XHClient.getInstance().getChatManager().sendMessage(json, mTargetId, new IXHResultCallback() {
                @Override
                public void success(Object data) {
                    MLOC.d("IM_C2C  成功", "消息序号：" + data);
                    // log.d("starRtc", "send: " + "IM_C2C  成功" + "消息序号：" + data);
                }

                @Override
                public void failed(String errMsg) {
                    // log.d("starRtc", "send: " + "IM_C2C  失败" + "消息序号：" + errMsg);
                    MLOC.d("IM_C2C  失败", "消息序号：" + errMsg);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
