package com.yxkj.facexradix.rtc;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.trtc.TRTCCloud;
import com.tencent.trtc.TRTCCloudDef;
import com.tencent.trtc.TRTCCloudListener;
import com.tencent.trtc.TRTCStatistics;

import java.util.ArrayList;


public class TRTCCloudListenerImpl extends TRTCCloudListener  {

    private final TRTCActivity trtcActivity;
    private final TRTCCloud trtcCloud;
    private final FrameLayout remoteVideoView;

    public TRTCCloudListenerImpl(TRTCActivity trtcActivity, TRTCCloud trtcCloud, FrameLayout remoteVideoView) {
        this.trtcActivity = trtcActivity;
        this.trtcCloud = trtcCloud;
        this.remoteVideoView = remoteVideoView;
    }

    @Override
    public void onEnterRoom(long elapsed) {

    }

    @Override
    public void onExitRoom(int reason) {
        if (trtcActivity != null){
            trtcActivity.finish();
        }
    }

    @Override
    public void onError(int errCode, String errMsg, Bundle extraInfo) {

    }


    @Override
    public void onRemoteUserEnterRoom(String s) {
        trtcActivity.enterRoom(s);
        super.onRemoteUserEnterRoom(s);
    }

    @Override
    public void onUserVideoAvailable(String userId, boolean available) {
        if (trtcActivity != null) {
            if (available) {
                // 设置remoteView
                TXCloudVideoView remoteView = new TXCloudVideoView(trtcActivity);
                remoteVideoView.addView(remoteView);
                trtcCloud.setRemoteViewFillMode(userId, TRTCCloudDef.TRTC_VIDEO_RENDER_MODE_FILL);
                trtcCloud.startRemoteView(userId, remoteView);
                trtcActivity.connectTypeChange();
            } else {
                //停止观看画面
                trtcCloud.stopRemoteView(userId);
            }
        }
    }

    @Override
    public void onUserSubStreamAvailable(String userId, boolean available) {

    }

    @Override
    public void onUserAudioAvailable(String userId, boolean available) {

    }

    @Override
    public void onFirstVideoFrame(String userId, int streamType, int width, int height) {

    }

    @Override
    public void onUserVoiceVolume(ArrayList<TRTCCloudDef.TRTCVolumeInfo> userVolumes, int totalVolume) {

    }

    @Override
    public void onStatistics(TRTCStatistics statics) {

    }

    @Override
    public void onConnectOtherRoom(String userID, int err, String errMsg) {

    }

    @Override
    public void onDisConnectOtherRoom(int err, String errMsg) {

    }

    @Override
    public void onNetworkQuality(TRTCCloudDef.TRTCQuality localQuality, ArrayList<TRTCCloudDef.TRTCQuality> remoteQuality) {

    }

    @Override
    public void onAudioEffectFinished(int effectId, int code) {

    }

    @Override
    public void onRecvCustomCmdMsg(String userId, int cmdID, int seq, byte[] message) {

    }

    @Override
    public void onRecvSEIMsg(String userId, byte[] data) {

    }

    @Override
    public void onRemoteUserLeaveRoom(String s, int i) {
        trtcActivity.LeaveRoom(s);
        super.onRemoteUserLeaveRoom(s, i);
    }
}
