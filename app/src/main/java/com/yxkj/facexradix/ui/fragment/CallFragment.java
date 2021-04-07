package com.yxkj.facexradix.ui.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.tencent.liteav.demo.trtc.TRTCVideoRoomActivity;
import com.tencent.liteav.demo.trtc.debug.GenerateTestUserSig;
import com.tencent.trtc.TRTCCloudDef;
import com.yxdz.commonlib.base.BaseFragment;
import com.yxdz.commonlib.util.DeviceUtil;
import com.yxdz.commonlib.util.NoDoubleClick;
import com.yxdz.commonlib.util.SPUtils;
import com.yxdz.commonlib.util.ToastUtils;
import com.yxkj.facexradix.Constants;
import com.yxkj.facexradix.R;
import com.yxkj.facexradix.netty.Message;
import com.yxkj.facexradix.netty.util.ClientMain;
import com.yxkj.facexradix.netty.util.ClientUtil;
import com.yxkj.facexradix.room.FacexDatabase;
import com.yxkj.facexradix.room.bean.IosToken;
import com.yxkj.facexradix.rtc.TRTCActivity;
import com.yxkj.facexradix.star.KeepLiveService;
import com.yxkj.facexradix.star.MessageBean;
import com.yxkj.facexradix.star.NetWorkUtils;
import com.yxkj.facexradix.star.SendUtils;
import com.yxkj.facexradix.star.utils.AEvent;
import com.yxkj.facexradix.ui.activity.MainActivity;
import com.yxkj.facexradix.utils.FragmentUtil;
import com.yxkj.facexradix.utils.PermissionChecker;
import com.yxkj.facexradix.view.OnXKeyListener;
import com.yxkj.facexradix.view.XKeyBoard;
import com.yxkj.facexradix.view.XTextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.tencent.liteav.demo.trtc.TRTCVideoRoomActivity.KEY_AUDIO_HANDFREEMODE;
import static com.tencent.liteav.demo.trtc.TRTCVideoRoomActivity.KEY_AUDIO_VOLUMETYOE;
import static com.tencent.liteav.demo.trtc.TRTCVideoRoomActivity.KEY_RECEIVED_AUDIO;
import static com.tencent.liteav.demo.trtc.TRTCVideoRoomActivity.KEY_RECEIVED_VIDEO;
import static com.yxkj.facexradix.netty.util.ClientUtil.requestMessageForRecord;


/**
 * @PackageName: com.yxdz.facex.ui.fragment
 * @Desription:
 * @Author: Dreamcoding
 * @CreatDate: 2019/2/14 17:22
 */
public class CallFragment extends BaseFragment implements View.OnClickListener {
    private static final String[] RequiredPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.RECORD_AUDIO};
    private final int REQUEST_PHONE_PERMISSIONS = 0;
    protected PermissionChecker permissionChecker = new PermissionChecker();
    ArrayList<String> snList;
    private SnReceiver snReceiver;
    private ImageButton ibtnBack;
    private ImageButton ibtnCall;
    private Button ibtnABC;
    private XKeyBoard xKeyBoard;
    private XTextView xEditText;
    private BaseFragment backFragment;
    private String room;
    private boolean isCaller;
    private View ibtnRing;
    private View type2_layout;
    private View type1_layout;
    private View ibtnBack2;
    private LinearLayout qrcode;
    private AlertDialog dialog;
    private String mVideoFile = "";
    private Intent StarRtcintent;
    private int times = 0;

    public void setBackFragment(BaseFragment backFragment) {
        this.backFragment = backFragment;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_call;
    }

    @Override
    public void onModel() {
        ibtnBack = rootView.findViewById(R.id.ibtnBack);
        ibtnCall = rootView.findViewById(R.id.ibtnCall);
        ibtnABC = rootView.findViewById(R.id.ibtnABC);
        xKeyBoard = rootView.findViewById(R.id.xkeyBoard);
        TextView tvCallTip = rootView.findViewById(R.id.tvCallTip);
        xEditText = rootView.findViewById(R.id.xEditText);
        ibtnRing = rootView.findViewById(R.id.ibtnRing);
        type2_layout = rootView.findViewById(R.id.type2_layout);
        type1_layout = rootView.findViewById(R.id.type1_layout);
        ibtnBack2 = rootView.findViewById(R.id.Back);
        qrcode = rootView.findViewById(R.id.qr_code);


        NetWorkUtils.isNetWorkAvailable("www.baidu.com", new Comparable<Boolean>() {

            private Handler handler;

            @Override
            public int compareTo(Boolean available) {
                if (available) {
                    AEvent.setHandler(new Handler());
                    checkPermission();

                } else {
                    Toast.makeText(mActivity, "网络不可用", Toast.LENGTH_SHORT).show();
                }
                return 0;
            }

        });

    }

    @Override
    public void onData(Bundle savedInstanceState) {
        ClientUtil.isOnCall = true;
        type2_layout.setVisibility(View.GONE);
        type1_layout.setVisibility(View.GONE);
        if (SPUtils.getInstance().getInt("CALL_MODE", 0) == 0) {
            type1_layout.setVisibility(View.VISIBLE);
            xEditText.setClickable(false);
            xEditText.setFocusable(false);
            xKeyBoard.setMode(1);
            xEditText.setHint("请输入呼叫的房间号");
            xEditText.setPasswordMode(false);
            ibtnBack.setOnClickListener(this);
            ibtnCall.setOnClickListener(this);
            ibtnABC.setOnClickListener(this);
            xKeyBoard.setKeyListener(new OnXKeyListener() {
                @Override
                public void onKey(char key) {
                    xEditText.addTvTipLimit(key);
                }
            });
        } else {
            type2_layout.setVisibility(View.VISIBLE);
            ibtnRing.setOnClickListener(this);
            ibtnBack2.setOnClickListener(this);
        }

        qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                dialog = builder.create();
                View dialogView = View.inflate(getContext(), R.layout.qr_code, null);
                dialog.setView(dialogView);
                dialog.show();

                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = null;
                try {
                    bitmap = barcodeEncoder.encodeBitmap(DeviceUtil.getLocalMacAddress(getContext()), BarcodeFormat.QR_CODE, 400, 400);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                ImageView imageViewQrCode = (ImageView) dialog.findViewById(R.id.qr_code);
                imageViewQrCode.setImageBitmap(bitmap);
//                dialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
        });


        snReceiver = new SnReceiver();
        getActivity().registerReceiver(snReceiver, new IntentFilter("TCP_RTC_SN"));

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setUpgradeView(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtnBack:
                FragmentUtil.remove(this);
                FragmentUtil.show(backFragment);
                break;
            case R.id.ibtnCall:
                if (!NoDoubleClick.isFastDoubleClick(800)) {
                    room = xEditText.getData();
                    if (!TextUtils.isEmpty(room)) {
                        if(SPUtils.getInstance().getInt("CALL_MODE", 0) != 3) {
                            checkPermissions();
                        }else{
                            int roomNo = (int) ((Math.random() * 9 + 1) * 100000);
                            Message message = new Message();
                            HashMap<String, Object> map = new HashMap();
                            map.put("roomNo", roomNo);
                            message.setCode(304);
                            message.setData(map);
                            ClientUtil.sendMessage(message);
                            startJoinRoomInternal(roomNo, ClientUtil.Sender);
                        }
                    } else {
                        ToastUtils.showShortToast("房间号不能为空");
                    }
                }
                break;
            case R.id.ibtnRing:
                if (!NoDoubleClick.isFastDoubleClick(800)) {
                    room = xEditText.getData();
                    checkPermissions();
                }
                break;
            case R.id.ibtnABC:
                if (xKeyBoard.getMode() == 1) {
                    xKeyBoard.setMode(2);
                } else if (xKeyBoard.getMode() == 2) {
                    xKeyBoard.setMode(1);
                }
                break;
            case R.id.Back:
                ClientUtil.isOnCall = false;
                FragmentUtil.remove(this);
                FragmentUtil.show(backFragment);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void requestRoom() {
        if (SPUtils.getInstance().getBoolean(Constants.SERVER_CONNECT_STATS, false)) {
            if (SPUtils.getInstance().getInt("CALL_MODE", 0) == 0) {
                HashMap<String, Object> map = new HashMap<>();
                Message message = new Message();
                message.setMsgId(1);
                message.setCode(301);
                map.put("deviceId", room);
                message.setData(map);
                ClientUtil.requestMessage(message);
            } else {
                String default_call_number = SPUtils.getInstance().getString("DEFAULT_CALL_NUMBER");
                HashMap<String, Object> map = new HashMap<>();
                Message message = new Message();
                message.setMsgId(1);
                message.setCode(301);
                map.put("deviceId", default_call_number);
                message.setData(map);
                ClientUtil.requestMessage(message);
            }
        } else {
            ToastUtils.showShortToast("没有连接服务器或网络不可用");
            if (ClientMain.getChannel() != null && ClientMain.getChannel().isActive()) {
                ClientMain.getChannel().close();
            }
        }
    }

    private void checkPermissions() {
        permissionChecker.verifyPermissions(getActivity(), RequiredPermissions, new PermissionChecker.VerifyPermissionsCallback() {
            @Override
            public void onPermissionAllGranted() {
                requestRoom();
            }

            @Override
            public void onPermissionDeny(String[] permissions) {
                Toast.makeText(getActivity(), "Please grant required permissions.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onResume() {
        xEditText.clear();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        ClientUtil.isOnCall = false;
        getActivity().unregisterReceiver(snReceiver);
        if (dialog != null) {
            dialog.dismiss();
        }
        if (StarRtcintent != null) {
            getActivity().stopService(StarRtcintent);
        }
        super.onDestroy();
    }

    public void sendMessage(org.json.JSONObject payload) throws JSONException {
        Message message = new Message();
        HashMap<String, Object> map = new HashMap<>();
        map.put("sender", ClientUtil.Sender);
        map.put("receiver", ClientUtil.Receiver);

        if (payload != null) {
            map.put("forward", payload.toString());
        } else {
            org.json.JSONObject jsonObject = new org.json.JSONObject();
            map.put("forward", jsonObject.toString());
        }
        message.setMsgId(1);
        message.setData(map);
        message.setCode(302);
        ClientUtil.requestMessage(message);
    }

    private  void startJoinRoomInternal(final int roomId, final String userId) {
        final Intent intent = new Intent(getActivity(), TRTCActivity.class);
        int sdkAppId = GenerateTestUserSig.SDKAPPID;
        String userSig = GenerateTestUserSig.genTestUserSig(userId);
        intent.putExtra(TRTCVideoRoomActivity.KEY_SDK_APP_ID, sdkAppId);
        intent.putExtra(TRTCVideoRoomActivity.KEY_USER_SIG, userSig);

        // roomId userId
        intent.putExtra(TRTCVideoRoomActivity.KEY_ROOM_ID, roomId);
        intent.putExtra(TRTCVideoRoomActivity.KEY_USER_ID, userId);
        saveUserInfo(String.valueOf(roomId), userId);


        // 视频通话
        intent.putExtra(TRTCVideoRoomActivity.KEY_APP_SCENE, TRTCCloudDef.TRTC_APP_SCENE_VIDEOCALL);
        intent.putExtra("snList", snList);
        // 是否使用外部采集
        boolean isCustomVideoCapture = false;
        intent.putExtra(TRTCVideoRoomActivity.KEY_CUSTOM_CAPTURE, isCustomVideoCapture);
        intent.putExtra(TRTCVideoRoomActivity.KEY_VIDEO_FILE_PATH, mVideoFile);
        // 接收模式
        boolean mReceivedVideo = true;
        boolean mReceivedAudio = true;
        //音量类型
        //自动
//        mAudioVolumeType = TRTCCloudDef.TRTCSystemVolumeTypeAuto;
        //媒体
//        mAudioVolumeType = TRTCCloudDef.TRTCSystemVolumeTypeMedia;
        //通话
        int mAudioVolumeType = TRTCCloudDef.TRTCSystemVolumeTypeVOIP;
        //不选
//      mAudioVolumeType = -1;

        intent.putExtra(KEY_AUDIO_VOLUMETYOE, mAudioVolumeType);

        //免提
        intent.putExtra(KEY_AUDIO_HANDFREEMODE, true);
        intent.putExtra(KEY_RECEIVED_VIDEO, mReceivedVideo);
        intent.putExtra(KEY_RECEIVED_AUDIO, mReceivedAudio);
        startActivityForResult(intent, 4321);
    }

    private void saveUserInfo(String roomId, String userId) {
        try {
            SharedPreferences shareInfo = getActivity().getSharedPreferences("per_data", 0);
            SharedPreferences.Editor editor = shareInfo.edit();
            editor.putString("userId", userId);
            editor.putString("roomId", roomId);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkPermission() {
        times++;
        final List<String> permissionsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((getActivity().checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.ACCESS_NETWORK_STATE);
            if ((getActivity().checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.READ_PHONE_STATE);
            if ((getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if ((getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            if ((getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.CAMERA);
            if ((getActivity().checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.BLUETOOTH);
            if ((getActivity().checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.RECORD_AUDIO);
            if (permissionsList.size() != 0) {
                if (times == 1) {
                        requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                            REQUEST_PHONE_PERMISSIONS);
                } else {
                    new AlertDialog.Builder(getContext())
                            .setCancelable(true)
                            .setTitle("提示")
                            .setMessage("获取不到授权，APP将无法正常使用，请允许APP获取权限！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                                REQUEST_PHONE_PERMISSIONS);
                                    }
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            ClientUtil.isOnCall = false;
                            FragmentUtil.remove(CallFragment.this);
                            FragmentUtil.show(backFragment);
                        }
                    }).show();
                }
            } else {
                initSDK();
            }
        } else {
            initSDK();
        }
    }

    private void initSDK() {
        startService();
    }

    private void startService() {
        StarRtcintent = new Intent(getActivity(), KeepLiveService.class);
        getActivity().startService(StarRtcintent);
    }

    private class SnReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            snList = new ArrayList<String>();
            JSONObject data = JSON.parseObject(intent.getStringExtra("data"));
            if (data.containsKey("obj")) {
                int roomid = (int) ((Math.random() * 9 + 1) * 100000);
                JSONArray obj = data.getJSONArray("obj");
                for (int i = 0; i < obj.size(); i++) {
                    MessageBean messageBean = new MessageBean("roomid", roomid + "", System.currentTimeMillis());
                    messageBean.setSn(ClientUtil.Sender);
                    IosToken iosToken = FacexDatabase.getInstance(getContext()).getIosTokenDao().listTokenBySn(obj.getString(i));
                    if (iosToken != null) {
                        SendUtils.sendMsgIos(messageBean, iosToken.getTokne());
                    } else {
                        SendUtils.sendMsg(messageBean, obj.getString(i));
                    }
                    snList.add(obj.getString(i));
                }
                isCaller = true;
                MainActivity.setSnList(snList);
                MainActivity.stopSceenControlService();
                startJoinRoomInternal(roomid, ClientUtil.Sender);
            } else {
                ToastUtils.showShortToast("对方离线或房号没有绑定可视对讲机");
            }
        }
    }
}
