package com.yxkj.facexradix.ui.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.idl.face.main.FaceManager;
import com.baidu.idl.face.main.api.FaceApi;
import com.baidu.idl.main.facesdk.utils.FileUitls;
import com.yxdz.commonlib.base.BaseFragment;
import com.yxdz.commonlib.util.DeviceUtil;
import com.yxdz.commonlib.util.NetworkUtils;
import com.yxdz.commonlib.util.SPUtils;
import com.yxkj.facexradix.Constants;
import com.yxkj.facexradix.R;
import com.yxkj.facexradix.room.FacexDatabase;
import com.yxkj.facexradix.ui.activity.MainActivity;
import com.yxkj.facexradix.utils.FragmentUtil;
import com.yxkj.facexradix.view.CallModeDialog;
import com.yxkj.facexradix.view.FaceConfigDialog;
import com.yxkj.facexradix.view.LockDialog;
import com.yxkj.facexradix.view.ModeSettingDialog;
import com.yxkj.facexradix.view.RestartDialog;
import com.yxkj.facexradix.view.ServerIpDialog;
import com.yxkj.facexradix.view.XDiaLog;

import java.io.File;


/**
 * @PackageName: com.yxdz.facex.ui.fragment
 * @Desription:
 * @Author: Dreamcoding
 * @CreatDate: 2019/2/14 14:56
 */
public class ConfigFragment extends BaseFragment implements View.OnClickListener {

    private ImageButton ibtnConfigBack;
    private TextView tvDeviceSn;
    private TextView tvDeviceIp;
    private TextView tvServerIp;
    private TextView tvDeviceVoice;
    private TextView tvConfigUserCount;
    private TextView tvConfigFaceCount;
    private TextView tvConfigCardCount;
    private TextView tvConfigUserOfflineRecordCount;
    private ImageButton valueLess;
    private ImageButton valuePlus;
    private TextView soundValue;
    private AudioManager audioManager;
    private Switch cameraswitch;
    private RelativeLayout ipSettingLayout;
    private TextView tvCopyrightx;
    private int flag = 0;
    private long currentTime;
    private TextView tvDeviceMode;
    private TextView modeText;
    private Switch outputswitch;
    private XDiaLog xDiaLog;
    private ModeSettingDialog modeSettingDialog;
    private RelativeLayout serverIpLayout;
    private String serverIp;
    private String serverPort;
    private ServerIpDialog serverIpDialog;
    private RelativeLayout rvReset;
    private Switch alarmswitch;


    private TextView tvProjectType;
    private RelativeLayout rlPrejectType;

    private int projectType;
    private RelativeLayout callModeSetting;
    private TextView tvCallTip;
    private CallModeDialog callModeDialog;
    private Switch wakeSwitch;
    private TextView locksetting;
    private View locksettinglayout;
    private LockDialog lockDialog;
    private RestartDialog restartDialog;
    private FaceConfigDialog faceConfigDialog;
    private View faceconfig;


    @Override
    public int getLayoutRes() {
        return R.layout.fragment_config;
    }

    @Override
    public void onModel() {

        ibtnConfigBack = rootView.findViewById(R.id.ibtnConfigBack);
        tvCallTip = rootView.findViewById(R.id.tvCallTip);
        callModeSetting = rootView.findViewById(R.id.call_mode_setting);
        tvDeviceSn = rootView.findViewById(R.id.tvDeviceSn);
        tvDeviceIp = rootView.findViewById(R.id.tvDeviceIp);
        tvServerIp = rootView.findViewById(R.id.tvServerIp);
        tvDeviceVoice = rootView.findViewById(R.id.tvDeviceVoice);
        tvCopyrightx = rootView.findViewById(R.id.textView9);
        modeText = rootView.findViewById(R.id.modeText);
        ipSettingLayout = rootView.findViewById(R.id.ipsettinglayout);
        tvConfigUserCount = rootView.findViewById(R.id.tvConfigUserCount);
        tvConfigCardCount = rootView.findViewById(R.id.tvConfigCardCount);
        tvConfigFaceCount = rootView.findViewById(R.id.tvConfigUserFaceCount);
        tvConfigUserOfflineRecordCount = rootView.findViewById(R.id.tvConfigUserOfflineRecordCount);
        valueLess = rootView.findViewById(R.id.soundValueLess);
        valuePlus = rootView.findViewById(R.id.soundValuePlus);
        soundValue = rootView.findViewById(R.id.soundvalue);
        cameraswitch = rootView.findViewById(R.id.cameraswitch);
        tvDeviceMode = rootView.findViewById(R.id.tvDeviceMode);
        serverIpLayout = rootView.findViewById(R.id.serverIpLayout);
        rvReset = rootView.findViewById(R.id.restart);
        faceconfig = rootView.findViewById(R.id.faceconfig);
        alarmswitch = rootView.findViewById(R.id.warmswitch);
        rlPrejectType = rootView.findViewById(R.id.rlPrejectType);
        tvProjectType = rootView.findViewById(R.id.tvProjectType);
        wakeSwitch = rootView.findViewById(R.id.wakeSwitch);
        locksetting = rootView.findViewById(R.id.locksetting);
        locksettinglayout = rootView.findViewById(R.id.locksettinglayout);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setUpgradeView(false);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onData(Bundle savedInstanceState) {
        serverIpLayout.setOnClickListener(this);
        switch (SPUtils.getInstance().getString(Constants.DEVICE_OPEATOR_MODE, "composite")) {
            case "11":
                tvDeviceMode.setText("卡+人脸");
                break;
            case "12":
                tvDeviceMode.setText("卡+密码");
                break;
            case "13":
                tvDeviceMode.setText("卡+人脸+密码");
                break;
            case "14":
                tvDeviceMode.setText("人脸+密码");
                break;
            default:
                tvDeviceMode.setText("多选模式");
                break;

        }


        ibtnConfigBack.setOnClickListener(this);
        tvDeviceSn.setText(DeviceUtil.getLocalMacAddress(getContext()));
        tvDeviceIp.setText(NetworkUtils.getIPAddress(true));
        serverIp = SPUtils.getInstance().getString(Constants.SERVER_ADDRESS, Constants.DEFAULT_SERVER_ADDRESS);
        serverPort = SPUtils.getInstance().getString(Constants.SERVER_ADDRESS_PORT, Constants.DEFAULT_SERVER_ADDRESS_PORT);
        tvServerIp.setText(serverIp + ":" + serverPort);
        int ttsMode = SPUtils.getInstance().getInt(Constants.TTS_MODE, Constants.DEFAULT_TTS_MODE);
        if (ttsMode == 0) {
            tvDeviceVoice.setText("关闭");
        } else if (ttsMode == 1) {
            tvDeviceVoice.setText("开启");
        }

        if (SPUtils.getInstance().getBoolean("LICENSE_IS_ACTIVATION")) {
            tvConfigUserCount.setText(FaceManager.getCount() + "");

            tvConfigCardCount.setText(FaceManager.getCount() + "");
            File faceDirectory = FileUitls.getFaceDirectory();
            tvConfigFaceCount.setText(faceDirectory.listFiles().length + "");
        }

        faceconfig.setOnClickListener(this);
        int recordCount = FacexDatabase.getInstance(getContext()).getRecord().count();
        tvConfigUserOfflineRecordCount.setText(recordCount + "");

        audioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
        valueLess.setOnClickListener(this);
        valuePlus.setOnClickListener(this);
        int streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        soundValue.setText(streamVolume + "");


        rootView.findViewById(R.id.modesettinglayout).setOnClickListener(this);


        ipSettingLayout.setOnClickListener(this);
        GetIpaddress getIpaddress = new GetIpaddress();
        IntentFilter intentFilter = new IntentFilter(Constants.BROADCAST_RECEIVER_NETWORK_NOTIC);
        mActivity.registerReceiver(getIpaddress, intentFilter);

        tvCopyrightx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == 0) {
                    currentTime = System.currentTimeMillis();
                } else if (flag == 5) {
                    flag = 0;
                    if ((System.currentTimeMillis() - currentTime) < 5000) {
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(intent);
                    } else {
                        flag = 0;
                    }
                }
                flag++;
            }
        });

        outputswitch = rootView.findViewById(R.id.outputswitch);
        int aBoolean = SPUtils.getInstance().getInt(Constants.DEVICE_CTRL_MODE, Constants.DEFAULT_MODE_US_CONTROL);
        if (aBoolean == 0) {
            modeText.setText("自继电器控制模式");
            outputswitch.setChecked(false);
            locksettinglayout.setVisibility(View.VISIBLE);
        } else {
            modeText.setText("外接控制器模式");
            outputswitch.setChecked(true);
            locksettinglayout.setVisibility(View.GONE);
        }

        if (SPUtils.getInstance().getInt("DEVICE_WAKE_MODE", 1) == 0) {
            wakeSwitch.setChecked(false);
        } else {
            wakeSwitch.setChecked(true);
        }

        locksetting.setText((SPUtils.getInstance().getInt("Lock_time") / 1000) + "秒");

        callModeSetting.setOnClickListener(this);


        outputswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(getContext(), "外接控制器模式", Toast.LENGTH_SHORT).show();
                modeText.setText("外接控制器模式");
                SPUtils.getInstance().put(Constants.DEVICE_CTRL_MODE, 1);
                locksettinglayout.setVisibility(View.GONE);
            } else {
                modeText.setText("自继电器控制模式");
                Toast.makeText(getContext(), "自继电器控制模式", Toast.LENGTH_SHORT).show();
                locksettinglayout.setVisibility(View.VISIBLE);
                SPUtils.getInstance().put(Constants.DEVICE_CTRL_MODE, 0);

            }
        });

        wakeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(getContext(), "单摄像头唤醒模式", Toast.LENGTH_SHORT).show();
                SPUtils.getInstance().put("DEVICE_WAKE_MODE", 1);
                Intent intent = new Intent("WAKE_UP_MODE");
                intent.putExtra("type", 1);
                getActivity().sendBroadcast(intent);
            } else {
                Toast.makeText(getContext(), "双摄像头唤醒模式", Toast.LENGTH_SHORT).show();
                SPUtils.getInstance().put("DEVICE_WAKE_MODE", 0);
                Intent intent = new Intent("WAKE_UP_MODE");
                intent.putExtra("type", 0);
                getActivity().sendBroadcast(intent);
            }
        });

        rvReset.setOnClickListener(this);

        if (SPUtils.getInstance().getInt(Constants.DEVICE_ALARM, 1) == 1) {
            alarmswitch.setChecked(true);
        } else {
            alarmswitch.setChecked(false);
        }

        alarmswitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(getContext(), "开启警告输出", Toast.LENGTH_SHORT).show();
                SPUtils.getInstance().put(Constants.DEVICE_ALARM, 1);
            } else {
                Toast.makeText(getContext(), "关闭警告输出", Toast.LENGTH_SHORT).show();
                SPUtils.getInstance().put(Constants.DEVICE_ALARM, 0);
            }
        });


        cameraswitch.setChecked(SPUtils.getInstance().getBoolean(Constants.CAMERA_MODE, false));
        cameraswitch.setOnCheckedChangeListener((buttonView, isChecked) -> SPUtils.getInstance().put(Constants.CAMERA_MODE, isChecked));

        projectType = SPUtils.getInstance().getInt(Constants.PROJECT_TYPE, Constants.PROJECT_JIANGSHENG);
        if (projectType == Constants.PROJECT_JIANGSHENG) {
            tvProjectType.setText("JIANGSHENG");
        } else if (projectType == Constants.PROJECT_STANDARD) {
            tvProjectType.setText("STANDARD");
        }
        rlPrejectType.setOnClickListener(this);


        if (SPUtils.getInstance().getInt("CALL_MODE", 0) == 0) {
            tvCallTip.setText("房间号模式");
        } else if(SPUtils.getInstance().getInt("CALL_MODE", 0) == 1) {
            tvCallTip.setText("门铃模式");
        }else{
            tvCallTip.setText("社区模式");
        }

        locksettinglayout.setOnClickListener(this);
    }


    @Override
    public void onDestroy() {
        if (xDiaLog != null) {
            xDiaLog.dismiss();
        }
        if (modeSettingDialog != null) {
            modeSettingDialog.dismiss();
        }
        if (serverIpDialog != null) {
            serverIpDialog.dismiss();
        }
        if (callModeDialog != null) {
            callModeDialog.dismiss();
        }
        if (restartDialog != null) {
            restartDialog.dismiss();
        }
        if (faceConfigDialog != null) {
            faceConfigDialog.dismiss();
        }

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtnConfigBack:
                FragmentUtil.remove(this);
                break;
            case R.id.soundValuePlus:
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
                        AudioManager.FX_FOCUS_NAVIGATION_UP);
                soundValue.setText(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + "");
                break;
            case R.id.soundValueLess:
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,
                        AudioManager.FX_FOCUS_NAVIGATION_UP);
                soundValue.setText(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + "");
                break;
            case R.id.ipsettinglayout:
                xDiaLog = new XDiaLog(mActivity, tvDeviceIp);
                xDiaLog.show();
                Window window1 = xDiaLog.getWindow();
                window1.getDecorView().setMinimumWidth(getResources().getDisplayMetrics().widthPixels);
                window1.getDecorView().setMinimumHeight(getResources().getDisplayMetrics().heightPixels);
                window1.getDecorView().setBackgroundColor(Color.TRANSPARENT);
                break;
            case R.id.modesettinglayout:
                modeSettingDialog = new ModeSettingDialog(mActivity, tvDeviceMode);
                modeSettingDialog.show();
                break;
            case R.id.serverIpLayout:
                serverIpDialog = new ServerIpDialog(mActivity, tvServerIp);
                serverIpDialog.show();
                Window window = serverIpDialog.getWindow();
                window.getDecorView().setMinimumWidth(getResources().getDisplayMetrics().widthPixels);
                window.getDecorView().setMinimumHeight(getResources().getDisplayMetrics().heightPixels);
                window.getDecorView().setBackgroundColor(Color.TRANSPARENT);
                break;
            case R.id.restart:
                restartDialog = new RestartDialog(mActivity);
                restartDialog.show();
                break;
            case R.id.faceconfig:
                faceConfigDialog = new FaceConfigDialog(mActivity);
                faceConfigDialog.show();
                break;
            case R.id.rlPrejectType:
                if (projectType == Constants.PROJECT_STANDARD) {
                    tvProjectType.setText("JIANGSHENG");
                    projectType = Constants.PROJECT_JIANGSHENG;
                    SPUtils.getInstance().put(Constants.PROJECT_TYPE, Constants.PROJECT_JIANGSHENG);
                } else if (projectType == Constants.PROJECT_JIANGSHENG) {
                    tvProjectType.setText("STANDARD");
                    projectType = Constants.PROJECT_STANDARD;
                    SPUtils.getInstance().put(Constants.PROJECT_TYPE, Constants.PROJECT_STANDARD);
                }
                break;
            case R.id.call_mode_setting:
                callModeDialog = new CallModeDialog(mActivity, tvCallTip);
                callModeDialog.show();
                Window window2 = callModeDialog.getWindow();
                window2.getDecorView().setMinimumWidth(getResources().getDisplayMetrics().widthPixels);
                window2.getDecorView().setMinimumHeight(getResources().getDisplayMetrics().heightPixels);
                window2.getDecorView().setBackgroundColor(Color.TRANSPARENT);
                break;
            case R.id.locksettinglayout:
                lockDialog = new LockDialog(mActivity, locksetting);
                lockDialog.show();
                Window window3 = lockDialog.getWindow();
                window3.getDecorView().setMinimumWidth(getResources().getDisplayMetrics().widthPixels);
                window3.getDecorView().setMinimumHeight(getResources().getDisplayMetrics().heightPixels);
                window3.getDecorView().setBackgroundColor(Color.TRANSPARENT);
            default:
                break;
        }
    }


    class GetIpaddress extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            tvDeviceIp.setText(NetworkUtils.getIPAddress(true));
        }
    }

}
