package com.yxkj.facexradix.star.demo;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.core.im.message.XHIMMessage;
import com.yxkj.facexradix.star.database.CoreDB;

import com.yxkj.facexradix.star.database.HistoryBean;
import com.yxkj.facexradix.star.utils.AEvent;
import com.yxkj.facexradix.star.utils.IEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class BaseActivity extends AppCompatActivity implements IEventListener {

    @Override
    protected void onResume() {
        super.onResume();
        addListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeListener();
    }

    private void addListener() {
        AEvent.addListener(AEvent.AEVENT_VOIP_REV_CALLING, this);
        AEvent.addListener(AEvent.AEVENT_VOIP_P2P_REV_CALLING, this);
        AEvent.addListener(AEvent.AEVENT_C2C_REV_MSG, this);
        AEvent.addListener(AEvent.AEVENT_REV_SYSTEM_MSG, this);
        AEvent.addListener(AEvent.AEVENT_GROUP_REV_MSG, this);
        AEvent.addListener(AEvent.AEVENT_USER_ONLINE, this);
        AEvent.addListener(AEvent.AEVENT_USER_OFFLINE, this);
        AEvent.addListener(AEvent.AEVENT_CONN_DEATH, this);
    }

    private void removeListener() {
        AEvent.removeListener(AEvent.AEVENT_VOIP_REV_CALLING, this);
        AEvent.removeListener(AEvent.AEVENT_VOIP_P2P_REV_CALLING, this);
        AEvent.removeListener(AEvent.AEVENT_C2C_REV_MSG, this);
        AEvent.removeListener(AEvent.AEVENT_REV_SYSTEM_MSG, this);
        AEvent.removeListener(AEvent.AEVENT_GROUP_REV_MSG, this);
        AEvent.removeListener(AEvent.AEVENT_USER_ONLINE, this);
        AEvent.removeListener(AEvent.AEVENT_USER_OFFLINE, this);
        AEvent.removeListener(AEvent.AEVENT_CONN_DEATH, this);
    }

    @Override
    public void dispatchEvent(String aEventID, boolean success, final Object eventObj) {
        switch (aEventID) {
            case AEvent.AEVENT_VOIP_REV_CALLING:
                break;
            case AEvent.AEVENT_VOIP_P2P_REV_CALLING:

                break;
            case AEvent.AEVENT_C2C_REV_MSG:
            case AEvent.AEVENT_REV_SYSTEM_MSG:
                MLOC.hasNewC2CMsg = true;
                try {
                    XHIMMessage revMsg = (XHIMMessage) eventObj;
                    JSONObject alertData = new JSONObject();
                    alertData.put("listType", 0);
                    alertData.put("farId", revMsg.fromId);
                    alertData.put("msg", "收到一条新消息");
                    MLOC.showDialog(BaseActivity.this, alertData);
                    List<HistoryBean> mHistoryList = new ArrayList<>();
                    List<HistoryBean> list = MLOC.getHistoryList(CoreDB.HISTORY_TYPE_C2C);
                    if(list!=null&&list.size()>0){
                        mHistoryList.addAll(list);
                    }
                    for (HistoryBean historyBean : mHistoryList) {
                        if(historyBean.getConversationId().equals(alertData.getString("farId"))){
                            // log.d("starRtc", "dispatchEvent: "+historyBean.getLastMsg());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case AEvent.AEVENT_GROUP_REV_MSG:
                MLOC.hasNewGroupMsg = true;
                try {
                    XHIMMessage revMsg = (XHIMMessage) eventObj;
                    JSONObject alertData = new JSONObject();
                    alertData.put("listType", 1);
                    alertData.put("farId", revMsg.targetId);
                    alertData.put("msg", "收到一条群消息");
                    MLOC.showDialog(BaseActivity.this, alertData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case AEvent.AEVENT_USER_OFFLINE:
                MLOC.showMsg(BaseActivity.this, "服务已断开");
                // log.d("starRtc", "连接中...");
                break;
            case AEvent.AEVENT_USER_ONLINE:
                if (XHClient.getInstance().getIsOnline()) {
                    sendBroadcast(new Intent("TCP_CONNECTION_SUSSESS"));
                } else {
                    sendBroadcast(new Intent("TCP_CONNECTION_FAIL"));
                }

                break;
            case AEvent.AEVENT_CONN_DEATH:
                MLOC.showMsg(BaseActivity.this, "服务已断开");
                // log.d("starRtc", "连接异常，请重新登录");
                break;
        }
    }

}
