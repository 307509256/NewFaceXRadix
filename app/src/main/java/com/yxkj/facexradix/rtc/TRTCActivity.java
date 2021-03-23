package com.yxkj.facexradix.rtc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.starrtc.starrtcsdk.api.XHClient;
import com.starrtc.starrtcsdk.core.im.message.XHIMMessage;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.yxkj.facexradix.R;
import com.yxkj.facexradix.netty.util.ClientUtil;
import com.yxkj.facexradix.room.FacexDatabase;
import com.yxkj.facexradix.room.bean.IosToken;
import com.yxkj.facexradix.room.dao.IosTokenDao;
import com.yxkj.facexradix.star.MessageBean;
import com.yxkj.facexradix.star.SendUtils;
import com.yxkj.facexradix.star.database.CoreDB;
import com.yxkj.facexradix.star.database.HistoryBean;
import com.yxkj.facexradix.star.demo.MLOC;
import com.yxkj.facexradix.star.utils.AEvent;
import com.yxkj.facexradix.star.utils.IEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.tencent.liteav.demo.trtc.TRTCVideoRoomActivity.KEY_ROOM_ID;
import static com.tencent.liteav.demo.trtc.TRTCVideoRoomActivity.KEY_SDK_APP_ID;
import static com.tencent.liteav.demo.trtc.TRTCVideoRoomActivity.KEY_USER_ID;
import static com.tencent.liteav.demo.trtc.TRTCVideoRoomActivity.KEY_USER_SIG;
import static com.tencent.trtc.TRTCCloudDef.TRTC_APP_SCENE_VIDEOCALL;

public class TRTCActivity extends AppCompatActivity implements View.OnClickListener, IEventListener {


    private int sdkAppId;
    private int roomId;
    private String userId;
    private String userSig;
    private TRTCCloud trtcCloud;
    private TXCloudVideoView localVideoView;
    private FrameLayout remoteVideoView;
    private AudioManager audioManager;
    private TextView connectType;
    private MediaPlayer mediaPlayer;
    private int streamVolume;
    private Timer noAnserTimer;
    private ArrayList<String> snList;

    @Override
    protected void onDestroy() {
        trtcCloud.exitRoom();
        if (noAnserTimer != null) {
            noAnserTimer.cancel();
            noAnserTimer.purge();
            noAnserTimer = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, streamVolume, AudioManager.FLAG_FIXED_VOLUME);
        if(closeTRTCActivity != null){
            unregisterReceiver(closeTRTCActivity);
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trtc);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        initView();
        trtcCloud = TRTCCloud.sharedInstance(this);
        TRTCCloudListenerImpl trtcCloudListener = new TRTCCloudListenerImpl(this, trtcCloud, remoteVideoView);
        trtcCloud.setListener(trtcCloudListener);

        Intent intent = getIntent();
        sdkAppId = intent.getIntExtra(KEY_SDK_APP_ID, 0);
        roomId = intent.getIntExtra(KEY_ROOM_ID, 0);
        userId = intent.getStringExtra(KEY_USER_ID);
        userSig = intent.getStringExtra(KEY_USER_SIG);

        snList = intent.getStringArrayListExtra("snList");

        noAnserTimer = new Timer();
        noAnserTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
                closeSn("2");
            }
        }, 120000);

        initAudio();
        registerReceiver(closeTRTCActivity, new IntentFilter("CLOSE_TRTCACTIVITY"));
        TRTCCloudDef.TRTCParams trtcParams = new TRTCCloudDef.TRTCParams(sdkAppId, userId, userSig, roomId, "", "");
        trtcCloud.enterRoom(trtcParams, TRTC_APP_SCENE_VIDEOCALL);
        startLocalPreview(true, localVideoView);
    }

    private void closeSn(String type) {
        for (String s : snList) {
            MessageBean closeBean = new MessageBean("close", type);
            SendUtils.sendMsg(closeBean, s);
        }
    }

    private void initView() {
        localVideoView = findViewById(R.id.localVideoView);
        remoteVideoView = findViewById(R.id.remoteVideoView);
        connectType = findViewById(R.id.connectType);
        findViewById(R.id.ivEnd).setOnClickListener(this);
        findViewById(R.id.volume_down).setOnClickListener(this);
        findViewById(R.id.volume_up).setOnClickListener(this);
    }

    void startLocalPreview(boolean frontCamera, TXCloudVideoView localVideoView) {
        trtcCloud.setLocalViewFillMode(TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FIT);
        trtcCloud.startLocalPreview(frontCamera, localVideoView);
        trtcCloud.startLocalAudio();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivEnd:
                closeSn("1");
                finish();
                break;
            case R.id.volume_down:
                audioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_LOWER,
                        AudioManager.FX_FOCUS_NAVIGATION_UP);
                break;
            case R.id.volume_up:
                audioManager.adjustStreamVolume(AudioManager.STREAM_VOICE_CALL, AudioManager.ADJUST_RAISE,
                        AudioManager.FX_FOCUS_NAVIGATION_UP);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        trtcCloud.exitRoom();
    }

    public void connectTypeChange() {
        if (noAnserTimer != null) {
            noAnserTimer.cancel();
            noAnserTimer.purge();
            noAnserTimer = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        connectType.setText("连接成功 正在通话中......");
    }


    private void initAudio() {
        mediaPlayer = MediaPlayer.create(this, R.raw.sound);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 7, AudioManager.FLAG_FIXED_VOLUME);
    }


    public void LeaveRoom(String sn) {
        finish();
    }


    BroadcastReceiver closeTRTCActivity = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
            closeSn("2");
        }
    };


    @Override
    protected void onResume() {
        addListener();
        super.onResume();
    }


    @Override
    protected void onStop() {
        removeListener();
        super.onStop();
    }

    //   starRtc======================================================================================
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
                    MLOC.showDialog(this, alertData);
                    List<HistoryBean> mHistoryList = new ArrayList<>();
                    List<HistoryBean> list = MLOC.getHistoryList(CoreDB.HISTORY_TYPE_C2C);
                    if (list != null && list.size() > 0) {
                        mHistoryList.addAll(list);
                    }
                    for (HistoryBean historyBean : mHistoryList) {
                        if (historyBean.getConversationId().equals(alertData.getString("farId"))) {
                            checkJson(historyBean.getLastMsg(), historyBean.getConversationId());
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
                    MLOC.showDialog(this, alertData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case AEvent.AEVENT_USER_OFFLINE:
                MLOC.showMsg(this, "服务已断开");
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
                MLOC.showMsg(this, "服务已断开");
                // log.d("starRtc", "连接异常，请重新登录");
                break;
        }
    }

    private boolean isArlreadyJoin = false;

    private void checkJson(String msg, String sn) {
        // log.d("starRtc", "dispatchEvent: " + msg);
        Gson gson = new Gson();
        try {
//            msg = SecretUtils.decrypt3DES(msg);
            MessageBean messageBean = gson.fromJson(msg, MessageBean.class);
            switch (messageBean.getType()) {
                case "close":
                    snList.remove(sn);
                    if (snList.isEmpty()) {
                        sendBroadcast(new Intent("CLOSE_TRTCACTIVITY"));
                    }
                    break;
                case "open":
                    sendBroadcast(new Intent("OPENDOOR_VIA_TRTC"));
                    break;
                case "join":

                    break;
                case "token":
                    IosTokenDao iosTokenDao = FacexDatabase.getInstance(this).getIosTokenDao();
                    String token = String.valueOf(messageBean.getData());
                    IosToken iosToken = iosTokenDao.listTokenBySn(sn);
                    if (iosToken != null) {
                        iosTokenDao.delete(iosToken);
                    }
                    IosToken newIosToken = new IosToken();
                    assert iosToken != null;
                    iosToken.setSn(sn);
                    iosToken.setTokne(token);
                    iosTokenDao.insert(newIosToken);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enterRoom(String sn) {
//        if (isArlreadyJoin){
//            MessageBean closeBean = new MessageBean("close", "2");
//            SendUtils.sendMsg(closeBean, sn);
//            return;
//        }
        if (!isArlreadyJoin) {
            isArlreadyJoin = true;
            ClientUtil.Receiver = sn;
            snList.remove(sn);
            closeSn("2");
        }
    }
}
