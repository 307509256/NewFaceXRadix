package com.yxkj.facexradix.netty.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yxdz.commonlib.util.DeviceUtil;
import com.yxdz.commonlib.util.SPUtils;
import com.yxkj.facexradix.Constants;
import com.yxkj.facexradix.MyApplication;
import com.yxkj.facexradix.netty.Message;
import com.yxkj.facexradix.room.FacexDatabase;
import com.yxkj.facexradix.room.bean.Record;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;


/**
 * @author Administrator
 */
public class ClientHandler extends SimpleChannelInboundHandler<Message> {

    private final Context context = MyApplication.getAppContext();

    private String TAG = "ClientHandler";
    private int flag;


    /**
     * 已连接并就绪
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Log.e(TAG, "连接就绪");
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean b = sendSn();
                if (!b) {
                    SPUtils.getInstance().put(Constants.SERVER_CONNECT_STATS, false);
                    Intent intent = new Intent("TCP_DEVICE_VERIFICATION_ERROR");
                    context.sendBroadcast(intent);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ctx.close();
                } else {
                    Log.e(TAG, "验证成功");
                    Intent server = new Intent(Constants.BROADCAST_RECEIVER_TCP_CONNECTION);
                    server.putExtra("type", true);
                    context.sendBroadcast(server);
                    SPUtils.getInstance().put(Constants.SERVER_CONNECT_STATS, true);
                    List<Record> records = FacexDatabase.getInstance(context).getRecord().listAll();
                    for (Record r : records) {
                        ClientUtil.sendOpenRecord(r);
                    }
                    FacexDatabase.getInstance(context).getRecord().deleteAll();
                }
            }
        }).start();
    }


    private boolean sendSn() {
        HashMap<String, String> map = new HashMap<>();
        map.put("deviceSn", DeviceUtil.getLocalMacAddress(context));
        ClientUtil.Sender = DeviceUtil.getLocalMacAddress(context);
        Message stringMessage = new Message();
        stringMessage.setMsgId(ClientUtil.increase());
        stringMessage.setCode(100);
        stringMessage.setData(map);
        Message message = ClientUtil.requestMessage(stringMessage);
        if (message != null) {
            JSONObject da = null;
            int result = 0;
            try {
                if (message.getData() != null) {
                    da = new JSONObject((String) message.getData());
                    if (da != null) {
                        result = da.getInt("code");
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (result == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 读取数据
     */
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        ClientUtil.msgMap.put(msg.getMsgId(), msg);
        Intent intent = null;
        // log.d("ClientHandler", "msg.getCode():" + msg.getCode());
        switch (msg.getCode()) {
//            case 100:
//                /*JSONObject da = new JSONObject((String) msg.getData());
//                int result = da.getInt("code");
//                if (result == 0) {
//                    Log.e(TAG, "验证成功");
//                    Int54ent server = new Intent(Constants.BROADCAST_RECEIVER_TCP_CONNECTION);
//                    server.putExtra("type", true);
//                    context.sendBroadcast(server);
//                    SPUtils.getInstance().put(Constants.SERVER_CONNECT_STATS, true);
//                    return;
//                } else {
//                    intent = new Intent("TCP_DEVICE_VERIFICATION_ERROR");
//                    ctx.channel().eventLoop().schedule(new Runnable() {
//                        @Override
//                        public void run() {
//                            ctx.close();
//                        }
//                    }, 5000, TimeUnit.SECONDS);
//                }*/
//                return;
            case 101:
                Log.e(TAG, "心跳包回复");
                MyApplication.SetTime((Long.parseLong(msg.getData().toString())));
                return;
            case 102:
                intent = new Intent("TCP_INTENT_DEVICE_PASSWORD");
                break;
            case 103:
                intent = new Intent("TCP_INTENT_DEVICE_SETTINGS");
                break;
            case 104:
                intent = new Intent("TCP_INTENT_DEVICE_TIME");
                break;
            case 105:
                intent = new Intent("TCP_INTENT_DEVICE_NETWORK");
                break;
            case 106:
                intent = new Intent("TCP_INTENT_DEVICE_RESTART");
                break;
            case 107:
                intent = new Intent("TCP_INTENT_DEVICE_RESET");
                break;
            case 108:
                intent = new Intent("TCP_INTENT_DEVICE_OPENDOOR");
                break;
            case 109:
                intent = new Intent("TCP_INTENT_DEVICE_UPDATE");
                break;
            case 110:
                intent = new Intent("TCP_INTENT_DEVICE_PERIOD_ISSUED");
                break;
            case 111:
                intent = new Intent("TCP_INTENT_DEVICE_PERIOD_DELETE");
                break;
            case 112:
                intent = new Intent("TCP_INTENT_DEVICE_PERIOD_HOLIDAY_ISSUED");
                break;
            case 113:
                intent = new Intent("TCP_INTENT_DEVICE_PERIOD_HOLIDAY_DELETE");
                break;
            case 114:
                intent = new Intent("TCP_INTENT_DEVICE_STATUS");
                break;
            case 201:
                intent = new Intent("TCP_INTENT_USER_CREATE");
                break;
            case 202:
                intent = new Intent("TCP_INTENT_USER_UPDATE");
                break;
            case 203:
                intent = new Intent("TCP_INTENT_USER_FACE_ORPERATION");
                break;
            case 204:
                intent = new Intent("TCP_INTENT_USER_DELETE");
                break;
            case 301:
                intent = new Intent("TCP_RTC_SN");
                break;
            case 302:
                intent = new Intent("TCP_RTC_FORWARD");
                try {
                    String data = msg.getData().toString();
                    // log.d("ClientHandler", data);
                    JSONObject jsonObject = new JSONObject(data);
                    JSONObject forwardObject = new JSONObject(jsonObject.get("forward").toString());
                    ClientUtil.Receiver = jsonObject.getString("sender");
                    switch (forwardObject.getString("sendtype")) {
                        case "close":
                            context.sendBroadcast(new Intent("CLOSE_TRTCACTIVITY"));
                            break;
                        case "open":
                            context.sendBroadcast(new Intent("OPENDOOR_VIA_TRTC"));
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 303:
                //社区可视对讲房间号下发
                intent = new Intent("TCP_RTC_FOR_MINI_PROGRAM");
                break;
            default:
                intent = new Intent();
                break;
        }

        intent.putExtra("cmd", msg.getCode());
        intent.putExtra("msgid", msg.getMsgId());
        if (msg.getData() != null) {
            intent.putExtra("data", msg.getData().toString());
        }
        context.sendBroadcast(intent);
    }

    /**
     * 发送心跳包
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            if (((IdleStateEvent) evt).state() == IdleState.WRITER_IDLE) {
                Log.e(TAG, "发送心跳包........");
                ctx.writeAndFlush(new Message(0, CommonsCMD.HEART_BEAT, System.currentTimeMillis()));
            } else if (((IdleStateEvent) evt).state() == IdleState.READER_IDLE) {
                Log.e(TAG, "触发心跳读事件........");
                ctx.close();
                flag = 1;
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 处理异常事件
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) throws Exception {
        Log.e(TAG, "异常信息：" + e.getMessage());
        ctx.close();
        flag = 1;
    }


    /**
     * 已断开连接
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Intent intent = new Intent(Constants.BROADCAST_RECEIVER_TCP_CONNECTION);
        intent.putExtra("type", false);
        SPUtils.getInstance().put(Constants.SERVER_CONNECT_STATS, false);
        context.sendBroadcast(intent);
        Log.e(TAG, "断开连接");
        ClientMain.doConnect();
    }
}
